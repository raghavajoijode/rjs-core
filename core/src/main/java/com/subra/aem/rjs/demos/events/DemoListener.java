package com.subra.aem.rjs.demos.events;

import com.subra.aem.rjs.core.constants.JcrPrimaryType;
import com.subra.aem.rjs.core.utils.RJSResourceUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

@Component(service = EventListener.class, immediate = true, enabled = false,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Demo to Event Listener listen on changes in the resource tree"
        })
@Designate(ocd = DemoListener.Config.class)
public class DemoListener implements EventListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected String testNode1;
    protected String testNode2;
    protected ObservationManager observationManager;
    @Reference
    private ResourceResolverFactory resolverFactory;
    @Reference
    private SlingRepository slingRepository;
    private Session session;

    @Activate
    protected void activate(final Config config) {
        testNode1 = config.testNode1();
        testNode2 = config.testNode2();
        log.debug("DemoListener will listen on {} and update on {}", testNode1, testNode2);
        try {
            session = RJSResourceUtils.getAdminSession(slingRepository);
            observationManager = session.getWorkspace().getObservationManager();
            observationManager.addEventListener(this, Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED, testNode1, true, null, null, true);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    @Deactivate
    protected void deactivate(final ComponentContext componentContext) {
        try {
            if (observationManager != null) observationManager.removeEventListener(this);
        } catch (RepositoryException e) {
            log.error("error removing the JCR event listener", e);
        } finally {
            if (session != null) session.logout();
        }
    }

    @Override
    public void onEvent(final EventIterator events) {
        log.info("On Event of Demo Listener");
        while (events.hasNext()) {
            Event event = events.nextEvent();
            log.info("\nWorking on event:\n{}", event);
            try (ResourceResolver resourceResolver = RJSResourceUtils.getAdminServiceResourceResolver(resolverFactory)) {
                Resource source = resourceResolver.getResource(event.getIdentifier());
                Resource destination = RJSResourceUtils.getOrCreateResource(resourceResolver, testNode2, JcrPrimaryType.UNSTRUCTURED);
                if (source != null)
                    RJSResourceUtils.addOrUpdateProperty(destination, "prodNumber", source.getValueMap().get("testProp", "defVal"));
            } catch (LoginException | RepositoryException e) {
                log.error("Error updating destination node", e);
            }
        }
    }

    @ObjectClassDefinition(name = "Demo Event Listener")
    public @interface Config {
        @AttributeDefinition(name = "Source Node")
        String testNode1() default "/apps/demo/testnode1";

        @AttributeDefinition(name = "Destination Node")
        String testNode2() default "/apps/demo/testnode2";
    }

}
