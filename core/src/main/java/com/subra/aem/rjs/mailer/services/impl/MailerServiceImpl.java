package com.subra.aem.rjs.mailer.services.impl;

import com.drew.lang.annotations.NotNull;
import com.subra.aem.rjs.core.UserMapperService;
import com.subra.aem.rjs.mailer.Template;
import com.subra.aem.rjs.mailer.internal.helpers.MailerHelper;
import com.subra.aem.rjs.mailer.services.MailerGatewayService;
import com.subra.aem.rjs.mailer.services.MailerService;
import com.subra.aem.rjs.mailer.utils.MailerUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.helpers.CommonHelper;

import javax.activation.DataSource;
import java.util.Collections;
import java.util.Map;

/**
 * @author Raghava Joijode
 * <p>
 * Mailer Service to Send Emails with either just template path or
 * Template object.
 */
@Component(service = MailerService.class, immediate = true)
@ServiceRanking(60000)
@ServiceDescription("Subra - Mailer Service")
@Designate(ocd = MailerService.Config.class)
public class MailerServiceImpl implements MailerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailerServiceImpl.class);

    @Reference
    private MailerGatewayService connectorGateway;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private int connectTimeout;
    private int soTimeout;

    @Activate
    protected void activate(final Config config) {
        connectTimeout = config.connection_timeout();
        soTimeout = config.socket_timeout();
    }

    @Override
    public Map<String, Object> sendEmail(String templatePath, Map<String, String> emailParams, String... recipients) {
        return sendEmail(connectorGateway, templatePath, emailParams, null, recipients);
    }

    @Override
    public Map<String, Object> sendEmail(String templatePath, Map<String, String> emailParams) {
        String[] recipients = StringUtils.split(emailParams.remove(MailerUtils.TO), CommonHelper.COMMA);
        return sendEmail(connectorGateway, templatePath, emailParams, null, recipients);
    }

    @Override
    public Map<String, Object> sendEmail(MailerGatewayService messageGateway, final String templatePath,
                                         final Map<String, String> emailParams, final String... recipients) {
        return sendEmail(messageGateway, templatePath, emailParams, null, recipients);
    }

    @Override
    public Map<String, Object> sendEmail(String templatePath, Map<String, String> emailParams,
                                         Map<String, DataSource> attachments, String... recipients) {
        return sendEmail(connectorGateway, templatePath, emailParams, attachments, recipients);
    }

    @Override
    public Map<String, Object> sendEmail(MailerGatewayService messageGateway, String templatePath,
                                         Map<String, String> emailParams, Map<String, DataSource> attachments, String... recipients) {

        return sendEmail(messageGateway, createTemplate(templatePath), emailParams, attachments, recipients);

    }

    @Override
    public Map<String, Object> sendEmail(@NotNull Template template, Map<String, String> emailParams, MailerGatewayService messageGateway, Map<String, DataSource> attachments) {
        return sendEmail(messageGateway != null ? messageGateway : connectorGateway, template, emailParams, attachments, StringUtils.split(emailParams.remove(MailerUtils.TO), CommonHelper.COMMA));
    }

    private Map<String, Object> sendEmail(final MailerGatewayService messageGateway, @NotNull Template template,
                                          Map<String, String> emailParams, Map<String, DataSource> attachments, String... recipients) {
        MailerHelper.setTimeouts(soTimeout, connectTimeout);
        return MailerHelper.sendEmail(messageGateway, template, emailParams, attachments, recipients);
    }

    private Template createTemplate(final String templatePath) {
        Map<String, Object> authInfo = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE,
                UserMapperService.EMAIL_SERVICE.value());
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {
            return new Template(resourceResolver.getResource(templatePath));
        } catch (LoginException e) {
            LOGGER.error("Unable to obtain an administrative resource resolver to get the Mail Template at [{}]",
                    templatePath, e);
        }
        return null;
    }

}
