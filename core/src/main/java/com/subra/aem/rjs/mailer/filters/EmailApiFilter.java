package com.subra.aem.rjs.mailer.filters;

import com.subra.aem.rjs.core.helpers.RequestParser;
import com.subra.aem.rjs.mailer.services.TemplateService;
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
import org.subra.commons.dtos.mailer.EmailRequest;
import org.subra.commons.helpers.CommonHelper;

import javax.servlet.*;
import java.io.IOException;

/**
 * Sling Servlet Filter Api for Sending Email
 */
@Component
@SlingServletFilter(scope = SlingServletFilterScope.REQUEST, pattern = EmailApiFilter.PATTERN, methods = HttpConstants.METHOD_POST)
@ServiceDescription("Subra Mailer Email Api")
@ServiceRanking(-700)
@ServiceVendor("RJS")
public class EmailApiFilter implements Filter {

    protected static final String PATTERN = "/api/subra/mailer/v1/sendemail";
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailApiFilter.class);
    @Reference
    TemplateService templateService;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        EmailRequest emailRequest = CommonHelper.convertToClass(RequestParser.getBody(slingRequest),
                EmailRequest.class);
        response.setContentType(HttpType.MEDIA_TYPE_JSON.value());
        response.getWriter().write(CommonHelper.writeValueAsString(templateService.sendEmail(emailRequest)));
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        LOGGER.debug("MailerApiFilter initialised...");
    }

    @Override
    public void destroy() {
        LOGGER.debug("MailerApiFilter destroyed...");
    }

}