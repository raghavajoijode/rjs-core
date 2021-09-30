package com.subra.aem.rjs.mailer.services;

import com.drew.lang.annotations.NotNull;
import com.subra.aem.rjs.mailer.Template;
import com.subra.aem.rjs.mailer.utils.MailerUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import javax.activation.DataSource;
import java.util.Map;

/**
 * @author Raghava Joijode
 * <p>
 * Mailer Service to Send Emails with either just template path or
 * Template object.
 */
public interface MailerService {

    Map<String, Object> sendEmail(MailerGatewayService messageGateway, String templatePath, Map<String, String> emailParams, String... recipients);

    Map<String, Object> sendEmail(MailerGatewayService messageGateway, String templatePath, Map<String, String> emailParams, Map<String, DataSource> attachments, String... recipients);

    Map<String, Object> sendEmail(String templatePath, Map<String, String> emailParams);

    Map<String, Object> sendEmail(String templatePath, Map<String, String> emailParams, String... recipients);

    Map<String, Object> sendEmail(String templatePath, Map<String, String> emailParams, Map<String, DataSource> attachments, String... recipients);

    Map<String, Object> sendEmail(@NotNull Template template, Map<String, String> emailParams, MailerGatewayService messageGateway, Map<String, DataSource> attachments);

    @ObjectClassDefinition(name = "Subra Mailer Service Configuration")
    public @interface Config {

        @AttributeDefinition(name = "Socket Timeout", description = "Socket Timeout")
        int socket_timeout() default MailerUtils.DEFAULT_SOCKET_TIMEOUT;

        @AttributeDefinition(name = "Connection Timeout", description = "Connection Timeout")
        int connection_timeout() default MailerUtils.DEFAULT_CONNECT_TIMEOUT;
    }

}
