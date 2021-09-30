package com.subra.aem.rjs.workflows.participant;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        immediate = true,
        service = ParticipantStepChooser.class,
        property = {
                "chooser.label=Content Publisher Participant",
                Constants.SERVICE_DESCRIPTION + "=ContentPublisherParticipant Participant Chooser"
        })
@Designate(ocd = ContentPublisherParticipant.Config.class)
public class ContentPublisherParticipant implements ParticipantStepChooser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentPublisherParticipant.class);

    private String userOrGroupId;

    @Activate
    protected void activate(Config config) {
        userOrGroupId = config.user_or_group_id();
        LOGGER.debug("Activate ContentPublisherParticipant.. and userOrGroupId: {}", userOrGroupId);
    }

    @Override
    public String getParticipant(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        if (workflowSession != null && workItem != null && workItem.getWorkflowData() != null && workItem.getWorkflowData().getPayload() != null) {
            LOGGER.debug("Send to user/group {}", userOrGroupId);
            return userOrGroupId;
        }
        return StringUtils.EMPTY;
    }

    @ObjectClassDefinition(name = "Content Publisher Participant", description = "Content Publisher Participant")
    public @interface Config {
        @AttributeDefinition(name = "User/Group Principle id")
        String user_or_group_id() default "test-publishers";
    }

}
