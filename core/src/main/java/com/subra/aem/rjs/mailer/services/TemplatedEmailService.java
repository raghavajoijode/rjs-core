package com.subra.aem.rjs.mailer.services;

import com.subra.aem.rjs.mailer.utils.EmailType;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.Map;

/**
 * @author Raghava Joijode
 */
public interface TemplatedEmailService {

    boolean email(EmailType type, String subject, String recipientName, String senderName, String link, Map<String, String> optionalParams, String... recipient);

    @ObjectClassDefinition(name = "Subra Templated Email Service Configuration", description = "RJS - Email Service")
    public @interface Config {

        @AttributeDefinition(name = "Registration Email Template", description = "Registration Email Template")
        String registration_email_template() default "/conf/foundation/settings/notification/email/subra/user-registration-email.txt";

        @AttributeDefinition(name = "Invitation Email Template", description = "Invitition Email Template")
        String invitation_email_template() default "/conf/foundation/settings/notification/email/subra/registration-invitation-email.txt";

        @AttributeDefinition(name = "Welcome Email Template", description = "Welcome Email Template")
        String welcome_email_template() default "/conf/foundation/settings/notification/email/subra/welcome-email.txt";

        @AttributeDefinition(name = "Exception Email Template", description = "Exception Email Template")
        String exception_email_template() default "/conf/foundation/settings/notification/email/subra/exception-email.txt";

        @AttributeDefinition(name = "Generic Email Template", description = "Exception Email Template")
        String generic_email_template() default "/conf/foundation/settings/notification/email/subra/generic-email.txt";

        @AttributeDefinition(name = "HTML Email Template", description = "Exception Email Template")
        String sample_html_email() default "/conf/foundation/settings/notification/email/subra/sample.html";

    }

}
