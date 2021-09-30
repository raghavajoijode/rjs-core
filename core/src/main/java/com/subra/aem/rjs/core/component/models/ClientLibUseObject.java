package com.subra.aem.rjs.core.component.models;

import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.scripting.sightly.pojo.Use;
import org.apache.sling.xss.XSSAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientLibUseObject implements Use {

    private static final String BINDINGS_CATEGORIES = "categories";
    private static final String BINDINGS_MODE = "mode";
    private static final String BINDINGS_LOADING = "loading";
    private static final String BINDINGS_ON_LOAD = "onload";
    private static final String BINDINGS_CROSS_ORIGIN = "crossorigin";
    private static final String END_TAG_JAVASCRIPT = "></script>";
    private static final String END_TAG_STYLESHEET = "type=\"text/css\">";
    private static final String ON_LOAD_ATTRIBUTE = " onload=\"%s\"";
    private static final String CROSS_ORIGIN_ATTRIBUTE = " crossorigin=\"%s\"";
    private static final List<String> VALID_JS_ATTRIBUTES = new ArrayList<>();
    private static final List<String> VALID_CROSS_ORIGIN_VALUES = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientLibUseObject.class);
    private HtmlLibraryManager htmlLibraryManager = null;
    private String[] categories;
    private String mode;
    private String loadingAttribute;
    private String onloadAttribute;
    private String crossoriginAttribute;
    private SlingHttpServletRequest request;
    private XSSAPI xssAPI;

    @Override
    public void init(Bindings bindings) {
        Object categoriesObject = bindings.get(BINDINGS_CATEGORIES);
        loadingAttribute = (String) bindings.get(BINDINGS_LOADING);
        onloadAttribute = (String) bindings.get(BINDINGS_ON_LOAD);
        crossoriginAttribute = (String) bindings.get(BINDINGS_CROSS_ORIGIN);
        VALID_JS_ATTRIBUTES.add("async");
        VALID_JS_ATTRIBUTES.add("defer");
        VALID_CROSS_ORIGIN_VALUES.add("anonymous");
        VALID_CROSS_ORIGIN_VALUES.add("use-credentials");

        if (categoriesObject != null) {
            bindCategories(bindings, categoriesObject);
        }
    }

    private void bindCategories(Bindings bindings, Object categoriesObject) {
        if (categoriesObject instanceof Object[]) {
            Object[] categoriesArray = (Object[]) categoriesObject;
            categories = new String[categoriesArray.length];
            int i = 0;
            for (Object o : categoriesArray) {
                if (o instanceof String)
                    categories[i++] = ((String) o).trim();
            }
        } else if (categoriesObject instanceof String) {
            categories = ((String) categoriesObject).split(",");
            int i = 0;
            for (String c : categories) {
                categories[i++] = c.trim();
            }
        }
        if (categories != null && categories.length > 0) {
            mode = (String) bindings.get(BINDINGS_MODE);
            request = (SlingHttpServletRequest) bindings.get(SlingBindings.REQUEST);
            SlingScriptHelper sling = (SlingScriptHelper) bindings.get(SlingBindings.SLING);
            htmlLibraryManager = sling.getService(HtmlLibraryManager.class);
            xssAPI = sling.getService(XSSAPI.class);
        }
    }

    public String include() {
        StringWriter sw = new StringWriter();
        try {
            if (categories == null || categories.length == 0) {
                LOGGER.error(
                        "'categories' option might be missing from the invocation of the /libs/granite/sightly/templates/clientlib.html"
                                + "client libraries template library. Please provide a CSV list or an array of categories to include.");
            } else {
                PrintWriter out = new PrintWriter(sw);
                if ("js".equalsIgnoreCase(mode)) {
                    htmlLibraryManager.writeJsInclude(request, out, categories);
                } else if ("css".equalsIgnoreCase(mode)) {
                    htmlLibraryManager.writeCssInclude(request, out, categories);
                } else {
                    htmlLibraryManager.writeIncludes(request, out, categories);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to include client libraries {}", Arrays.toString(categories));
        }
        return sw.toString();
    }

    public String includeMarkUp() {
        StringWriter sw = new StringWriter();
        try {
            if (categories == null || categories.length == 0) {
                LOGGER.error(
                        "'categories' option might be missing from the invocation of the /libs/granite/sightly/templates/clientlib.html"
                                + "client libraries template library. Please provide a CSV list or an array of categories to include.");
            } else {
                PrintWriter out = new PrintWriter(sw);
                if ("js".equalsIgnoreCase(mode))
                    htmlLibraryManager.writeJsInclude(request, out, categories);
                else if ("css".equalsIgnoreCase(mode))
                    htmlLibraryManager.writeCssInclude(request, out, categories);
                else
                    htmlLibraryManager.writeIncludes(request, out, categories);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to include client libraries {}", Arrays.toString(categories));
        }
        return updateWithAttributes(sw.toString());
    }

    private String updateWithAttributes(String tags) {
        String attributesJS = setAttributes(LibraryType.JS);
        String attributesCSS = setAttributes(LibraryType.CSS);
        String[] searchList = new String[]{END_TAG_JAVASCRIPT, END_TAG_STYLESHEET};
        String[] replacementList = new String[]{attributesJS + END_TAG_JAVASCRIPT, attributesCSS + StringUtils.SPACE + END_TAG_STYLESHEET};
        return StringUtils.replaceEach(tags, searchList, replacementList);
    }

    private String setAttributes(LibraryType libraryType) {
        StringBuilder sb = new StringBuilder();
        if (libraryType.equals(LibraryType.JS)) {
            if (StringUtils.isNotBlank(loadingAttribute)
                    && VALID_JS_ATTRIBUTES.contains(loadingAttribute.toLowerCase())) {
                sb.append(StringUtils.SPACE).append(loadingAttribute.toLowerCase());
            }
            if (StringUtils.isNotBlank(onloadAttribute)) {
                final String safeOnLoad = xssAPI.encodeForHTMLAttr(onloadAttribute);
                if (StringUtils.isNotBlank(safeOnLoad)) {
                    sb.append(String.format(ON_LOAD_ATTRIBUTE, safeOnLoad));
                }
            }
        }
        if (StringUtils.isNotBlank(crossoriginAttribute)
                && VALID_CROSS_ORIGIN_VALUES.contains(crossoriginAttribute.toLowerCase())) {
            sb.append(String.format(CROSS_ORIGIN_ATTRIBUTE, crossoriginAttribute.toLowerCase()));
        }
        return sb.toString();
    }

}
