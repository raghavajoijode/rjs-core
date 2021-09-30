package com.subra.aem.rjs.restclient.helpers;

import org.apache.http.client.HttpClient;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

public interface RJSHttpClient {

    HttpClient getHttpClient();

    @ObjectClassDefinition(name = "RJS - Http Client", description = "Service for http configuration")
    @interface Config {

        @AttributeDefinition(name = "subservice.name", description = "Sub Service name for query service")
        String subservice_name() default "httpClient";

        @AttributeDefinition(name = "http.max.connections", description = "Maximum http connections for the client")
        int max_http_connections() default 1000;

        @AttributeDefinition(name = "http.max.connections.per.route", description = "Maximum http connections per route for the client")
        int max_http_connections_per_route() default 500;

        @AttributeDefinition(name = "http.timeout.request", description = "Request connect timeout (in milliseconds) for requesting connection from connection manager for the client")
        int http_connect_request_timeout() default 1000;

        @AttributeDefinition(name = "http.timeout.socket", description = "Socket (receive) timeout (in milliseconds) for the client")
        int http_socket_timeout() default 10000;

        @AttributeDefinition(name = "http.timeout.connect", description = "Connect  timeout (in milliseconds) for estabishing a connection for the client")
        int http_connect_timeout() default 1000;
    }

}
