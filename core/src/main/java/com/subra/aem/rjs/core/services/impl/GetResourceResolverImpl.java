package com.subra.aem.rjs.core.services.impl;

import com.subra.aem.rjs.core.UserMapperService;
import com.subra.aem.rjs.core.services.GetResourceResolver;
import com.subra.aem.rjs.core.utils.RJSResourceUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Collections;

@Component(service = GetResourceResolver.class, immediate = true)
public class GetResourceResolverImpl implements GetResourceResolver {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public ResourceResolver getAdminResourceResolver() throws LoginException {
        return resourceResolverFactory.getServiceResourceResolver(RJSResourceUtils.getAuthInfo(UserMapperService.ADMIN_SERVICE));
    }

    @Override
    public ResourceResolver getWorkflowResourceResolver() throws LoginException {
        return getUserResourceResolver(UserMapperService.WORKFLOW.value());
    }

    @Override
    public ResourceResolver getServiceUserResourceResolver(UserMapperService userMapperService) throws LoginException {
        return resourceResolverFactory.getServiceResourceResolver(RJSResourceUtils.getAuthInfo(userMapperService));
    }

    @Override
    public ResourceResolver getUserResourceResolver(final String subServiceName) throws LoginException {
        return resourceResolverFactory.getServiceResourceResolver(subServiceName != null ? Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, subServiceName) : null);
    }
}
