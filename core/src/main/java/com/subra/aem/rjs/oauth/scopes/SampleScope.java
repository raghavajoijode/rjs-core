package com.subra.aem.rjs.oauth.scopes;

import com.adobe.granite.oauth.server.Scope;
import org.apache.jackrabbit.api.security.user.User;
import org.osgi.service.component.annotations.Component;

import javax.servlet.http.HttpServletRequest;

@Component(service = Scope.class, immediate = true, enabled = false)
public class SampleScope implements Scope {

    @Override
    public String getDescription(HttpServletRequest request) {
        return "Trigger XYZ Servlet";
    }

    @Override
    public String getName() {
        return "sample_scope";
    }

    @Override
    public String getResourcePath(User user) {
        return null;
    }

    @Override
    public String getEndpoint() {
        return "/bin/rjs/env/info";
    }

}