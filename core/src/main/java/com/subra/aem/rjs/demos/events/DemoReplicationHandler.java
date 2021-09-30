package com.subra.aem.rjs.demos.events;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.subra.aem.rjs.demos.jobs.DemoJobConsumer;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.helpers.CommonHelper;

import java.util.HashMap;
import java.util.Map;

@Component(service = EventHandler.class, enabled = false,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Demo to listen on changes in the Replication tree",
                EventConstants.EVENT_TOPIC + "=com/day/cq/replication"
        })
public class DemoReplicationHandler implements EventHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private JobManager jobManager;

    public void handleEvent(final Event event) {
        log.debug("\n\n  Replication event: {} at: {}", event.getTopic(), event.getProperty("type"));
        if (ReplicationAction.fromEvent(event).getType().equals(ReplicationActionType.ACTIVATE)) {
            log.debug("Triggered activate on {}", ReplicationAction.fromEvent(event).getPath());
            Map<String, Object> jobProperties = new HashMap<>();
            try {
                jobProperties.put("path", CommonHelper.writeValueAsString(event.getProperty("paths")));
            } catch (JsonProcessingException e) {
                jobProperties.put("path", event.getProperty("paths"));
            }
            jobProperties.put("type", "ACTIVATED");
            jobProperties.put("sendEmail", "false");
            Job j = jobManager.addJob(DemoJobConsumer.TOPIC, jobProperties);
            log.info("Job triggered .... status -> {}", j.getJobState());
        }
    }
}