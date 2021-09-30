package com.subra.aem.rjs.core.component.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.granite.license.ProductInfoProvider;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Style;
import com.subra.aem.rjs.core.component.base.AbstractComponentModel;
import com.subra.aem.rjs.core.utils.RJSResourceUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.*;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Version;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.*;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

@Model(adaptables = {SlingHttpServletRequest.class}, adapters = {BasePage.class,
        ComponentExporter.class}, resourceType = BasePage.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION, options = {
        @ExporterOption(name = "MapperFeature.SORT_PROPERTIES_ALPHABETICALLY", value = "true"),
        @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "false")})
public class BasePage extends AbstractComponentModel implements ComponentExporter {

    public static final String PN_REDIRECT_TARGET = "cq:redirectTarget";
    protected static final String DEFAULT_TEMPLATE_EDITOR_CLIENTLIB = "wcm.foundation.components.parsys.allowedcomponents";
    protected static final String PN_CLIENTLIBS = "clientlibs";
    protected static final String RESOURCE_TYPE = "foundation/components/structure/base-page";
    protected static final String PN_CLIENTLIBS_JS_HEAD = "clientlibsJsHead";
    private static final String PN_APP_RESOURCES_CLIENTLIB = "appResourcesClientlib";
    protected String[] clientLibCategoriesJsBody = new String[0];
    protected String[] clientLibCategoriesJsHead = new String[0];
    @ValueMapValue(name = PROPERTY_RESOURCE_TYPE, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "No resourceType")
    private String resourceType;
    @ValueMapValue(name = PN_REDIRECT_TARGET, injectionStrategy = InjectionStrategy.OPTIONAL)
    private String redirectTargetValue;
    @ScriptVariable
    private Page currentPage;
    @ScriptVariable
    private Design currentDesign;
    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    private Style currentStyle;
    @ScriptVariable
    private ValueMap properties;
    @ScriptVariable
    private ComponentContext componentContext;
    @Self
    private SlingHttpServletRequest request;
    @OSGiService
    private SlingSettingsService settings;
    @OSGiService
    private HtmlLibraryManager htmlLibraryManager;
    @OSGiService
    private ProductInfoProvider productInfoProvider;
    @SlingObject
    private Resource currentResource;
    @SlingObject
    private ResourceResolver resourceResolver;
    @OSGiService
    private ModelFactory modelFactory;
    @OSGiService
    private SlingModelFilter slingModelFilter;
    private String[] keywords = new String[0];
    private String designPath;
    private String staticDesignPath;
    private String appResourcesPath;
    private String title;
    private String templateName;
    private String[] clientLibCategories = new String[0];
    private Calendar lastModifiedDate;
    private Boolean hasCloudconfigSupport;
    private Map<String, Object> redirectTarget;

    @PostConstruct
    protected void init() {
        title = currentPage.getTitle();
        if (StringUtils.isBlank(title)) {
            title = currentPage.getName();
        }
        // Keywords
        Tag[] tags = currentPage.getTags();
        keywords = new String[tags.length];
        int index = 0;
        for (Tag tag : tags) {
            keywords[index++] = tag.getTitle(currentPage.getLanguage(false));
        }

        if (currentDesign != null) {
            String currentDesignPath = currentDesign.getPath();
            if (!"/etc/designs/default".equals(currentDesignPath)) {
                designPath = currentDesignPath;
                if (resourceResolver.getResource(currentDesignPath + "/static.css") != null) {
                    staticDesignPath = currentDesignPath + "/static.css";
                }
            }

        }

        String resourcesClientLibrary = currentStyle.get(PN_APP_RESOURCES_CLIENTLIB, String.class);
        if (resourcesClientLibrary != null) {
            Collection<ClientLibrary> clientLibraries = htmlLibraryManager
                    .getLibraries(new String[]{resourcesClientLibrary}, LibraryType.CSS, true, true);
            ArrayList<ClientLibrary> clientLibraryList = Lists.newArrayList(clientLibraries.iterator());
            if (!clientLibraryList.isEmpty()) {
                appResourcesPath = getProxyPath(clientLibraryList.get(0));
            }
        }

        populateClientlibCategories();
        populateClientLibCategoriesJs();
        extractTemplateName();
        setRedirect();
    }

    public ValueMap getPageProperties() {
        return properties;
    }

    // Tested
    public String getLanguage() {
        return currentPage == null ? Locale.getDefault().toLanguageTag()
                : currentPage.getLanguage(false).toLanguageTag();
    }

    public String[] getKeywords() {
        return Arrays.copyOf(keywords, keywords.length);
    }

    public String getTitle() {
        return title;
    }

    public String getTemplateName() {
        return templateName;
    }

    public Map<String, Object> getRedirectTarget() {
        return redirectTarget;
    }

    public String getCssClassNames() {
        Set<String> cssClassesSet = componentContext.getCssClassNames();
        return StringUtils.join(cssClassesSet, " ");
    }

    public String getAppResourcesPath() {
        return appResourcesPath;
    }

    public String getDesignPath() {
        return designPath;
    }

    public String getStaticDesignPath() {
        return staticDesignPath;
    }

    public String[] getClientLibCategories() {
        return Arrays.copyOf(clientLibCategories, clientLibCategories.length);
    }

    public String[] getClientLibCategoriesJsBody() {
        return Arrays.copyOf(clientLibCategoriesJsBody, clientLibCategoriesJsBody.length);
    }

    public String[] getClientLibCategoriesJsHead() {
        return Arrays.copyOf(clientLibCategoriesJsHead, clientLibCategoriesJsHead.length);
    }

    public boolean hasCloudconfigSupport() {
        if (hasCloudconfigSupport == null) {
            if (productInfoProvider == null || productInfoProvider.getProductInfo() == null
                    || productInfoProvider.getProductInfo().getVersion() == null) {
                hasCloudconfigSupport = false;
            } else {
                hasCloudconfigSupport = productInfoProvider.getProductInfo().getVersion()
                        .compareTo(new Version("6.4.0")) >= 0;
            }
        }
        return hasCloudconfigSupport;
    }

    public Calendar getLastModifiedDate() {
        if (lastModifiedDate == null) {
            lastModifiedDate = properties.get(NameConstants.PN_PAGE_LAST_MOD, Calendar.class);
        }
        return lastModifiedDate;
    }

    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }

    private void extractTemplateName() {
        String templatePath = properties.get(NameConstants.PN_TEMPLATE, String.class);
        Optional.ofNullable(templatePath).filter(StringUtils::isNotBlank).filter(p -> p.lastIndexOf('/') > 0)
                .ifPresent(a -> templateName = StringUtils.substringAfterLast(a, "/"));
    }

    private void populateClientlibCategories() {
        List<String> categories = new ArrayList<>();
        Template template = currentPage.getTemplate();
        if (template != null && template.hasStructureSupport()) {
            Resource templateResource = template.adaptTo(Resource.class);
            if (templateResource != null) {
                addDefaultTemplateEditorClientLib(templateResource, categories);
                addPolicyClientLibs(categories);
            }
        }
        clientLibCategories = categories.toArray(new String[categories.size()]);
    }

    private void populateClientLibCategoriesJs() {
        if (currentStyle != null) {
            clientLibCategoriesJsHead = currentStyle.get(PN_CLIENTLIBS_JS_HEAD, ArrayUtils.EMPTY_STRING_ARRAY);
            LinkedHashSet<String> categories = new LinkedHashSet<>(Arrays.asList(clientLibCategories));
            categories.removeAll(Arrays.asList(clientLibCategoriesJsHead));
            clientLibCategoriesJsBody = categories.toArray(new String[0]);

        }
    }

    private void addDefaultTemplateEditorClientLib(Resource templateResource, List<String> categories) {
        if (currentPage.getPath().startsWith(templateResource.getPath())) {
            categories.add(DEFAULT_TEMPLATE_EDITOR_CLIENTLIB);
        }
    }

    private void addPolicyClientLibs(List<String> categories) {
        if (currentStyle != null) {
            Collections.addAll(categories, currentStyle.get(PN_CLIENTLIBS, ArrayUtils.EMPTY_STRING_ARRAY));
        }
    }

    private String getProxyPath(ClientLibrary lib) {
        String path = lib.getPath();
        if (lib.allowProxy()) {
            for (String searchPath : request.getResourceResolver().getSearchPath()) {
                if (path.startsWith(searchPath)) {
                    path = request.getContextPath() + "/etc.clientlibs/" + path.replaceFirst(searchPath, "");
                }
            }
        } else {
            if (request.getResourceResolver().getResource(lib.getPath()) == null) {
                path = null;
            }
        }
        return path != null ? path + "/resources" : path;
    }

    private void setRedirect() {
        if (StringUtils.isNotEmpty(redirectTargetValue)) {
            Page redirectPage = RJSResourceUtils.getPageFromPath(request, redirectTargetValue);
            redirectTarget = new HashMap<>();
            redirectTarget.put("redirectTarget", redirectTargetValue);
            redirectTarget.put("page", redirectPage);
            redirectTarget.put("url", redirectPage != null ? RJSResourceUtils.getPageURL(request, redirectPage) : redirectTargetValue);
        }
    }

}
