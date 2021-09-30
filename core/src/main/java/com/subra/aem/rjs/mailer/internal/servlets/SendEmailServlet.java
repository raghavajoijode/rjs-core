package com.subra.aem.rjs.mailer.internal.servlets;

import com.subra.aem.rjs.core.helpers.RequestParser;
import com.subra.aem.rjs.mailer.services.TemplateService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.subra.commons.constants.HttpType;
import org.subra.commons.dtos.mailer.EmailRequest;
import org.subra.commons.helpers.CommonHelper;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Template Demo Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/subra/sendemail"})
public class SendEmailServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -7639144471855594171L;

    @Reference
    transient TemplateService templateService;

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {
        EmailRequest email = RequestParser.getBody(request, EmailRequest.class);
        Map<String, Object> result = templateService.sendEmail(email);
        response.setContentType(HttpType.MEDIA_TYPE_JSON.value());
        response.getWriter().write(CommonHelper.writeValueAsString(result));
    }

}
