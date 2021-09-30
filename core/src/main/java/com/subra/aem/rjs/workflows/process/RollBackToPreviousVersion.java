package com.subra.aem.rjs.workflows.process;

import com.day.cq.wcm.api.Page;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.VersionManager;
import java.util.Optional;

@Component(
        service = WorkflowProcess.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Write Adaptive Form Attachments to File System",
                Constants.SERVICE_VENDOR + "=Adobe Systems",
                "process.label" + "=Save Adaptive Form Attachments to File System"
        })
public class RollBackToPreviousVersion implements WorkflowProcess {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RollBackToPreviousVersion.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        final String payloadPath = workItem.getWorkflowData().getPayload().toString();
        VersionManager versionManager;
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(null)) {
            final String contentPath = Optional.of(resourceResolver).map(r -> r.getResource(payloadPath))
                    .map(resource -> resource.adaptTo(Page.class))
                    .map(Page::getContentResource).map(Resource::getPath).orElse(payloadPath);
            Session session = resourceResolver.adaptTo(Session.class);
            if (session != null && (versionManager = session.getWorkspace().getVersionManager()) != null) {
                versionManager.restore(versionManager.getBaseVersion(contentPath), true);
                versionManager.checkout(contentPath);
            }
        } catch (LoginException | RepositoryException e) {
            LOGGER.error("Exception occurred creating version", e);
        }
    }

}
