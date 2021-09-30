package com.subra.aem.rjs.demos.jobs;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                JobConsumer.PROPERTY_TOPICS + "=" + DemoJobConsumerNew.TOPIC
        })
public class DemoJobConsumerNew implements JobConsumer {

    public static final String TOPIC = "demo/test/scheduled/job";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private String myParameter;

    @Override
    public JobResult process(Job job) {
        try {
            log.info("Processing the DemoJobConsumerNew *******");
            log.info("NEW:: Job {}", myParameter);
            // Business logic goes here
            return JobResult.OK;
        } catch (Exception e) {
            log.error("Exception is ", e);
        }
        return JobResult.FAILED;
    }

    @Activate
    protected void activate(final Config config) {
        log.info("************************************** ACTIVATED ?????? {} ????? ******************************************", config.myParameter());
        myParameter = config.myParameter();
    }

    @ObjectClassDefinition(name = "A Demo scheduled task")
    @interface Config {
        @AttributeDefinition(name = "A parameter", description = "sample value")
        String myParameter() default "Demo Scheduled Job value";
    }

}