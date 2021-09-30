package com.subra.aem.rjs.mailer.internal.helpers;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.subra.aem.rjs.core.utils.MongoDBUtils;
import com.subra.aem.rjs.mailer.Template;
import com.subra.aem.rjs.mailer.services.MailerGatewayService;
import com.subra.aem.rjs.mailer.utils.MailerUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.utils.RJSDateTimeUtils;

import javax.activation.DataSource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.*;
import java.util.stream.Collectors;

public class MailerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailerHelper.class);
    private static final String MSG_INVALID_RECIPIENTS = "Invalid Recipients";

    private static int connectTimeout = MailerUtils.DEFAULT_CONNECT_TIMEOUT;
    private static int soTimeout = MailerUtils.DEFAULT_SOCKET_TIMEOUT;

    private static String draftTemplatesIDPrefix = MailerUtils.DEFAULT_DRAFT_TEMPLATES_ID_PREFIX;
    private static String approvedTemplatesIDPrefix = MailerUtils.DEFAULT_APPROVED_TEMPLATES_ID_PREFIX;

    private static String draftTemplatesPath = MailerUtils.DEFAULT_DRAFT_TEMPLATES_PATH;
    private static String approvedTemplatesPath = MailerUtils.DEFAULT_APPROVED_TEMPLATES_PATH;

    private MailerHelper() {
        throw new UnsupportedOperationException();
    }

    public static void setTimeouts(int soTimeout, int connectTimeout) {
        MailerHelper.soTimeout = soTimeout;
        MailerHelper.connectTimeout = connectTimeout;
    }

    public static void setTemplateIDPrefixes(String draftTemplatesIDPrefix, String approvedTemplatesIDPrefix) {
        MailerHelper.draftTemplatesIDPrefix = draftTemplatesIDPrefix;
        MailerHelper.approvedTemplatesIDPrefix = approvedTemplatesIDPrefix;
    }

    public static void setTemplatePaths(String draftTemplatesPath, String approvedTemplatesPath) {
        MailerHelper.draftTemplatesPath = draftTemplatesPath;
        MailerHelper.approvedTemplatesPath = approvedTemplatesPath;
    }

    public static String getTemplateId(Template template) {
        StringBuilder idBuilder = new StringBuilder();
        idBuilder.append(template.isDraft() ? draftTemplatesIDPrefix : approvedTemplatesIDPrefix)
                .append(template.getName());
        return idBuilder.toString();
    }

    public static <T extends Email> Email getEmail(String content, Class<T> type) throws EmailException {
        if (type.equals(HtmlEmail.class)) {
            HtmlEmail email = new HtmlEmail();
            email.setHtmlMsg(content);
            return email;
        } else if (type.equals(SimpleEmail.class)) {
            SimpleEmail email = new SimpleEmail();
            email.setMsg(content);
            return email;
        }
        return new SimpleEmail();
    }

    public static InternetAddress[] convertToInternetAddresses(String... recipients) {
        List<InternetAddress> addresses = new ArrayList<>(recipients.length);
        Arrays.stream(recipients).forEach(recipient -> {
            try {
                addresses.add(new InternetAddress(recipient));
            } catch (AddressException e) {
                LOGGER.warn("Invalid email address {} passed to sendEmail(). Skipping.", recipient);
            }
        });
        return addresses.toArray(new InternetAddress[addresses.size()]);
    }

    public static Class<? extends Email> getMailType(String templatePath, boolean hasAttachments) {
        return (templatePath != null && templatePath.endsWith(".html")) || hasAttachments ? HtmlEmail.class : SimpleEmail.class;
    }

    public static Email createEmail(final String content, Map<String, String> emailParams, Class<? extends Email> mailType) throws EmailException {
        Email email = getEmail(content, mailType);
        if (emailParams.containsKey(MailerUtils.SENDER_EMAIL_ADDRESS) && emailParams.containsKey(MailerUtils.SENDER_NAME))
            email.setFrom(emailParams.get(MailerUtils.SENDER_EMAIL_ADDRESS), emailParams.get(MailerUtils.SENDER_NAME));
        else if (emailParams.containsKey(MailerUtils.SENDER_EMAIL_ADDRESS))
            email.setFrom(emailParams.get(MailerUtils.SENDER_EMAIL_ADDRESS));

        if (connectTimeout > 0) email.setSocketConnectionTimeout(connectTimeout);

        if (soTimeout > 0) email.setSocketTimeout(soTimeout);

        if (emailParams.containsKey(MailerUtils.SUBJECT)) email.setSubject(emailParams.get(MailerUtils.SUBJECT));

        if (emailParams.containsKey(MailerUtils.CC)) email.addCc(emailParams.get(MailerUtils.CC));

        if (emailParams.containsKey(MailerUtils.BCC)) email.addBcc(emailParams.get(MailerUtils.BCC));

        if (emailParams.containsKey(MailerUtils.BOUNCE_ADDRESS))
            email.setBounceAddress(emailParams.get(MailerUtils.BOUNCE_ADDRESS));

        return email;
    }

    public static Map<String, Object> sendEmail(final MailerGatewayService messageGateway, Template template2, Map<String, String> emailParams, Map<String, DataSource> attachments, String... recipients) {
        return sendEmail(messageGateway, template2.getMessage(), emailParams, attachments, template2.getPath(), recipients);
    }

    public static Map<String, Object> sendEmail(final MailerGatewayService messageGateway, String messageContent, Map<String, String> emailParams, Map<String, DataSource> attachments, final String templatePath, String... recipients) {
        final String transactionId = UUID.randomUUID().toString();
        List<InternetAddress> failureList = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();
        boolean status = false;
        response.put("transactionId", transactionId);
        InternetAddress[] addresses = convertToInternetAddresses(recipients);
        if (addresses == null || addresses.length <= 0) throw new IllegalArgumentException(MSG_INVALID_RECIPIENTS);

        final String content = getEmailContent(messageContent, emailParams);
        for (final InternetAddress address : addresses) {
            try {
                boolean hasAttachments = attachments != null && attachments.size() > 0;
                Email email = createEmail(content, emailParams, getMailType(templatePath, hasAttachments));
                email.setTo(Collections.singleton(address));
                if (hasAttachments) {
                    for (Map.Entry<String, DataSource> entry : attachments.entrySet()) {
                        ((HtmlEmail) email).attach(entry.getValue(), entry.getKey(), null);
                    }
                }
                status = messageGateway.send(email);
            } catch (Exception e) {
                failureList.add(address);
                LOGGER.error("Error sending email to [ {} ]", address, e);
            }
        }
        //updateRecord(template, transactionId, content, emailParams, status)
        response.put("Status", status ? "SUCCESS" : "FAILURE");
        response.put("failureList", CollectionUtils.emptyIfNull(failureList).stream().map(InternetAddress::toString).collect(Collectors.toList()));
        return response;
    }

    public static String getEmailContent(final String content, Map<String, String> params) {
        return new StrSubstitutor(StrLookup.mapLookup(params)).replace(content);
    }

    private static String updateRecord(Template template, String transactionId, String content, Map<String, String> params, boolean status) {
        MongoClient client = null;
        try {
            client = MongoDBUtils.createConnection("localhost", 27017, null, null);
            MongoCollection<Document> collection = MongoDBUtils.getMongoCollection(client, "subra", "mailer");
            Document doc = new Document();
            doc.put("transactionId", transactionId);
            doc.put("Status", status ? "SUCCESS" : "FAILURE");
            doc.put("timestamp", RJSDateTimeUtils.localDateTimeAtUTC());
            doc.put("templateId", template.getId());
            doc.put("params", params);
            doc.put("lookups", template.getLookUps());
            doc.put("message", content);
            collection.insertOne(doc);
        } catch (Exception e) {
            LOGGER.error("Error Inserting record to mongoDB", e);
        } finally {
            MongoDBUtils.closeConnection(client);
        }
        return transactionId;
    }

    public String getDraftTemplatesIDPrefix() {
        return draftTemplatesIDPrefix;
    }

    public String getApprovedTemplatesIDPrefix() {
        return approvedTemplatesIDPrefix;
    }

    public String getDraftTemplatesPath() {
        return draftTemplatesPath;
    }

    public String getApprovedTemplatesPath() {
        return approvedTemplatesPath;
    }

}
