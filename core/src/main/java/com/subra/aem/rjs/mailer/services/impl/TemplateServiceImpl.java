package com.subra.aem.rjs.mailer.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.subra.aem.rjs.core.UserMapperService;
import com.subra.aem.rjs.core.constants.JcrFileNames;
import com.subra.aem.rjs.core.constants.JcrPrimaryType;
import com.subra.aem.rjs.core.constants.JcrProperties;
import com.subra.aem.rjs.core.utils.RJSResourceUtils;
import com.subra.aem.rjs.mailer.Template;
import com.subra.aem.rjs.mailer.internal.helpers.MailerHelper;
import com.subra.aem.rjs.mailer.services.MailerService;
import com.subra.aem.rjs.mailer.services.TemplateService;
import com.subra.aem.rjs.mailer.utils.MailerUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.*;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.constants.HttpType;
import org.subra.commons.dtos.mailer.EmailRequest;
import org.subra.commons.exceptions.RJSCustomException;
import org.subra.commons.exceptions.RJSRuntimeException;
import org.subra.commons.helpers.CommonHelper;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component(service = TemplateService.class, immediate = true)
@ServiceRanking(60000)
@ServiceDescription("Subra Mailer Template Service")
@Designate(ocd = TemplateService.Config.class)
public final class TemplateServiceImpl implements TemplateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateServiceImpl.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private MailerService mailerService;

    private Resource approvedTemplatesResource;
    private Resource draftTemplatesResource;
    private String draftTemplatesIDPrefix;
    private String approvedTemplatesIDPrefix;

    @Activate
    protected void activate(final Config config) {
        try (ResourceResolver resourceResolver = RJSResourceUtils.getServiceResourceResolver(resolverFactory, UserMapperService.EMAIL_SERVICE)) {
            draftTemplatesResource = RJSResourceUtils.getOrCreateResource(resourceResolver, config.draft_templates_path(), JcrPrimaryType.SLING_FOLDER);
            approvedTemplatesResource = RJSResourceUtils.getOrCreateResource(resourceResolver, config.approved_templates_path(), JcrPrimaryType.SLING_FOLDER);
        } catch (LoginException e) {
            LOGGER.error("Unable to get resource resolver...");
        }
        draftTemplatesIDPrefix = config.draft_templates_id_prefix();
        approvedTemplatesIDPrefix = config.approved_templates_id_prefix();
        MailerHelper.setTemplateIDPrefixes(draftTemplatesIDPrefix, approvedTemplatesIDPrefix);
    }

    @Override
    public List<Template> listTemplates() {
        List<Template> templates = new ArrayList<>();
        Iterator<Resource> approvedTemplatesItr = approvedTemplatesResource.listChildren();
        Iterator<Resource> draftTemplatesItr = draftTemplatesResource.listChildren();
        while (approvedTemplatesItr.hasNext()) {
            Template template = createTemplate(approvedTemplatesItr.next());
            template.setDraft(false);
            templates.add(template);
        }
        while (draftTemplatesItr.hasNext()) {
            templates.add(new Template(draftTemplatesItr.next()));
        }
        return templates;
    }

    private Template createTemplate(Resource resource) {
        Template template = new Template(resource);
        template.setLookUps(getLookUpKeys(template));
        return template;
    }

    @Override
    public Template getTemplate(final String id) throws RJSCustomException {
        StringBuilder pathBuilder = new StringBuilder();
        if (StringUtils.startsWith(id, approvedTemplatesIDPrefix))
            pathBuilder.append(approvedTemplatesResource.getPath()).append(CommonHelper.SLASH)
                    .append(StringUtils.stripStart(id, approvedTemplatesIDPrefix));

        else if (StringUtils.startsWith(id, draftTemplatesIDPrefix))
            pathBuilder.append(draftTemplatesResource.getPath()).append(CommonHelper.SLASH)
                    .append(StringUtils.stripStart(id, draftTemplatesIDPrefix));

        try (ResourceResolver resourceResolver = RJSResourceUtils.getServiceResourceResolver(resolverFactory, UserMapperService.EMAIL_SERVICE)) {
            return Optional.of(resourceResolver).map(r -> r.getResource(pathBuilder.toString())).map(this::createTemplate)
                    .orElseThrow(() -> new RJSCustomException("Invalid template ID..."));
        } catch (LoginException e) {
            LOGGER.error("Error getting resource resolver ", e);
        }
        return null;
    }

    @Override
    public EmailRequest createOrUpdateTemplate(final String fileTitle, final String content) {
        try (ResourceResolver resourceResolver = RJSResourceUtils.getServiceResourceResolver(resolverFactory, UserMapperService.EMAIL_SERVICE)) {
            Session session = RJSResourceUtils.adoptToOrThrow(resourceResolver, Session.class);
            InputStream contentIS = IOUtils.toInputStream(content, HttpType.CHARSET_UTF_8.value());
            Binary binaryContent = session.getValueFactory().createBinary(contentIS);
            Node rootNode = RJSResourceUtils.adoptToOrThrow(draftTemplatesResource, Node.class);
            final String fileName = CommonHelper.createNameFromTitle(fileTitle);
            Node fileNode = null;
            if (!rootNode.hasNode(fileName)) {
                Node fileFolder = rootNode.addNode(fileName, JcrResourceConstants.NT_SLING_FOLDER);
                fileFolder.setProperty(JcrProperties.PN_CREATED_BY.property(), session.getUserID());
                fileFolder.setProperty(JcrProperties.PN_LOCKED.property(), false);
                fileNode = fileFolder.addNode(JcrFileNames.DEFAULT_TEXT_FILE.value(), JcrConstants.NT_FILE);
                Node fileContent = fileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
                fileContent.setProperty(JcrConstants.JCR_MIMETYPE, HttpType.MEDIA_TYPE_TEXT.value());
                fileContent.setProperty(JcrConstants.JCR_DATA, binaryContent);
            } else {
                fileNode = rootNode.getNode(fileName).getNode(JcrFileNames.DEFAULT_TEXT_FILE.value());
                fileNode.getNode(JcrConstants.JCR_CONTENT).setProperty(JcrConstants.JCR_DATA, binaryContent);
            }
            session.save();
            return generateRequestFormat(new Template(resourceResolver.getResource(fileNode.getParent().getPath())));
        } catch (IOException | RepositoryException e) {
            LOGGER.error("Error creating/updating template... ", e);
        } catch (Exception e) {
            LOGGER.error("Uncatched Error creating/updating template... ", e);
        }
        return null;
    }

    @Override
    public String readTemplate(final Template template) {
        return Optional.ofNullable(template).map(Template::getMessage).orElseGet(() -> {
            throw new RJSRuntimeException();
        });
    }

    @Override
    public boolean deleteTemplate(final Template template) {
        boolean status = false;
        try {
            Resource templateResource = template.getResource();
            templateResource.getResourceResolver().delete(templateResource);
            status = true;
        } catch (PersistenceException e) {
            LOGGER.error("Error deleting template...", e);
        }
        return status;
    }

    @Override
    public List<String> getLookUpKeys(final Template template) {
        return CommonHelper.getLookUpKeys(readTemplate(template));
    }

    @Override
    public EmailRequest generateRequestFormat(final Template template) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTemplateId(MailerHelper.getTemplateId(template));
        Map<String, String> params = new HashMap<>();
        params.put(MailerUtils.TO, "<to-emails-seperate-by-comma>");
        params.put(MailerUtils.CC, "<cc-emails-seperate-by-comma>");
        params.put(MailerUtils.BCC, "<bcc-emails-seperate-by-comma>");
        params.put(MailerUtils.SUBJECT, "<subject-line>");
        params.putAll(getLookUpKeys(template).stream().distinct().collect(Collectors.toMap(k -> k, v -> "<value>")));
        emailRequest.setParams(params);
        return emailRequest;
    }

    @Override
    public String generateEmailMarkUp(final EmailRequest email) {
        try {
            return MailerHelper.getEmailContent(readTemplate(getTemplate(email.getTemplateId())), email.getParams());
        } catch (RJSCustomException e) {
            LOGGER.error("Error generating email markup...", e);
        }
        return StringUtils.EMPTY;
    }

    @Override
    public Map<String, Object> sendEmail(final EmailRequest email) {
        Template template;
        try {
            template = getTemplate(email.getTemplateId());
            return mailerService.sendEmail(template, email.getParams(), null, null);
        } catch (RJSCustomException e) {
            LOGGER.error("Error sending email...", e);
        }
        return Collections.emptyMap();
    }

}
