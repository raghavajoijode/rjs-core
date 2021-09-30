package com.subra.aem.rjs.authoring.rendercondition.filters;

import com.subra.aem.rjs.authoring.AuthoringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.servlets.annotations.SlingServletFilter;
import org.apache.sling.servlets.annotations.SlingServletFilterScope;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.*;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;


@Component(service = Filter.class, enabled = false)
@SlingServletFilter(scope = SlingServletFilterScope.REQUEST, pattern = "/mnt/overlay/wcm/core/content/sites/properties")
@ServiceDescription("Filter To Handle Page Properties access")
public class PagePropertiesFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PagePropertiesFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) servletRequest;
        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) servletResponse;
        LOGGER.debug("request for {}, with parameter {}", slingRequest.getRequestPathInfo().getResourcePath(), slingRequest.getRequestParameter("item"));
        Optional.of(slingRequest).map(SlingHttpServletRequest::getResourceResolver)
                .map(resourceResolver -> resourceResolver.adaptTo(Authorizable.class)).ifPresent(authorizable -> {
            try {
                Iterator<Group> memberGroups = authorizable.declaredMemberOf();
                while (memberGroups.hasNext()) {
                    Group g = memberGroups.next();
                    if (StringUtils.equalsIgnoreCase(g.getID(), "nopropertyaccess")) {
                        Cookie cookie = new Cookie("authorized", "false");
                        cookie.setPath("/");
                        slingResponse.addCookie(cookie);
                        slingResponse.sendRedirect("/editor.html" + AuthoringUtils.getPagePathForDialogRequest(slingRequest) + ".html");
                        break;
                    }
                }
            } catch (RepositoryException | IOException e) {
                LOGGER.error("Exception accessing memberGroups and redirecting accordingly", e);
            } finally {
                try {
                    filterChain.doFilter(servletRequest, servletResponse);
                } catch (IOException | ServletException e) {
                    LOGGER.error("Exception processing doFilter", e);
                }
            }
        });

    }

    @Override
    public void destroy() {
        // Do nothing
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Do nothing
    }
}