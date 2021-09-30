package com.subra.aem.rjs.mailer.internal.servlets;

import com.subra.aem.rjs.mailer.Template;
import com.subra.aem.rjs.mailer.services.MailerService;
import com.subra.aem.rjs.mailer.utils.MailerUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.subra.commons.constants.HttpType;
import org.subra.commons.helpers.CommonHelper;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Email Demo Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/rjs/demo/email"})
public class DemoEmailServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = -7639144471855594170L;

    @Reference
    transient MailerService emailService;

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
            throws ServletException, IOException {
        Map<String, String> emailParams = new HashMap<>();
        emailParams.put(MailerUtils.SUBJECT, "Test Mail");
        emailParams.put(MailerUtils.TO, "raghava.joijode@gmail.com");
        // Map<String, Object> response = emailService.sendEmail(
        //        "/conf/foundation/settings/notification/email/subra/sample.html", emailParams,
        //        "raghava.joijode@gmail.com", "angelsubhashree@gmail.com")
        resp.setContentType(HttpType.MEDIA_TYPE_JSON.value());
        resp.getWriter().write(CommonHelper.writeValueAsString(emailService.sendEmail(new Template("*************** Test Message ***************"), emailParams, null, null)));
    }
}
