package com.subra.aem.rjs.demos.schedulers;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// White board pattern
@Designate(ocd = DemoSelfInvokingScheduledJob.Config.class)
@Component(service = Runnable.class, enabled = false)
public class DemoSelfInvokingScheduledJob implements Runnable {

    @ObjectClassDefinition(name = "A Demo self invoking scheduled job")
    @interface Config {

        @AttributeDefinition(name = "Cron-job expression", description = "Default value '0 0/2 * ? * * *' runs at 0th second in every 2 minutes")
        String scheduler_expression() default "0 0/2 * ? * * *";

        @AttributeDefinition(name = "Concurrent task", description = "Whether or not to schedule this task concurrently")
        boolean scheduler_concurrent() default false;

        @AttributeDefinition(name = "Some Property", description = "Whether or not to schedule this task concurrently")
        String myProperty() default "test";

    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    private String prop;

    @Override
    public void run() {
        log.info("\n\nWill Do Some task as scheduled... {}\n\n", prop);
    }

    @Activate
    protected void activate(final Config config) {
        log.info("{} activated with config {} ", getClass(), config);
        prop = config.myProperty();
    }

}
