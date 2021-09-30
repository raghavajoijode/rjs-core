package com.subra.aem.rjs.demos.events;

import com.subra.aem.rjs.demos.jobs.DemoJobConsumer;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = ResourceChangeListener.class, enabled = false,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Demo to listen on changes in the resource tree"
                //ResourceChangeListener.PATHS + "=/apps/demo",//ResourceChangeListener.CHANGES + "=ADDED"
        })
@Designate(ocd = DemoResourceChangeListener.Config.class)
public class DemoResourceChangeListener implements ResourceChangeListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private JobManager jobManager;

    @Override
    public void onChange(List<ResourceChange> changes) {
        for (ResourceChange change : changes) {
            log.info("DemoResourceChangeListener - change  {}", change);
            Map<String, Object> jobProperties = new HashMap<>();
            jobProperties.put("path", change.getPath());
            jobProperties.put("type", change.getType().name());
            jobProperties.put("sendEmail", "false");
            Job j = jobManager.addJob(DemoJobConsumer.TOPIC, jobProperties);
            log.info("Job triggered .... status -> {}", j.getJobState());
        }
    }

    @Activate
    protected void activate(final Config config) {
        log.info("Listening to resource changes for path - {}, type - {}", config.resource_paths(), config.resource_change_types());
    }

    @ObjectClassDefinition(name = "Demo Resource Change Listener")
    @interface Config {
        @AttributeDefinition(name = "Resource change listener paths")
        String resource_paths() default "/apps/demo";

        @AttributeDefinition(name = "Resource change listener types")
        String[] resource_change_types() default {"ADDED", "REMOVED", "CHANGED"};
    }
}
