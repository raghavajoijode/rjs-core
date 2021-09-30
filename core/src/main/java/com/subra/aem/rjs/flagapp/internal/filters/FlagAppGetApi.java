package com.subra.aem.rjs.flagapp.internal.filters;

import com.subra.aem.rjs.flagapp.internal.services.FlagService;
import com.subra.aem.rjs.flagapp.internal.utils.FlagAppUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.servlets.annotations.SlingServletFilter;
import org.apache.sling.servlets.annotations.SlingServletFilterScope;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.component.propertytypes.ServiceVendor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.constants.HttpType;
import org.subra.commons.exceptions.RJSRuntimeException;
import org.subra.commons.helpers.CommonHelper;

import javax.servlet.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sling Servlet Filter Api for templates
 */
@Component
@SlingServletFilter(scope = SlingServletFilterScope.REQUEST, pattern = FlagAppUtils.FLAP_APP_GET_API_PATTERN, methods = HttpConstants.METHOD_GET)
@ServiceDescription("RJSFlagApp Get Api")
@ServiceRanking(-700)
@ServiceVendor("RJS")
public class FlagAppGetApi implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlagAppGetApi.class);

    @Reference
    FlagService fms;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        response.setContentType(HttpType.MEDIA_TYPE_JSON.value());
        Pattern pattern = Pattern.compile(FlagAppUtils.FLAP_APP_GET_API_PATTERN);
        Matcher matcher = pattern.matcher(slingRequest.getRequestURI());
        Map<String, Object> result = new HashMap<>();
        if (matcher.matches()) {
            try {
                result = processOperation(matcher);
            } catch (RJSRuntimeException e) {
                setFailure(result, e.getMessage());
            }
        } else {
            result.put(FlagAppUtils.RS_STATUS, FlagAppUtils.PATTERN_ERROR);
        }
        response.getWriter().write(CommonHelper.writeValueAsString(result));
    }

    private Map<String, Object> processOperation(final Matcher matcher) {
        Map<String, Object> result = new HashMap<>();
        result.put(FlagAppUtils.RS_STATUS, FlagAppUtils.SUCCESS);
        final String target = matcher.group(FlagAppUtils.TARGET);
        final String operation = matcher.group(FlagAppUtils.ACTION);
        String projectName = matcher.group(FlagAppUtils.PROJECT_NAME_GROUP);
        String flagName = matcher.group(FlagAppUtils.FLAG_NAME_GROUP);
        if (StringUtils.isNoneBlank(target, operation, projectName)) {
            projectName = StringUtils.replace(projectName, "%20", StringUtils.SPACE);
            flagName = StringUtils.replace(flagName, "%20", StringUtils.SPACE);
            switch (operation) {
                case "read":
                    processRead(result, target, projectName, flagName);
                    break;
                case "delete":
                    processDelete(result, target, projectName, flagName);
                    break;
                default:
                    throw new UnsupportedOperationException(operation + " is not supported...");
            }
        } else if (StringUtils.equalsIgnoreCase(operation, "read")
                && !StringUtils.equalsIgnoreCase(target, FlagAppUtils.FLAG)) {
            result.put(FlagAppUtils.RS_DATA, fms.projects());
        } else {
            setFailure(result, "Unsupported Operation...");
        }
        return result;
    }

    private void setFailure(Map<String, Object> result, String message) {
        result.put(FlagAppUtils.RS_STATUS, FlagAppUtils.FAILURE);
        result.put(FlagAppUtils.RS_FAILURE_REASON, message);
    }

    private void processRead(final Map<String, Object> result, final String target, final String projectName,
                             final String flagName) {
        if (StringUtils.equalsIgnoreCase(target, FlagAppUtils.FLAG))
            result.put(FlagAppUtils.RS_DATA, StringUtils.isBlank(flagName) ? fms.projectFlags(projectName)
                    : fms.readFlag(projectName, flagName));
        else
            result.put(FlagAppUtils.RS_DATA, fms.readProject(projectName));
    }

    private void processDelete(final Map<String, Object> result, final String target, final String projectName,
                               final String flagName) {
        if (StringUtils.equalsIgnoreCase(target, FlagAppUtils.FLAG)) {
            Object value = fms.readFlag(projectName, flagName);
            if (value != null)
                fms.deleteFlag(projectName, flagName);
            result.put(FlagAppUtils.RS_DATA, value);
        } else {
            fms.deleteProject(projectName);
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        LOGGER.debug("FlagAppUtils initialised...");
    }

    @Override
    public void destroy() {
        LOGGER.debug("FlagAppUtils destroyed...");
    }

}