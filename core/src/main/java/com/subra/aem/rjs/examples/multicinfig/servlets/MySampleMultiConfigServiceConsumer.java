package com.subra.aem.rjs.examples.multicinfig.servlets;

import com.subra.aem.rjs.examples.multicinfig.services.MyTestSampleMultiConfigService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "=MySampleMultiConfigServiceConsumer Demo Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=" + "/bin/mytestsamplemulticonfigdata"})
public class MySampleMultiConfigServiceConsumer extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(MySampleMultiConfigServiceConsumer.class);
    private transient List<MyTestSampleMultiConfigService> configurationList;

    /**
     * Executed on Configuration Add event
     *
     * @param config New configuration for factory
     */
    @Reference(name = "myTestSampleMultiConfigService", cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    protected synchronized void bindMyTestSampleMultiConfigService(final MyTestSampleMultiConfigService config) {
        LOGGER.info("[Raghava]--> bindConfigurationFactory: {}", config.getMemberName());
        if (configurationList == null) {
            configurationList = new ArrayList<>();
        }
        configurationList.add(config);
    }

    /**
     * Executed on Configuration Remove event
     *
     * @param config New configuration for factory
     */
    protected synchronized void unbindMyTestSampleMultiConfigService(final MyTestSampleMultiConfigService config) {
        LOGGER.info("unbindConfigurationFactory: {}", config.getMemberName());
        configurationList.remove(config);
    }

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
            throws ServletException, IOException {
        if (configurationList != null) {
            resp.getWriter().println("Total Items = " + configurationList.size());
            for (MyTestSampleMultiConfigService myTestSampleMultiConfigService : configurationList) {
                resp.getWriter()
                        .println("Name = " + myTestSampleMultiConfigService.getMemberName() + " " + "place = "
                                + myTestSampleMultiConfigService.getMemberPlace() + " " + "PIN = "
                                + myTestSampleMultiConfigService.getMemberPIN());
            }
        } else {
            resp.getWriter().println("Zero Items");
        }
    }
}
