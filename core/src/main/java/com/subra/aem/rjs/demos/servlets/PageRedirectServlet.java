package com.subra.aem.rjs.demos.servlets;


import com.day.cq.wcm.api.WCMMode;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * property = {
 * Constants.SERVICE_DESCRIPTION + "= RJ CQ Redirect Servlet", "sling.servlet.extensions=" + "html",
 * "sling.servlet.selectors=" + "redirect", "sling.servlet.resourceTypes=" + "cq/Page",
 * Constants.SERVICE_RANKING + "=20"
 * }
 */
@Component(service = Servlet.class)
@SlingServletResourceTypes(selectors = "redirect", extensions = "html", resourceTypes = "cq/Page")
@ServiceRanking(1)
@ServiceDescription("RJ CQ Redirect Servlet")
@Designate(ocd = PageRedirectServlet.Config.class)
public class PageRedirectServlet extends SlingSafeMethodsServlet {

    private static final String WCM_MODE_PARAM = "wcmmode";
    private static final Logger LOGGER = LoggerFactory.getLogger(PageRedirectServlet.class);
    private Set<String> excludedResourceTypes;

    @Activate
    protected void activate(Config config) {
        String[] excludedResourceTypesArray = config.excluded_resource_types();
        this.excludedResourceTypes = new HashSet<>();
        if (!ArrayUtils.isEmpty(excludedResourceTypesArray)) {
            Collections.addAll(this.excludedResourceTypes, excludedResourceTypesArray);
        }
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        Resource resource = request.getResource();
        Resource contentResource = resource.getChild("jcr:content");
        if (contentResource != null) {
            String redirectTarget = Optional.of(contentResource).map(Resource::getValueMap).map(vm -> vm.get("cq:redirectTarget", String.class)).orElse(StringUtils.EMPTY);
            String redirectType = getRedirectType(contentResource);
            if (isRedirectRequest(request, redirectTarget) && !isExcludedResourceType(contentResource)) {
                if (!isExternalRedirect(redirectTarget)) {
                    redirectTarget = resource.getResourceResolver().map(request, redirectTarget) + ".html";
                }
                redirectTarget = appendWcmModeQueryParameter(request, redirectTarget);
                LOGGER.debug("Redirecting page {} to target {}", resource.getPath(), redirectTarget);
                if (redirectType.equals("301")) {
                    response.setStatus(301);
                    response.setHeader("Location", redirectTarget);
                } else {
                    response.sendRedirect(redirectTarget);
                }
                return;
            }

            RequestDispatcherOptions requestDispatcherOptions = new RequestDispatcherOptions();
            String selectorString = request.getRequestPathInfo().getSelectorString();
            selectorString = StringUtils.replace(selectorString, "redirect", "");
            requestDispatcherOptions.setReplaceSelectors(selectorString);

            RequestDispatcher requestDispatcher = request.getRequestDispatcher(contentResource, requestDispatcherOptions);
            if (requestDispatcher != null) {
                requestDispatcher.include(request, response);
            }
        }

    }

    private boolean isExcludedResourceType(Resource contentResource) {
        for (String excludedResourceType : this.excludedResourceTypes) {
            if (contentResource.isResourceType(excludedResourceType)) {
                return true;
            }
        }
        return false;
    }

    private String appendWcmModeQueryParameter(SlingHttpServletRequest request, String redirectTarget) {
        if (isModeDisabledChangeRequest(request)) {
            redirectTarget = redirectTarget + (redirectTarget.contains("?") ? "&" : "?") + WCM_MODE_PARAM + "=disabled";
        }
        return redirectTarget;
    }

    private boolean isModeDisabledChangeRequest(SlingHttpServletRequest request) {
        boolean isModeChangeRequest = false;
        String modeChange = request.getParameter(WCM_MODE_PARAM);
        if (StringUtils.equalsIgnoreCase(modeChange, WCMMode.DISABLED.name())) {
            isModeChangeRequest = true;
        }
        return isModeChangeRequest;
    }

    private boolean isExternalRedirect(String redirectTarget) {
        boolean externalRedirect = false;
        try {
            URL url = new URL(redirectTarget);
            String protocol = url.getProtocol();
            if (StringUtils.isNotBlank(protocol)) {
                externalRedirect = true;
            }
        } catch (MalformedURLException e) {
            return false;
        }
        return externalRedirect;
    }

    private String getRedirectType(Resource resource) {
        ValueMap valueMap = resource.adaptTo(ValueMap.class);
        String redirectTarget = "";
        if (valueMap != null) {
            redirectTarget = valueMap.get("redirectType", "");
        }
        return redirectTarget;
    }

    private boolean isRedirectRequest(SlingHttpServletRequest request, String redirectTarget) {
        WCMMode wcmMode = WCMMode.fromRequest(request);
        return StringUtils.isNotEmpty(redirectTarget) && wcmMode.equals(WCMMode.DISABLED);
    }

    @ObjectClassDefinition(name = "RJ CQ Redirect Servlet")
    @interface Config {
        @AttributeDefinition(name = "Excluded resource types", description = "List of resource types which should be ignored by this redirect servlet.")
        String[] excluded_resource_types();
    }
}