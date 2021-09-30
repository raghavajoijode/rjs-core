package com.subra.aem.rjs.core.helpers.impl;

import com.subra.aem.rjs.core.helpers.SubraGenericService;
import com.subra.aem.rjs.restclient.services.RestClientService;
import org.apache.http.HttpHeaders;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.constants.HttpType;
import org.subra.commons.dtos.Response;
import org.subra.commons.exceptions.RJSApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component(service = SubraGenericService.class, immediate = true)
@ServiceDescription("Subra - Generic Service")
public class SubraGenericServiceImpl implements SubraGenericService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubraGenericServiceImpl.class);
    // private static final String ADMIN_RECIPENT_EMAIL = "raghava.joijode@gmail.com"
    // private static final String ADMIN_RECIPENT_NAME = "Admin"
    // private static final String DEFAULT_SENDER_NAME = "Subra Group"

    // @Reference
    // SubraTemplatedEmailService templatedEmailService

    @Reference
    RestClientService restClientService;

    @Activate
    private void init() {
        this.sendElectronicMessage("Alert: Services Activated!",
                "This is to let you know that all the services are activated");
    }

    @Override
    public void sendExceptionMessage(String className, String message) {
        //boolean status = templatedEmailService.email(EmailType.EXCEPTION, "[EXCEPTION] :" + className,
        //        ADMIN_RECIPENT_NAME, DEFAULT_SENDER_NAME, null, Collections.singletonMap("exception", message),
        //      ADMIN_RECIPENT_EMAIL)
        //if : status
        //LOGGER.info("Sent Email with message {}", message)
        //} else
        // LOGGER.error("Error sending Email with message {}", message)
        //}
    }

    @Override
    public void sendElectronicMessage(String subject, String message) {
        //boolean status = templatedEmailService.email(EmailType.GENERIC, subject, ADMIN_RECIPENT_NAME,
        //        DEFAULT_SENDER_NAME, null, Collections.singletonMap("message", message), ADMIN_RECIPENT_EMAIL)
        //if status
        //     LOGGER.info("Sent Email with message {}", message)
        // } else
        //    LOGGER.error("Error sending Email with message {}", message)
        //
    }

    public <T> T callBackendService(final String endpointUrl, final String resource,
                                    final Map<String, String> requestHeaders, final Map<String, String> queryParams, final Object postObject,
                                    final HttpType requestType) {
        final Map<String, String> headers = new HashMap<>();
        if (requestHeaders != null)
            headers.putAll(requestHeaders);

        headers.put(HttpHeaders.ACCEPT, "application/json");
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
        Response response = null;
        try {
            if (requestType.equals(HttpType.GET))
                response = restClientService.getData(endpointUrl, resource, headers, queryParams, Response.class);

            if (requestType.equals(HttpType.POST))
                response = restClientService.postData(endpointUrl, resource, headers, queryParams, postObject,
                        Response.class);

        } catch (RJSApiException e) {
            LOGGER.error("Error calling backend service", e);
        }
        return (T) Optional.ofNullable(response).map(Response::getBody).orElse(null);
    }

}
