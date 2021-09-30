package com.subra.aem.rjs.core.helpers;

import org.subra.commons.constants.HttpType;

import java.util.Map;

public interface SubraGenericService {

    void sendExceptionMessage(final String className, final String message);

    void sendElectronicMessage(final String subject, final String message);

    <T> T callBackendService(final String endpointUrl, final String resource, final Map<String, String> requestHeaders,
                             final Map<String, String> queryParams, final Object postObject, final HttpType requestType);

}
