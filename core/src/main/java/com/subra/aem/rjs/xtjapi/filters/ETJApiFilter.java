package com.subra.aem.rjs.xtjapi.filters;

import com.day.cq.dam.api.Asset;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.subra.aem.rjs.xtjapi.services.ExcelToJsonService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.engine.EngineConstants;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author Raghava Joijode
 */
@Component(service = Filter.class, property = {Constants.SERVICE_DESCRIPTION
        + "=Service servlet filter component that manipulates incoming requests that matches the pattern and checks if it is valid JSON file then sends it as response",
        EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
        Constants.SERVICE_RANKING + "=-700", "sling.filter.pattern=/api/subra/core/xtj/.*"})
@Designate(ocd = ETJApiFilter.Config.class)
public class ETJApiFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ETJApiFilter.class);
    @Reference
    ExcelToJsonService excelToJsonService;
    private String xtjInRootPath;
    private String xtjOutRootPath;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;
        slingResponse.setContentType("application/json");

        String extension = slingRequest.getRequestPathInfo().getExtension();
        String regex = extension != null ? "." + extension : "";
        String assetFinalPath = xtjOutRootPath + slingRequest.getRequestPathInfo().getResourcePath().replace(regex, "");
        assetFinalPath = assetFinalPath.trim();
        getJsonFromJSONAsset(assetFinalPath, slingRequest);
        String jsonString = excelToJsonService.getJsonFromExcel(xtjInRootPath + "product_simple.xlsx");
        if (extension != null && extension.contains("xml")) {
            slingResponse.setContentType("application/xml");
            slingResponse.getWriter().print(getXMLFromJSON(jsonString));
        } else {
            slingResponse.getWriter().print(jsonString);
        }
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
        xtjInRootPath = config.xtjInPath();
        xtjOutRootPath = config.xtjOutPath();
    }

    private JsonObject getJsonFromJSONAsset(String assetFinalPath, SlingHttpServletRequest slingRequest) {
        JsonObject returnObject = new JsonObject();
        Resource fileResource = slingRequest.getResourceResolver().getResource(assetFinalPath + ".json"); // Converting
        try {
            if (fileResource != null && ("application/json").equals(fileResource.adaptTo(Asset.class).getMimeType())) {
                Asset asset = fileResource.adaptTo(Asset.class);
                returnObject = getJsonFromAsset(asset);
            } else {
                returnObject.addProperty("ERROR", "JSON_FILE_MISSING_" + assetFinalPath);
            }
        } catch (JsonParseException e) {
            LOGGER.error("JsonParseException - {}", e.getMessage());
        }
        return returnObject;
    }

    private JsonObject getJsonFromAsset(Asset asset) {
        JsonObject returnObject = new JsonObject();
        try (InputStreamReader is = new InputStreamReader(asset.getOriginal().getStream(), StandardCharsets.UTF_8)) {
            returnObject = (JsonObject) new JsonParser().parse(is);
        } catch (IOException e) {
            LOGGER.error("IOException_OCCURED_In {} . The Exception is - {}", this.getClass().getName(),
                    e.getMessage());
        }
        return returnObject;
    }

    private String getXMLFromJSON(String jsonString) {
        String xml = null;
        try {
            xml = XML.toString(new JSONObject(jsonString));
        } catch (JSONException e) {
            LOGGER.error("Exception occured at getXMLFromJSON - {}", e.getMessage());
        }
        return xml;
    }

    @ObjectClassDefinition(name = "XTJ API Filter In/Out dam path configurations")
    public @interface Config {
        @AttributeDefinition(name = "Input Excel Dam Path root path", description = "Path where input excels are stored; ex: = '/content/dam/core/api/xtj/in/excel/product_simple.xlsx'", type = AttributeType.STRING)
        String xtjInPath() default "/content/dam/core/api/xtj/in/excel/";

        @AttributeDefinition(name = "Output JSON Dam Path root path", description = "Path where output jsons are stored; ex: = '/content/dam/core/api/xtj/out/excel/product_simple.json'", type = AttributeType.STRING)
        String xtjOutPath() default "/content/dam/core/api/xtj/out/json/";
    }

}