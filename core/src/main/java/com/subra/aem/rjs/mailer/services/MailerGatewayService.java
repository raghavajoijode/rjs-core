package com.subra.aem.rjs.mailer.services;

import com.subra.aem.rjs.mailer.utils.EmailSenderType;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

public interface MailerGatewayService {

    boolean send(Email email) throws EmailException;


    @ObjectClassDefinition(name = "RJS Mailer Gateway Service Configuration")
    @interface Config {

        @AttributeDefinition(name = "SMTP Host", description = "Socket Timeout")
        String smtp_host() default "smtp.gmail.com";

        @AttributeDefinition(name = "SMTP Port")
        int smtp_port() default 465;

        @AttributeDefinition(name = "SMTP User")
        String smtp_user() default "noreply.subra";

        @AttributeDefinition(name = "SMTP Password")
        String smtp_password() default "vexbdwpuguhmdkvy";

        @AttributeDefinition(name = "SMTP Enable SSL")
        boolean smtp_ssl() default true;

        @AttributeDefinition(name = "SMTP Enable Debug")
        boolean smtp_debug() default true;

        @AttributeDefinition(name = "Default Sender Email")
        String smtp_from_address() default "noreply.subra@gmail.com";

        @AttributeDefinition(name = "Default Sender Name")
        String smtp_from_name() default "RJS Technologies Group";

        @AttributeDefinition(name = "Connect With...", type = AttributeType.STRING)
        EmailSenderType connect_with() default EmailSenderType.NOREPLY;
    }

}
