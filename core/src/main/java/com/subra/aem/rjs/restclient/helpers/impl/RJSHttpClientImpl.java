package com.subra.aem.rjs.restclient.helpers.impl;

import com.subra.aem.rjs.restclient.helpers.RJSHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component(service = RJSHttpClient.class, immediate = true)
@ServiceDescription("RJS - Http Client")
@Designate(ocd = RJSHttpClient.Config.class)
public class RJSHttpClientImpl implements RJSHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RJSHttpClientImpl.class);

    private static String subServiceName;
    private static int maxHttpConnections;
    private static int maxHttpConnectionsPerRoute;
    private static int httpConnectRequestTimeout;
    private static int httpSocketTimeout;
    private static int httpConnectTimeout;
    private CloseableHttpClient httpClient;

    public static String getSubServiceName() {
        return subServiceName;
    }

    public static void setSubServiceName(final String subServiceName) {
        RJSHttpClientImpl.subServiceName = subServiceName;
    }

    public static int getMaxHttpConnections() {
        return maxHttpConnections;
    }

    public static void setMaxHttpConnections(final int maxHttpConnections) {
        RJSHttpClientImpl.maxHttpConnections = maxHttpConnections;
    }

    public static int getMaxHttpConnectionsPerRoute() {
        return maxHttpConnectionsPerRoute;
    }

    public static void setMaxHttpConnectionsPerRoute(final int maxHttpConnectionsPerRoute) {
        RJSHttpClientImpl.maxHttpConnectionsPerRoute = maxHttpConnectionsPerRoute;
    }

    public static int getHttpConnectRequestTimeout() {
        return httpConnectRequestTimeout;
    }

    public static void setHttpConnectRequestTimeout(final int httpConnectRequestTimeout) {
        RJSHttpClientImpl.httpConnectRequestTimeout = httpConnectRequestTimeout;
    }

    public static int getHttpSocketTimeout() {
        return httpSocketTimeout;
    }

    public static void setHttpSocketTimeout(final int httpSocketTimeout) {
        RJSHttpClientImpl.httpSocketTimeout = httpSocketTimeout;
    }

    public static int getHttpConnectTimeout() {
        return httpConnectTimeout;
    }

    public static void setHttpConnectTimeout(final int httpConnectTimeout) {
        RJSHttpClientImpl.httpConnectTimeout = httpConnectTimeout;
    }

    @Activate
    @Modified
    public void activate(final Config config) {
        setMaxHttpConnections(config.max_http_connections());
        setMaxHttpConnectionsPerRoute(config.max_http_connections_per_route());
        setHttpConnectRequestTimeout(config.http_connect_request_timeout());
        setHttpSocketTimeout(config.http_socket_timeout());
        setHttpConnectTimeout(config.http_connect_timeout());
        setSubServiceName(config.subservice_name());
        LOGGER.info("Http configuration: MaxConn [{}] :: MaxConnPerRoute: [{}] :: ReqTO: [{}] :: SocketTO: [{}] :: ConnectTO: [{}]",
                getMaxHttpConnections(), getMaxHttpConnectionsPerRoute(), getHttpConnectRequestTimeout(), getHttpSocketTimeout(), getHttpConnectTimeout());
        initHttpClient();
    }

    @Override
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Initialize the http client. If already exists, deallocate it first.
     *
     * @throws Exception
     */
    protected synchronized void initHttpClient() {

        if (httpClient != null) {
            closeHttpClient();
        }

        HttpClientConnectionManager connectionManager = getConnectionManager();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(RJSHttpClientImpl.httpSocketTimeout)
                .setConnectionRequestTimeout(RJSHttpClientImpl.httpConnectRequestTimeout)
                .setConnectTimeout(RJSHttpClientImpl.httpConnectTimeout).build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager).build();

    }

    /**
     * Close the http client
     */
    public final synchronized void closeHttpClient() {

        LOGGER.info("Closing HttpClient");

        // method call added for junit testing
        if (getHttpClient() == null) {
            LOGGER.info("HttpClient is null");
            return;
        }
        try {
            ((CloseableHttpClient) getHttpClient()).close();
        } catch (IOException ioe) {
            LOGGER.error("Error closing http client", ioe);
        } finally {
            httpClient = null;
        }
    }

    /**
     * Get/create the connection manager for this http client.
     *
     * @return
     * @throws Exception
     */
    protected HttpClientConnectionManager getConnectionManager() {
        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();
        httpClientConnectionManager.setMaxTotal(RJSHttpClientImpl.maxHttpConnections);
        httpClientConnectionManager.setDefaultMaxPerRoute(RJSHttpClientImpl.maxHttpConnectionsPerRoute);

        return httpClientConnectionManager;
    }

    @Deactivate
    public void deactivate() {
        LOGGER.info("Deactivating the HttpClient");
        closeHttpClient();
    }

}
