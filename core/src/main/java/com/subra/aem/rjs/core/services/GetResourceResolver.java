package com.subra.aem.rjs.core.services;

import com.subra.aem.rjs.core.UserMapperService;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

public interface GetResourceResolver {

    ResourceResolver getAdminResourceResolver() throws LoginException;

    ResourceResolver getWorkflowResourceResolver() throws LoginException;

    ResourceResolver getServiceUserResourceResolver(UserMapperService userMapperService) throws LoginException;

    ResourceResolver getUserResourceResolver(String subServiceName) throws LoginException;
}
