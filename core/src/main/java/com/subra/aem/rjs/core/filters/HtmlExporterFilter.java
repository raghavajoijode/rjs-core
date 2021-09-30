package com.subra.aem.rjs.core.filters;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.contentsync.PageExporter;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.EngineConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceVendor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.exceptions.RJSRuntimeException;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Component(service = Filter.class, property = {Constants.SERVICE_DESCRIPTION
        + "=Service servlet filter component that manipulates incoming requests that matches the pattern and checks if it is valid JSON file then sends it as response",
        EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
        Constants.SERVICE_RANKING + "=-700",
        "sling.filter.pattern=" + HtmlExporterFilter.EXPORT_HTML_PREFIX + "(.*)" + HtmlExporterFilter.ZIP_EXTENSION})

@ServiceDescription("Html pageExporter filter filter incoming requests")
@ServiceVendor("Subra")
public class HtmlExporterFilter implements Filter {

    static final String ZIP_EXTENSION = ".zip";

    static final String EXPORT_HTML_PREFIX = "/export/html/";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private PageExporter pageExporter;

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        try (ResourceResolver resourceResolver = slingRequest.getResourceResolver()) {
            final String reqType = slingRequest.getParameter("requestType");
            // Getting Page path and page...
            final String requestPagePath = slingRequest.getRequestPathInfo().getResourcePath()
                    .replace(EXPORT_HTML_PREFIX, "/").replace(ZIP_EXTENSION, "").trim();
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Page page = pageManager.getPage(requestPagePath);
            if (page == null) {
                response.getWriter().write("Page DOsent exist");
                throw new RJSRuntimeException("Page Not Found...");
            }
            response.setContentType("application/zip");
            response.addHeader("Content-Disposition", "attachment;filename=\"" + page.getName()
                    + System.currentTimeMillis() + ZIP_EXTENSION + "\"");

            if (reqType != null && reqType.equalsIgnoreCase("dam"))
                exportPageViaDamFile(page, resourceResolver, response);
            else
                exportPageFromResponse(page, resourceResolver, response);

            response.flushBuffer();
        } catch (WCMException we) {
            log.error("WCMException occured.... {}", we.getMessage());
        } catch (RepositoryException | IOException e) {
            log.error("RepositoryException or RepositoryException occured.... {}", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void exportPageFromResponse(Page page, ResourceResolver resourceResolver, HttpServletResponse response) throws WCMException {
        pageExporter.export(page, resourceResolver, response);
    }

    private void exportPageViaDamFile(Page page, ResourceResolver resourceResolver, HttpServletResponse response) throws WCMException, RepositoryException, IOException {
        final String zipFileName = page.getName() + String.valueOf(System.currentTimeMillis()) + ZIP_EXTENSION;
        final String tmpDamFilePath = "/content/dam/foundation/" + zipFileName;
        pageExporter.export(page, resourceResolver, tmpDamFilePath);
        Node contentNode = resourceResolver.getResource(tmpDamFilePath + "/jcr:content").adaptTo(Node.class);
        Property data = contentNode.getProperty("jcr:data");
        OutputStream os = response.getOutputStream();
        try (InputStream is = data.getBinary().getStream()) {
            IOUtils.copy(is, os);
        } finally {
            resourceResolver.delete(resourceResolver.getResource(tmpDamFilePath));
            resourceResolver.commit();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // Nothing to init here
    }

    @Override
    public void destroy() {
        // Nothing to destroy here
    }

}