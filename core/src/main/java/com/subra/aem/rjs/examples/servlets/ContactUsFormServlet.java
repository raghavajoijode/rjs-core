package com.subra.aem.rjs.examples.servlets;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.jcr.api.SlingRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.Servlet;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=ContactUsFormServlet Demo Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/contactform"})
@Designate(ocd = ContactUsFormServlet.Config.class)
public class ContactUsFormServlet extends SlingAllMethodsServlet {
    public static final String BR_BR = "<br><br>";
    public static final String STATUS = "status";
    public static final String ERROR = "error";
    public static final String INQ_TYPE = "inqType";
    public static final String PHONE = "phone";
    public static final String OTHER_INQ = "othInq";
    public static final String JOB_TITLE = "jobTitle";
    public static final String FNAME = "firstName";
    public static final String LNAME = "lastName";
    public static final String EMAIL = "email";
    public static final String COUNTRY = "country";
    public static final String IND_BLG = "indBlg";
    public static final String DESC_INQ = "descInq";
    public static final String COMP_NAME = "compName";
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactUsFormServlet.class);
    private static final String DEFAULT_MAIL_PROTOCOL = "smtp";
    private static final String DEFAULT_PORT_NUMBER = "587";
    private static final String DEFAULT_MAIL_HOST = "Outlook.office365.com";
    private static final String DEFAULT_MAIL_FROM = "usha.dandu@abc.com";
    private static final String DEFAULT_MAIL_TO = "usha.dandu@abc.com";
    private static final String DEFAULT_USERNAME = "us310476@abc.com";
    private static final String DEFAULT_PWORD = "Purple@1234";
    private static final String PROP_MAIL_PROTOCOL = "mail.protocol";
    private static final String PROP_MAIL_PORTNUMBER = "mail.portnumber";
    private static final String PROP_MAIL_HOST = "mail.host";
    private static final String PROP_MAIL_FROM = "mail.from";
    private static final String PROP_MAIL_TO = "mail.to";
    private static final String ANALYSTRELATIONS_PROP_MAIL_TO = "analyst.relations.mail.to";
    private static final String ALLIANCES_PROP_MAIL_TO = "alliances.mail.to";
    private static final String CAREER_SEEKERS_PROP_MAIL_TO = "career.seekers.mail.to";
    private static final String INVESTOR_RELATIONS_PROP_MAIL_TO = "investor.relations.mail.to";
    private static final String PUBLIC_RELATIONS_PROP_MAIL_TO = "public.relations.mail.to";
    private static final String REQUEST_SERVICES_PROP_MAIL_TO = "request.services.mail.to";
    private static final String OTHERS_PROP_MAIL_TO = "others.mail.to";
    private static final String PROP_USERNAME = "mail.user";
    private static final String PROP_PWORD = "mail.password";
    private static final String DB_HOST = "db.host";
    private static final String DB_USER = "db.user";
    private static final String DB_PWORD = "db.password";
    @Reference
    private transient SlingRepository repository;
    private String mailProtocol;
    private String portNumber;
    private String hostNumber;
    private String from;
    private String to;
    private String anarelto;
    private String allito;
    private String carseekto;
    private String investrelto;
    private String pubrelto;
    private String reqserto;
    private String othto;
    private String userName;
    private String password;
    private String dbHost;
    private String dbUser;
    private String dbPwd;

    public static String getParameter(String paramenter, SlingHttpServletRequest request) {
        return request.getParameter(paramenter) != null ? request.getParameter(paramenter) : StringUtils.EMPTY;
    }

    @Activate
    protected void activate(Map<String, Object> properties) {
        LOGGER.info("inside activate");

        this.mailProtocol = PropertiesUtil.toString(properties.get(PROP_MAIL_PROTOCOL), DEFAULT_MAIL_PROTOCOL);
        this.portNumber = PropertiesUtil.toString(properties.get(PROP_MAIL_PORTNUMBER), DEFAULT_PORT_NUMBER);
        this.hostNumber = PropertiesUtil.toString(properties.get(PROP_MAIL_HOST), DEFAULT_MAIL_HOST);
        this.from = PropertiesUtil.toString(properties.get(PROP_MAIL_FROM), DEFAULT_MAIL_FROM);
        this.to = PropertiesUtil.toString(properties.get(PROP_MAIL_TO), DEFAULT_MAIL_TO);
        this.anarelto = PropertiesUtil.toString(properties.get(ANALYSTRELATIONS_PROP_MAIL_TO), "");
        this.allito = PropertiesUtil.toString(properties.get(ALLIANCES_PROP_MAIL_TO), "");
        this.carseekto = PropertiesUtil.toString(properties.get(CAREER_SEEKERS_PROP_MAIL_TO), "");
        this.investrelto = PropertiesUtil.toString(properties.get(INVESTOR_RELATIONS_PROP_MAIL_TO), "");
        this.pubrelto = PropertiesUtil.toString(properties.get(PUBLIC_RELATIONS_PROP_MAIL_TO), "");
        this.reqserto = PropertiesUtil.toString(properties.get(REQUEST_SERVICES_PROP_MAIL_TO), "");
        this.othto = PropertiesUtil.toString(properties.get(OTHERS_PROP_MAIL_TO), "");
        this.userName = PropertiesUtil.toString(properties.get(PROP_USERNAME), DEFAULT_USERNAME);
        this.password = PropertiesUtil.toString(properties.get(PROP_PWORD), DEFAULT_PWORD);
        this.dbHost = PropertiesUtil.toString(properties.get(DB_HOST), "");
        this.dbUser = PropertiesUtil.toString(properties.get(DB_USER), "");
        this.dbPwd = PropertiesUtil.toString(properties.get(DB_PWORD), "");
        LOGGER.info("inside end activate");
    }

    public void bindRepository(SlingRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        LOGGER.info("inside contactus DoPost");
        String recapcharesponse = request.getParameter("g_recaptcha_response");
        JSONObject obj = new JSONObject();
        try {
            obj.put(STATUS, false);
            obj.put(ERROR, "Application error");
            // Get the submitted form data that is sent from the
            // CQ web page
            Map<String, String> inputData = new HashMap<>();
            getDecodedParameter(inputData, FNAME, request);
            getDecodedParameter(inputData, LNAME, request);
            getDecodedParameter(inputData, "organization", request);
            getDecodedParameter(inputData, EMAIL, request);
            getDecodedParameter(inputData, JOB_TITLE, request);
            getDecodedParameter(inputData, COMP_NAME, request);
            getDecodedParameter(inputData, INQ_TYPE, request);
            getDecodedParameter(inputData, OTHER_INQ, request);
            getDecodedParameter(inputData, "code", request);
            getDecodedParameter(inputData, COUNTRY, request);
            getDecodedParameter(inputData, PHONE, request);
            getDecodedParameter(inputData, IND_BLG, request);
            getDecodedParameter(inputData, DESC_INQ, request);
            getDecodedParameter(inputData, "refUrl", request);
            getDecodedParameter(inputData, "iPage", request);
            getDecodedParameter(inputData, "refParam", request);
            getDecodedParameter(inputData, "lastLandingPage", request);

            if (inputData.get(JOB_TITLE).equals(StringUtils.EMPTY)) {
                inputData.put(JOB_TITLE, "N/A");
            }
            if (inputData.get(PHONE).equals(StringUtils.EMPTY)) {
                inputData.put(PHONE, "N/A");
            }
            if (inputData.get(OTHER_INQ).equals(StringUtils.EMPTY)) {
                inputData.put(OTHER_INQ, "N/A");
            }
            switch (inputData.get(INQ_TYPE)) {
                case "Request for Services":
                    to = reqserto;
                    break;
                case "Alliances":
                    to = allito;
                    break;
                case "Analyst Relations":
                    to = anarelto;
                    break;
                case "Career Seekers":
                    to = carseekto;
                    break;
                case "Investor Relations":
                    to = investrelto;
                    break;
                case "Media and Public Relations":
                    to = pubrelto;
                    break;
                default:
                    to = othto;
                    break;
            }

            String[] recipientList = to.split(",");
            InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
            int counter = 0;
            for (String recipient : recipientList) {
                recipientAddress[counter] = new InternetAddress(recipient.trim());
                counter++;
            }

            Session session = getSession();
            session.setDebug(true);
            response.setContentType("text/html");

            makeQueryAndUpdateResponse(recapcharesponse, obj, inputData, recipientAddress, session);

        } catch (Exception e) {
            LOGGER.error("exception occured: ", e);

        }

        String jsonData = obj.toString();
        response.getWriter().write(jsonData);
    }

    private void makeQueryAndUpdateResponse(String recapcharesponse, JSONObject obj, Map<String, String> inputData, InternetAddress[] recipientAddress, Session session) throws SQLException, JSONException, MessagingException {
        if (validateRequest(recapcharesponse)) {
            try (Connection con = getConnection()) {
                obj.put(STATUS, queryExecution(con, inputData, recipientAddress, session));
            } catch (NullPointerException e) {
                obj.put(STATUS, false);
                obj.put(ERROR, "error while inserting data");
            }
        } else {
            obj.put(STATUS, false);
            obj.put(ERROR, "error while verifying captcha");
        }
    }

    private Session getSession() {
        Properties properties = System.getProperties();
        properties.put("mail.transport.protocol", mailProtocol);
        properties.setProperty("mail.smtp.host", hostNumber);
        properties.put("mail.smtp.port", portNumber);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.debug", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        return Session.getInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });
    }

    private void getDecodedParameter(final Map<String, String> inputData, final String fieldName, final SlingHttpServletRequest request) {
        try {
            inputData.put(fieldName, URLDecoder.decode(getParameter(fieldName, request), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            inputData.put(fieldName, StringUtils.EMPTY);
        }
    }

    private boolean queryExecution(Connection con, Map<String, String> inputs,
                                   InternetAddress[] recipientAddress, Session session)
            throws MessagingException, SQLException {
        boolean status;
        String query;
        javax.mail.Transport transport = session.getTransport(mailProtocol);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, recipientAddress);
        message.setSubject("Contact-" + inputs.get(INQ_TYPE));

        message.setContent(
                "<b>First Name</b> : " + inputs.get(FNAME) + BR_BR + "<b>Last Name</b> : " + inputs.get(LNAME) + BR_BR
                        + "<b>Email</b> : " + inputs.get(EMAIL) + BR_BR + "<b>Job Title</b> : " + inputs.get(JOB_TITLE) + BR_BR
                        + "<b>Company/Organization/School</b> : " + inputs.get("company") + BR_BR
                        + "<b>Company/Organization/School Name</b> : " + inputs.get(COMP_NAME) + BR_BR
                        + "<b>Inquiry Type</b> : " + inputs.get(INQ_TYPE) + BR_BR + "<b>Others Inquiry Type</b> : " + inputs.get(OTHER_INQ)
                        + BR_BR + "<b>Country</b> : " + inputs.get(COUNTRY) + BR_BR + "<b>Code</b> : " + inputs.get("code") + BR_BR
                        + "<b>Phone</b> : " + inputs.get(PHONE) + BR_BR + "<b>Industry Belongs To</b> : " + inputs.get(IND_BLG) + BR_BR
                        + "<b>Description Of Inquiry</b> : " + inputs.get(DESC_INQ) + BR_BR + "<b>Referrer URL</b> : " + inputs.get("refUrl")
                        + BR_BR + "<b>Referrer Parameters</b> : " + inputs.get("refParam") + BR_BR
                        + "<b>Initial Landing Page</b> : " + inputs.get("iPage") + BR_BR + "<b>Last Landing Page</b> : " + inputs.get("lPage"),
                "text/html");
        // Send message

        transport.connect();
        transport.sendMessage(message, message.getAllRecipients());
        con.setAutoCommit(false);

        query = "INSERT INTO custom_contactus_contact (contact_id ,contact_firstname,contact_lastname,contact_email,contact_jobtitle,contact_organization,contact_organization_name,contact_inquirytype,contact_othersinquirytype,contact_country,contact_code,contact_phoneno,contact_industrybelongsto,contact_descriptionofinquiry,contact_upload) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, 0);
            pstmt.setString(2, inputs.get(FNAME));
            pstmt.setString(3, inputs.get(LNAME));
            pstmt.setString(4, inputs.get(EMAIL));
            pstmt.setString(5, inputs.get(JOB_TITLE));
            pstmt.setString(6, inputs.get("company"));
            pstmt.setString(7, inputs.get(COMP_NAME));
            pstmt.setString(8, inputs.get(INQ_TYPE));
            pstmt.setString(9, inputs.get(OTHER_INQ));
            pstmt.setString(10, inputs.get(COUNTRY));
            pstmt.setString(11, inputs.get("code"));
            pstmt.setDouble(12, inputs.get(PHONE).equals("N/A") ? 0.0 : Double.parseDouble(inputs.get(PHONE)));
            pstmt.setString(13, inputs.get(IND_BLG));
            pstmt.setString(14, inputs.get(DESC_INQ));
            pstmt.setString(15, "");
            pstmt.executeUpdate();
        }

        con.commit();

        status = true;
        return status;
    }

    private boolean validateRequest(String recapcharesponse) {
        try {

            HttpClient client = new HttpClient();
            PostMethod post = new PostMethod("https://www.google.com/recaptcha/api/siteverify");
            NameValuePair[] data = {new NameValuePair("secret", "6LfXYvkSAAAAAHDCvykff72o6i2IsM1v5Vk6JXaO"),
                    new NameValuePair("response", recapcharesponse)};
            post.setRequestBody(data);
            client.executeMethod(post);

            String result = post.getResponseBodyAsString();
            post.releaseConnection();
            return result.contains("true");
        } catch (Exception e) {
            LOGGER.info("Exception occured while verifying capcha", e);
            return false;
        }
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        LOGGER.info("inside contactus Doget1");
        this.doPost(request, response);
        LOGGER.info("inside contactus Doget2");

    }

    protected Connection getConnection() {
        LOGGER.info("inside getconnection");
        String host = dbHost;
        String uname = dbUser;
        String pwd = dbPwd;
        try (Connection con = DriverManager.getConnection(host, uname, pwd);) { // Inject the DataSourcePool right here!
            LOGGER.info("after getting connection1");
            return con;
        } catch (Exception e) {
            LOGGER.error("connection error:", e);
        }
        return null;
    }

    @ObjectClassDefinition(name = "ACS AEM Commons - Site Map Servlet", description = "Page and Asset Site Map Servlet")
    public @interface Config {

        @AttributeDefinition(defaultValue = DEFAULT_MAIL_PROTOCOL, name = "Mail Transport Protocol", description = "Mail Transport Protocol to be used.")
        String mailProtocol();

        @AttributeDefinition(defaultValue = DEFAULT_PORT_NUMBER, name = "Mail Port Number", description = "Mail Port Number to be used.")
        String mailPortNumber();

        @AttributeDefinition(defaultValue = DEFAULT_MAIL_HOST, name = "Mail Host", description = "Mail Host to be used.")
        String mailHost();

        @AttributeDefinition(defaultValue = DEFAULT_MAIL_FROM, name = "Mail FROM", description = "Mail From to be used.")
        String mailFrom();

        @AttributeDefinition(defaultValue = DEFAULT_MAIL_TO, name = "Mail To", description = "Mail To to be used.")
        String mailTo();

        @AttributeDefinition(name = "Analyst Relations Mail To", description = "Analyst RelationsMail To to be used.")
        String analystRelationsMailTo();

        @AttributeDefinition(name = "Alliances Mail To", description = "Alliances Mail To to be used.")
        String alliancesMailTo();

        @AttributeDefinition(name = "Career Seekers Mail To", description = "Career Seekers Mail To to be used.")
        String careerSeekersMailTo();

        @AttributeDefinition(name = "Investor Relations Mail To", description = "Investor Relations Mail To to be used.")
        String investorRelationsMailTo();

        @AttributeDefinition(name = "Media and Public Relations Mail To", description = "Media and Public Relations Mail To to be used.")
        String publicRelationsMailTo();

        @AttributeDefinition(name = "Request for Services Mail To", description = "Request for Services Mail To to be used.")
        String requestServicesMailTo();

        @AttributeDefinition(name = "Others Mail To", description = "Others Mail To to be used.")
        String othersMailTo();

        @AttributeDefinition(defaultValue = DEFAULT_USERNAME, name = "Mail User Name", description = "Mail User Name To to be used.")
        String mailUser();

        @AttributeDefinition(defaultValue = DEFAULT_PWORD, name = "Mail Password", description = "Mail Password To to be used.")
        String mailPassword();

        @AttributeDefinition(name = "Host", description = "Host to be used.")
        String dbHost();

        @AttributeDefinition(name = "DB User Name", description = "Db User Name to be used.")
        String dbUser();

        @AttributeDefinition(name = "DB Password", description = "DB Password to be used.")
        String dbPassword();

    }
}
