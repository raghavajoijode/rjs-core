package com.subra.aem.rjs.mailer.internal.servlets;

import com.subra.aem.rjs.mailer.Template;
import com.subra.aem.rjs.mailer.services.TemplateService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.constants.HttpType;
import org.subra.commons.dtos.mailer.EmailRequest;
import org.subra.commons.exceptions.RJSCustomException;
import org.subra.commons.helpers.CommonHelper;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Raghava Joijode
 * <p>
 * Servlet to operate with templates
 * <p>
 * /bin/subra/template
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Template Demo Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/subra/template"})

public class TemplateServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -7639144471855594171L;
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateServlet.class);

    @Reference
    transient TemplateService templateService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {
        final String action = StringUtils.defaultIfEmpty(request.getParameter("action"), StringUtils.EMPTY);
        try {
            switch (action) {
                case "create":
                    processCreateOrUpdateTemplate(request, response);
                    break;
                case "generate-json":
                    processGenerateRequestFormat(request, response);
                    break;
                case "read-content":
                    processReadTemplate(request, response);
                    break;
                default:
                    processListTemplates(response);
                    break;
            }
        } catch (IOException | RJSCustomException e) {
            LOGGER.error("Error processing servlet...", e);
        }
    }

    private void processListTemplates(final SlingHttpServletResponse response) throws IOException {
        response.setContentType(HttpType.MEDIA_TYPE_JSON.value());
        response.getWriter().write(CommonHelper.writeValueAsString(templateService.listTemplates()));
    }

    private void processCreateOrUpdateTemplate(final SlingHttpServletRequest request,
                                               final SlingHttpServletResponse response) throws IOException {
        final String fileTitle = request.getParameter("title");
        final String content = request.getParameter("content");
        EmailRequest emailRequest = templateService.createOrUpdateTemplate(fileTitle, content);
        response.setContentType(HttpType.MEDIA_TYPE_JSON.value());
        response.getWriter().write(emailRequest.toString());
    }

    private void processReadTemplate(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException, RJSCustomException {
        final String id = request.getParameter("id");
        response.setContentType(HttpType.MEDIA_TYPE_TEXT.value());
        response.getWriter().write(templateService.readTemplate(getTemplate(id)));
    }

    private void processGenerateRequestFormat(final SlingHttpServletRequest request,
                                              final SlingHttpServletResponse response) throws IOException, RJSCustomException {
        final String id = request.getParameter("id");
        response.setContentType(HttpType.MEDIA_TYPE_JSON.value());
        EmailRequest emailRequest = templateService.generateRequestFormat(getTemplate(id));
        response.getWriter().write(emailRequest.toString());
    }

    private Template getTemplate(final String id) throws RJSCustomException {
        return templateService.getTemplate(id);
    }

}
