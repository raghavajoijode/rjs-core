package com.subra.aem.rjs.core.component.base;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.SlingModelFilter;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.subra.aem.rjs.core.utils.RJSResourceUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.*;
import org.apache.sling.models.factory.ModelFactory;

import java.util.List;
import java.util.Map;

/**
 * Base abstract class for Sling Models which implements common methods involved
 * for each Sling Model like getting child items, their order and style system
 *
 * @author Raghava Joijode
 */
@Model(adaptables = {SlingHttpServletRequest.class}, adapters = {AbstractComponentModel.class, ComponentExporter.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public abstract class AbstractComponentModel {

    protected boolean isEmpty;
    @Self
    private SlingHttpServletRequest request;
    @OSGiService
    private ModelFactory modelFactory;
    @OSGiService
    private SlingModelFilter slingModelFilter;
    @ScriptVariable
    private Style currentStyle;
    @SlingObject
    private ResourceResolver resolver;
    @ValueMapValue(name = "cq:styleIds")
    @Optional
    private List<String> styleids;
    private Map<String, ComponentExporter> childModels;

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(value = ":items")
    public Map<String, ComponentExporter> getExportedItems() {
        if (childModels == null) {
            childModels = RJSResourceUtils.getChildModels(request, slingModelFilter, modelFactory, ComponentExporter.class);
        }
        return childModels;
    }

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(value = ":itemsOrder")
    public String[] getExportedItemsOrder() {
        Map<String, ? extends ComponentExporter> models = getExportedItems();
        if (models.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return models.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(value = ":styleSystems")
    public List<String> getStyleSystemClasses() {
        return RJSResourceUtils.getAssociatedStyleSystem(resolver.getResource(currentStyle.getPath()), styleids);
    }

    public boolean isEmpty() {
        return false;
    }

}
