package com.subra.aem.rjs.demos.jobs;

import com.subra.aem.rjs.mailer.Template;
import com.subra.aem.rjs.mailer.services.MailerService;
import com.subra.aem.rjs.mailer.utils.MailerUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * A JobConsumer Service implementation to demonstrate how to create a
 * JobConsumer to help schedule Jobs. A JOb is a special event that will be
 * processed exactly once. When a job is scheduled , it will be available in
 * /var/eventing/jobs . Even when the system fails, the jobs int he queue will
 * retry to execute till it fails
 */
@Component(service = JobConsumer.class, enabled = false,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Sample Job Consumer",
                JobConsumer.PROPERTY_TOPICS + "=" + DemoJobConsumer.TOPIC
        })
public class DemoJobConsumer implements JobConsumer {

    public static final String TOPIC = "demo/job";
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Reference
    private MailerService mailerService;

    @Override
    public JobResult process(Job job) {
        try {
            log.info("Processing the JOB ******* DemoJobConsumer");
            final String message = String.format("The resource %s was %s", String.valueOf(job.getProperty("path")), job.getProperty("type"));
            log.info(message);
            // Business logic goes here
            if (BooleanUtils.toBoolean((String) job.getProperty("sendEmail"))) {
                log.info("Sending email...");
                Map<String, Object> response = senEmail(message);
                if (!StringUtils.equalsAnyIgnoreCase((String) response.get("Status"), "SUCCESS"))
                    return JobResult.FAILED;
            }
            return JobResult.OK;
        } catch (Exception e) {
            log.error("Exception is ", e);
        }
        return JobResult.FAILED;
    }

    private Map<String, Object> senEmail(final String message) {
        Map<String, String> emailParams = new HashMap<>();
        emailParams.put(MailerUtils.SUBJECT, "DemoJobConsumer Update");
        emailParams.put(MailerUtils.TO, "raghava.joijode@gmail.com");
        return mailerService.sendEmail(new Template(message), emailParams, null, null);
    }

}