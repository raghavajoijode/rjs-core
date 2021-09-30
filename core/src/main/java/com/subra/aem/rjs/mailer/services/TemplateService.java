package com.subra.aem.rjs.mailer.services;

import com.subra.aem.rjs.mailer.Template;
import com.subra.aem.rjs.mailer.utils.MailerUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.subra.commons.dtos.mailer.EmailRequest;
import org.subra.commons.exceptions.RJSCustomException;

import java.util.List;
import java.util.Map;

/**
 * @author Raghava Joijode
 */
public interface TemplateService {

    List<Template> listTemplates();

    Template getTemplate(final String id) throws RJSCustomException;

    EmailRequest createOrUpdateTemplate(final String fileTitle, final String content);

    String readTemplate(final Template template);

    boolean deleteTemplate(final Template template);

    List<String> getLookUpKeys(final Template template);

    EmailRequest generateRequestFormat(final Template template);

    String generateEmailMarkUp(final EmailRequest email);

    Map<String, Object> sendEmail(final EmailRequest email);

    @ObjectClassDefinition(name = "Subra Mailer Template Service Configuration")
    @interface Config {

        @AttributeDefinition(name = "Approved Templates Path")
        String approved_templates_path() default MailerUtils.DEFAULT_APPROVED_TEMPLATES_PATH;

        @AttributeDefinition(name = "Draft Templates Path")
        String draft_templates_path() default MailerUtils.DEFAULT_DRAFT_TEMPLATES_PATH;

        @AttributeDefinition(name = "Draft Templates ID Prefix")
        String draft_templates_id_prefix() default MailerUtils.DEFAULT_DRAFT_TEMPLATES_ID_PREFIX;

        @AttributeDefinition(name = "Approved Templates ID Prefix")
        String approved_templates_id_prefix() default MailerUtils.DEFAULT_APPROVED_TEMPLATES_ID_PREFIX;

    }

}
