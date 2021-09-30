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
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.resourceTypes=" + CustomFormActionTypeDataSourceServlet.RESOURCE_TYPE,
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=html"
        }
)
@ServiceRanking(1)
public class CustomFormActionTypeDataSourceServlet extends SlingSafeMethodsServlet {

    public static final String RESOURCE_TYPE = "core/wcm/components/form/container/v1/datasource/actiontype";
    private static final long serialVersionUID = 9114656669504668093L;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        SimpleDataSource actionTypeDataSource = new SimpleDataSource(getActionTypeResources(request.getResourceResolver()).iterator());
        request.setAttribute(DataSource.class.getName(), actionTypeDataSource);
    }

    private List<Resource> getActionTypeResources(ResourceResolver resourceResolver) {
        String[] inclusionList = new String[]{"foundation/components/form/actions/mail", "commerce/components/actions/updateorder"};
        List<Resource> actionTypeResources = new ArrayList<>();
        FormsManager formsManager = resourceResolver.adaptTo(FormsManager.class);
        if (formsManager != null) {
            Iterator<FormsManager.ComponentDescription> actions = formsManager.getActions();
            while (actions.hasNext()) {
                FormsManager.ComponentDescription description = actions.next();
                Resource dialogResource = resourceResolver.getResource(description.getResourceType() + "/" + FormConstants.NN_DIALOG);
                if (dialogResource != null && StringUtils.containsAny(dialogResource.getPath(), inclusionList)) {
                    actionTypeResources.add(new ActionTypeResource(description, resourceResolver));
                }
            }
        }
        return actionTypeResources;
    }

    private static class ActionTypeResource extends SyntheticResource {

        private final FormsManager.ComponentDescription description;
        private ValueMap valueMap;

        @Override
        @SuppressWarnings("unchecked")
        public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
            if (type == ValueMap.class) {
                if (valueMap == null) {
                    initValueMap();
                }
                return (AdapterType) valueMap;
            } else {
                return super.adaptTo(type);
            }
        }

        private void initValueMap() {
            valueMap = new ValueMapDecorator(new HashMap<>());
            valueMap.put("value", getValue());
            valueMap.put("text", getText());
            valueMap.put("selected", getSelected());
        }

        ActionTypeResource(FormsManager.ComponentDescription description, ResourceResolver resourceResolver) {
            super(resourceResolver, StringUtils.EMPTY, Resource.RESOURCE_TYPE_NON_EXISTING);
            this.description = description;
        }

        public String getText() {
            return description.getTitle();
        }

        public String getValue() {
            return description.getResourceType();
        }

        protected boolean getSelected() {
            return false;
        }
    }

}