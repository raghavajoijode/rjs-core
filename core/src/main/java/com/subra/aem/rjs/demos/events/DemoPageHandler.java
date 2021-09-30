package com.subra.aem.rjs.demos.events; // Update 1

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import com.subra.aem.rjs.core.utils.RJSResourceUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Credentials;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.util.Iterator;
import java.util.Optional;

@Component(service = EventHandler.class, enabled = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Event Handler to listen on page modification",
                EventConstants.EVENT_TOPIC + "=" + PageEvent.EVENT_TOPIC,
                //EventConstants.EVENT_FILTER + "=" + "(&amp;(path=/content/we-retail/*/en/men))"
                //EventConstants.EVENT_FILTER + "=" + "(modifications.path=/content/we-retail/*/en/men)"

        })
public class DemoPageHandler implements EventHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private SlingRepository repository;

    @Reference
    private WorkflowService workflowService;

    @Reference
    private ResourceResolverFactory resolverFactory;

    public void handleEvent(final Event event) {
        PageEvent pageEvent = PageEvent.fromEvent(event);
        if (pageEvent != null && pageEvent.isLocal()) {
            Iterator<PageModification> modificationsIterator = pageEvent.getModifications();
            while (modificationsIterator.hasNext()) {
                PageModification modification = modificationsIterator.next();
                if (PageModification.ModificationType.MODIFIED.equals(modification.getType())) {
                    log.info("Page is modified {}, {}", modification.getUserId(), modification.getPath());
                    initiateWorkflow(modification.getUserId(), modification.getPath());
                }
            }
        }
    }

    private void initiateWorkflow(String userId, String path) {
        log.info("Initiating WF...");
        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = RJSResourceUtils.getAdminServiceResourceResolver(resolverFactory);
        } catch (LoginException e) {
            log.info("Exception getting resolver..", e);
        }
        Optional<Page> page = Optional.of(resourceResolver).map(resolver -> resolver.getResource(path)).map(resource -> resource.adaptTo(Page.class));
        page.ifPresent(p -> {
            Session session = getParticipantSession(userId);
            if (session == null) {
                session = p.adaptTo(Session.class);
            }
            startWorkflow(session, p.getPath());
        });

    }

    private void startWorkflow(Session session, String payload) {
        log.info("Starting WF");
        WorkflowSession wfSession = workflowService.getWorkflowSession(session);
        try {
            WorkflowModel wfModel = wfSession.getModel("/var/workflow/models/publish_example"); // Update 3 /var/workflow/models/publish_example
            WorkflowData wfData = wfSession.newWorkflowData("JCR_PATH", payload);
            wfSession.startWorkflow(wfModel, wfData);
        } catch (WorkflowException ex) {
            log.info("Error starting workflow.", ex);
        }
    }

    private Session getParticipantSession(String participantId) {
        try {
            return this.repository.impersonateFromService(null, (Credentials) new SimpleCredentials(participantId, new char[0]), null);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }
}

