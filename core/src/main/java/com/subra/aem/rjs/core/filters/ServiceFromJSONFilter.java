package com.subra.aem.rjs.core.filters;

import com.day.cq.dam.api.Asset;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.engine.EngineConstants;
import org.json.JSONException;
import org.json.XML;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.constants.HttpType;

import javax.servlet.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component(service = Filter.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Service servlet filter component that manipulates incoming requests that matches the pattern and checks if it is valid JSON file then sends it as response",
                EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
                Constants.SERVICE_RANKING + "=-700",
                "sling.filter.pattern=/services/api/.*"
        })
@Designate(ocd = ServiceFromJSONFilter.Config.class)
public class ServiceFromJSONFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private String rootDAMPath;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;
        String extension = slingRequest.getRequestPathInfo().getExtension();
        String assetFinalPath = rootDAMPath + slingRequest.getRequestPathInfo().getResourcePath().replace(extension != null ? "." + extension : "", "");
        assetFinalPath = assetFinalPath.trim();
        boolean isXMLRequest = extension != null && extension.contains("xml");
        slingResponse.setContentType(isXMLRequest ? HttpType.MEDIA_TYPE_XML.value() : HttpType.MEDIA_TYPE_JSON.value());
        slingResponse.getWriter().print(createResponse(assetFinalPath, slingRequest, isXMLRequest));
    }

    @Override
    public void init(FilterConfig filterConfig) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException();
    }

    @Activate
    @Modified
    protected void activate(Config config) {
        rootDAMPath = config.servicesBasePath();
    }

    private String createResponse(String assetFinalPath, SlingHttpServletRequest slingRequest, boolean isXMLRequest) {
        JsonObject returnObject = new JsonObject();
        Resource fileResource = slingRequest.getResourceResolver().getResource(assetFinalPath + ".json"); // Converting
        try {
            // validating if valid asset exists with above path and its of type JSON.
            if (fileResource != null && (HttpType.MEDIA_TYPE_JSON.value()).equals(fileResource.adaptTo(Asset.class).getMimeType())) {
                returnObject = getJsonFromAsset(fileResource.adaptTo(Asset.class));
            } else {
                returnObject.addProperty("ERROR", "JSON_FILE_MISSING_" + assetFinalPath);
            }
        } catch (JsonParseException e) {
            log.error("JSONException_OCCURED_In {} . The Exception is - {}", this.getClass().getName(), e.getMessage());
        }
        return isXMLRequest ? getXMLFromJSON(returnObject) : returnObject.toString();
    }

    private JsonObject getJsonFromAsset(Asset asset) {
        try (InputStreamReader is = new InputStreamReader(asset.getOriginal().getStream(), StandardCharsets.UTF_8)) {
            return (JsonObject) new JsonParser().parse(is);
        } catch (IOException e) {
            log.error("IOException_OCCURED_In {} . The Exception is - {}", this.getClass().getName(), e.getMessage());
        }
        return new JsonObject();
    }

    private String getXMLFromJSON(JsonObject returnObject) {
        try {
            return XML.toString(returnObject);
        } catch (JSONException e) {
            log.error("JSONException_OCCURED_In {} . The Exception is - {}", this.getClass().getName(), e.getMessage());
        }
        return StringUtils.EMPTY;
    }

    @ObjectClassDefinition(name = "Service Filter Property Configurations")
    public @interface Config {
        @AttributeDefinition(name = "Services Dam Path", description = "Path where service jsons are stored; ex: = '/content/dam/demo-services'", type = AttributeType.STRING)
        String servicesBasePath() default "/content/dam";
    }

}