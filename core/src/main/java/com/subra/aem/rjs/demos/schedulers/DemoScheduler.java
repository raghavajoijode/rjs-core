package com.subra.aem.rjs.demos.schedulers;

import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Using Scheduler api
@Designate(ocd = DemoScheduler.Config.class)
@Component(service = DemoScheduler.class, immediate = true, enabled = false)
public class DemoScheduler {

    @Reference
    private Scheduler scheduler;

    @Reference
    private DemoTask task;

    @ObjectClassDefinition(name = "A Demo scheduled job")
    @interface Config {
        @AttributeDefinition(name = "Enable Scheduler")
        boolean scheduler_enabled() default true;

        @AttributeDefinition(name = "Cron-job expression", description = "Default value '0 0/2 * ? * * *' runs at 0th second in every 2 minutes")
        String my_cron_expression() default "0 0/2 * ? * * *";

        @AttributeDefinition(name = "Cron-job expression", description = "Default value '0 0/2 * ? * * *' runs at 0th second in every 2 minutes")
        String scheduler_name() default "Demo Scheduler";

        @AttributeDefinition(name = "Concurrent task", description = "Whether or not to schedule this task concurrently")
        boolean scheduler_concurrent() default false;
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Modified
    @Activate
    protected void activate(final Config config) {
        removeScheduler(config);
        addScheduler(config);
    }

    @Deactivate
    protected void deActivate(final Config config) {
        removeScheduler(config);
    }

    private void removeScheduler(final Config config) {
        log.info("Removing Scheduler Job '{}'", config.scheduler_name());
        scheduler.unschedule(config.scheduler_name());
    }

    private void addScheduler(final Config config) {
        if (config.scheduler_enabled()) {
            // TODO add to RJSDateUtils - Converting local date to util.Date
            // LocalDateTime ldt = LocalDateTime.of(2021, 2, 12, 10, 20)
            // ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault())
            // Date date = Date.from(zdt.toInstant())

            // Create a schedule options to schedule the job based on the expression.
            ScheduleOptions cronScheduleOptions = scheduler.EXPR(config.my_cron_expression());
            cronScheduleOptions.name(config.scheduler_name());
            cronScheduleOptions.canRunConcurrently(false);
            boolean status = scheduler.schedule(task, cronScheduleOptions);

            // - Various available options
            // Create a schedule options to fire a job once at a specific date
            // ScheduleOptions scheduleOptions = scheduler.AT(date)

            //Create a schedule options to fire a job period starting at a specific date
            // here scheduler runs 2 times (use -1 for endless) for every 30 sec after given date
            // scheduleOptions = scheduler.AT(date, 2, 30)

            // Create a schedule options to fire a job immediately and only once
            // scheduleOptions = scheduler.NOW()

            // Create a schedule options to fire a job immediately more than once.
            // here scheduler runs 2 times (use -1 for endless) for every 30 sec after current date time
            // scheduleOptions = scheduler.NOW(2, 3)

            // Create a schedule options to schedule the job based on the expression
            // scheduleOptions = scheduler.EXPR("0 0/2 * ? * * *")

            // scheduleOptions.name(config.scheduler_name())
            // Whether this job can run even if previous scheduled runs are still running
            // scheduleOptions.canRunConcurrently(false)

            // schedule a job returns true if job is scheduled else false
            // scheduler.schedule(job, scheduleOptions)

            log.info(status ? "Scheduler added successfully" : "Scheduler initiated but could not be added");
        } else {
            log.info("DemoScheduler is Disabled, no scheduler job created");
        }
    }

}
