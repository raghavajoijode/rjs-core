package com.subra.aem.rjs.core.component.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragment;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Model(adaptables = SlingHttpServletRequest.class, adapters = ContentFragment.class, resourceType = ContentFragmentModel.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ContentFragmentModel implements ContentFragment {
    // weretail/components/content/contentfragment
    // core/wcm/components/contentfragment/v1/contentfragment
    public static final String RESOURCE_TYPE = "rjs/core/components/content/contentfragment/v1/contentfragment";
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentFragmentModel.class);

    @ScriptVariable
    private Resource resource;

    @Self
    @Via(type = ResourceSuperType.class)
    private ContentFragment contentFragment;

    @ValueMapValue(name = ContentFragment.PN_DISPLAY_MODE, injectionStrategy = InjectionStrategy.OPTIONAL)
    private String displayMode;

    @PostConstruct
    private void initModel() {
        updateTextProperty();
    }

    @Override
    public String getTitle() {
        return contentFragment.getTitle();
    }

    @Override
    public String getDescription() {
        return contentFragment.getDescription();
    }

    @Override
    public String getType() {
        return contentFragment.getType();
    }

    @Override
    public String getName() {
        return contentFragment.getName();
    }

    @Override
    public String getGridResourceType() {
        return contentFragment.getGridResourceType();
    }

    @Override
    public String getEditorJSON() {
        return contentFragment.getEditorJSON();
    }

    @Override
    public Map<String, DAMContentElement> getExportedElements() {
        return contentFragment.getExportedElements();
    }

    @Override
    public String[] getExportedElementsOrder() {
        return contentFragment.getExportedElementsOrder();
    }

    @Override
    public List<DAMContentElement> getElements() {
        return getContentFragmentElements().stream().map(DAMContentElementImpl::new).collect(Collectors.toList());
    }

    @Override
    public List<Resource> getAssociatedContent() {
        return contentFragment.getAssociatedContent();
    }

    @Override
    public String getExportedType() {
        return contentFragment.getExportedType();
    }

    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return contentFragment.getExportedItems();
    }

    @Override
    public String[] getExportedItemsOrder() {
        return contentFragment.getExportedItemsOrder();
    }

    @Override
    public String[] getParagraphs() {
        String[] paragraphs = contentFragment.getParagraphs();
        String[] processedParagraphs = new String[paragraphs.length];
        for (int i = 0; i < paragraphs.length; i++) {
            processedParagraphs[i] = "START- " + paragraphs[i] + " -END";
        }
        return processedParagraphs;
    }

    private void updateTextProperty() {
        String text = getText();
        if (StringUtils.isNoneBlank(text)) {
            try {
                ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
                map.put("text", text);
                resource.getResourceResolver().commit();
            } catch (PersistenceException e) {
                LOGGER.error("RJContentFragmentModel Exception updating property", e);
            }
        }
    }

    private String getText() {
        return isParagraph() ? StringUtils.join(contentFragment.getParagraphs()) : getTextFromElements();
    }

    private String getTextFromElements() {
        StringBuilder sb = new StringBuilder();
        getContentFragmentElements().forEach(element -> {
            if (!StringUtils.equalsAny(element.getDataType(), "calendar", "boolean")) {
                sb.append(element.getValue());
            }
        });
        return sb.toString();
    }

    private boolean isParagraph() {
        return getContentFragmentElements().size() == 1 && getContentFragmentElements().get(0).isMultiLine() && StringUtils.equals(displayMode, "singleText");
    }

    private List<DAMContentElement> getContentFragmentElements() {
        return Optional.of(contentFragment).map(ContentFragment::getElements).orElse(Collections.emptyList());
    }

    public class DAMContentElementImpl implements DAMContentElement {

        private final DAMContentElement element;

        public DAMContentElementImpl(DAMContentElement element) {
            this.element = element;
        }

        @Override
        public String getName() {
            return element.getName();
        }

        @Override
        public String getTitle() {
            return element.getTitle();
        }

        @Override
        public String getDataType() {
            return element.getDataType();
        }

        @Override
        public Object getValue() {
            return "<p>START 2- </p>" + element.getValue() + "<p> - 2 END</p>";
        }

        @Override
        public String getExportedType() {
            return element.getExportedType();
        }

        @Override
        public boolean isMultiLine() {
            return element.isMultiLine();
        }

        @Override
        public boolean isMultiValue() {
            return element.isMultiValue();
        }

        @Override
        public String getHtml() {
            return element.getHtml();
        }
    }

}
