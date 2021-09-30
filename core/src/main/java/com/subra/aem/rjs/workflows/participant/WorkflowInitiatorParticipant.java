package com.subra.aem.rjs.workflows.participant;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true, enabled = true, service = ParticipantStepChooser.class, property = {
        "chooser.label=WorkflowInitiatorParticipant",
        Constants.SERVICE_DESCRIPTION + "=WorkflowInitiatorParticipant Process"})

public class WorkflowInitiatorParticipant implements ParticipantStepChooser {

    @Override
    public String getParticipant(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        String user = workItem.getWorkflow().getInitiator();

        if (workflowSession != null && workItem.getWorkflowData() != null && workItem.getWorkflowData().getMetaDataMap() != null) {
            user = workItem.getWorkflowData().getMetaDataMap().get("userId", user);
        }
        return user;
    }

}
