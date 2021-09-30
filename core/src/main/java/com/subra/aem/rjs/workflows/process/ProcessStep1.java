package com.subra.aem.rjs.workflows.process;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = WorkflowProcess.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Write Adaptive Form Attachments to File System",
                Constants.SERVICE_VENDOR + "=Adobe Systems",
                "process.label" + "=Test 1"
        })
public class ProcessStep1 implements WorkflowProcess {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ProcessStep1.class);

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        System.out.println("Process Step 1 " + workItem.getWorkflow().getId());
    }
}
