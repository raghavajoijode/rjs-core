package com.subra.aem.rjs.account.servlets;

import com.subra.aem.rjs.account.services.MyAEMUserService;
import com.subra.aem.rjs.account.services.UserService;
import com.subra.aem.rjs.core.helpers.RequestParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Email Demo Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.paths=" + "/bin/subra/user/create-account"})
public class CreateAccountServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -7639144471855594170L;

    private static final String REQUEST_TYPE = "type";
    private static final String CREATE_NEW = "create";
    private static final String VALIDATE_USERNAME = "validate";

    private static final String USERNAME = "email";
    private static final String PWD = "pwd";
    private static final String NAME = "name";

    @Reference
    transient MyAEMUserService userAemService;

    @Reference
    transient UserService userService;

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        final String requestType = RequestParser.getParameter(request, REQUEST_TYPE);
        final boolean isExistingUser = isExistingUser(RequestParser.getParameter(request, USERNAME));
        if (StringUtils.equalsIgnoreCase(requestType, VALIDATE_USERNAME) && isExistingUser) {
            response.getWriter().write("Error crating user - User already exists");
        } else if (StringUtils.equalsIgnoreCase(requestType, CREATE_NEW) && !isExistingUser && isExistingUser(createUser(request))) {
            response.getWriter().write("User Created Succesfully");
        }
    }

    private boolean isExistingUser(final String userName) {
        return StringUtils.isNotBlank(userName) && userService.isExistingUser(userName);
    }

    private String createUser(final SlingHttpServletRequest request) {
        return userService.createUser(RequestParser.getParameter(request, USERNAME), RequestParser.getParameter(request, PWD), RequestParser.getParameter(request, NAME));
    }
}
