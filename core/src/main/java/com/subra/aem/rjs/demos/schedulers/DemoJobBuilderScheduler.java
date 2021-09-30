package com.subra.aem.rjs.demos.schedulers;

import com.subra.aem.rjs.demos.jobs.DemoJobConsumerNew;
import org.apache.sling.event.jobs.JobBuilder;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.ScheduledJobInfo;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JobConsumer Service implementation to demonstrate how to create a
 * JobConsumer to help schedule Jobs. A JOb is a special event that will be
 * processed exactly once. When a job is scheduled , it will be available in
 * /var/eventing/jobs . Even when the system fails, the jobs int he queue will
 * retry to execute till it fails
 */
@Component(service = DemoJobBuilderScheduler.class, immediate = true, enabled = false)
public class DemoJobBuilderScheduler {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private ScheduledJobInfo scheduledJobInfo;

    @Reference
    private JobManager jobManager;

    @Activate
    protected void activate() {
        log.info("Activate Method of DemoJobBuilderScheduler");
        startScheduledJob();
    }

    @Deactivate
    protected void deactivate() {
        log.info("Activate Method of DemoJobBuilderScheduler");
        if (scheduledJobInfo != null) scheduledJobInfo.unschedule();
    }

    public void startScheduledJob() {
        JobBuilder.ScheduleBuilder scheduleBuilder = jobManager.createJob(DemoJobConsumerNew.TOPIC).schedule();
        scheduleBuilder.cron("0 0/2 * ? * * *");
        log.info("DemoJobBuilderScheduler startScheduledJob");
        scheduledJobInfo = scheduleBuilder.add();
        if (scheduledJobInfo == null) {
            log.info("DemoJobConsumerNew Scheduled Failed");
        } else {
            log.info("DemoJobConsumerNew Scheduled");
        }
    }

}