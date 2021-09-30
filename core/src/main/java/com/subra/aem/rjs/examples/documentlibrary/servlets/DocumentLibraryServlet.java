package com.subra.aem.rjs.examples.documentlibrary.servlets;

import com.subra.aem.rjs.examples.documentlibrary.services.DocumentLibraryService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=DocumentLibraryServlet Demo Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=" + "/bin/documentLibrary"
        })
public class DocumentLibraryServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    private final transient Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private transient DocumentLibraryService documentLibraryService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        String path = request.getParameter("path");
        log.info("path of the clicked folder is : {}", path);
        Resource imageFolder = request.getResourceResolver().getResource(path);
        String jsonResponse = "{}";
        try {
            if (imageFolder != null) documentLibraryService.extractAssetList(imageFolder).toString();
        } catch (JSONException e) {
            log.error("-- DocumentLibraryServlet Error", e);
        }
        log.info("-- DocumentLibraryServlet {} ", jsonResponse);
        response.getWriter().write(jsonResponse);
    }

}
