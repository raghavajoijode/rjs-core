package com.subra.aem.rjs.examples.servlets;

import com.adobe.cq.wcm.core.components.internal.form.FormConstants;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.wcm.foundation.forms.FormsManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.resourceTypes=" + CustomFormActionSettingsDataSourceServlet.RESOURCE_TYPE,
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=html"
        }
)
@ServiceRanking(1)
public class CustomFormActionSettingsDataSourceServlet extends SlingSafeMethodsServlet {

    public static final String RESOURCE_TYPE = "core/wcm/components/form/container/v1/datasource/actionsetting";
    private static final long serialVersionUID = 9114656669504668094L;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        SimpleDataSource actionTypeDataSource = new SimpleDataSource(getSettingsDialogs(request.getResourceResolver()).iterator());
        request.setAttribute(DataSource.class.getName(), actionTypeDataSource);
    }

    private List<Resource> getSettingsDialogs(ResourceResolver resourceResolver) {
        String[] inclusionList = new String[]{"foundation/components/form/actions/mail", "commerce/components/actions/updateorder"};
        List<Resource> actionTypeSettingsResources = new ArrayList<>();
        FormsManager formsManager = resourceResolver.adaptTo(FormsManager.class);
        if (formsManager != null) {
            Iterator<FormsManager.ComponentDescription> actions = formsManager.getActions();
            while (actions.hasNext()) {
                FormsManager.ComponentDescription description = actions.next();
                Resource dialogResource = resourceResolver.getResource(description.getResourceType() + "/" + FormConstants.NN_DIALOG);
                if (dialogResource != null && StringUtils.containsAny(dialogResource.getPath(), inclusionList)) {
                    actionTypeSettingsResources.add(dialogResource);
                }
            }
        }
        return actionTypeSettingsResources;
    }

}