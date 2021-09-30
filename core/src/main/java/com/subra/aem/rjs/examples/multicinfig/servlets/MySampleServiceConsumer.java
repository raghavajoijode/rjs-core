package com.subra.aem.rjs.examples.multicinfig.servlets;

import com.subra.aem.rjs.examples.multicinfig.services.MyTestSampleService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "=MySampleServiceConsumer Demo Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/mytestsampledata"})
public class MySampleServiceConsumer extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(MySampleServiceConsumer.class);

    @Reference
    private transient MyTestSampleService myTestSampleService;

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
            throws ServletException, IOException {
        LOGGER.info("[RAGHAVA]In Servlet Doget ");
        resp.getWriter().write("Name = " + myTestSampleService.getName() + "  " + "Place = "
                + myTestSampleService.getPlace() + "  " + "PIN = " + myTestSampleService.getPIN());
    }
}
