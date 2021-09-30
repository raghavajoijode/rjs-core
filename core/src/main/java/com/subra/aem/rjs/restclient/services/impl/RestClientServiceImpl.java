package com.subra.aem.rjs.restclient.services.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subra.aem.rjs.restclient.RestClientResponseDto;
import com.subra.aem.rjs.restclient.helpers.RJSHttpClient;
import com.subra.aem.rjs.restclient.services.RestClientService;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.constants.HttpType;
import org.subra.commons.exceptions.RJSApiError;
import org.subra.commons.exceptions.RJSApiException;
import org.subra.commons.helpers.CommonHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component(service = RestClientService.class, immediate = true)
@ServiceDescription("RJS - Rest Client")
public class RestClientServiceImpl implements RestClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientServiceImpl.class);

    private static final ObjectMapper OBJECT_MAPPER = CommonHelper.getObjectMapper();
    private static final int ERROR_RESP_NUMBER_CHAR_LIMIT = 8000;
    private static final String AEM_SUBRA_IDENTIFIER = "AEM|SUBRA|";

    @Reference
    private RJSHttpClient rjsHttpClient;

    @Override
    public <T> T getData(final String endpointUrl, final String resource, final Map<String, String> requestHeaders,
                         final Map<String, String> queryParams, final Class<T> clazz) {
        return getData(endpointUrl, resource, requestHeaders, queryParams, clazz, 0);
    }

    @Override
    public <T> RestClientResponseDto<T> getDataAndGetResponse(final String endpointUrl, final String resource,
                                                              final Map<String, String> requestHeaders, final Map<String, String> queryParams, final Class<T> clazz) {
        return getDataAndGetResponse(endpointUrl, resource, requestHeaders, queryParams, clazz, 0);
    }

    @Override
    public <T> T getData(final String endpointUrl, final String resource, final Map<String, String> requestHeaders,
                         final Map<String, String> queryParams, final Class<T> clazz, final int retries) {
        final RestClientResponseDto<T> responseDto = getDataAndGetResponse(endpointUrl, resource, requestHeaders,
                queryParams, clazz, retries);
        return responseDto.getObject();
    }

    @Override
    public <T> RestClientResponseDto<T> getDataAndGetResponse(final String endpointUrl, final String resource,
                                                              final Map<String, String> requestHeaders, final Map<String, String> queryParams, final Class<T> clazz,
                                                              final int retries) {
        StatusLine httpStatus;
        int statusCode;
        String responseJson = StringUtils.EMPTY;
        URI uri = null;
        Header[] headers = null;

        try {
            uri = createUri(endpointUrl, resource, queryParams);
            final HttpClient httpClient = getHttpClient();
            final HttpGet request = new HttpGet(uri);
            addRequestHeaders(request, requestHeaders);

            LOGGER.debug("request at getDataAndGetResponse(...) ==> {}", uri);
            LOGGER.debug("headers  ==>{}", request);

            try (CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(request)) {
                httpStatus = response.getStatusLine();
                statusCode = httpStatus.getStatusCode();
                LOGGER.debug("Http response statusCode: [{}]", statusCode);

                headers = response.getAllHeaders();

                responseJson = getResponse(response);
                LOGGER.debug("ResponseJson : [{}]", responseJson);
            }
        } catch (final Exception e) {
            if (retries > 0) {
                LOGGER.warn("Retrying request after exception", e);
                return getDataAndGetResponse(endpointUrl, resource, requestHeaders, queryParams, clazz, retries - 1);
            }
            if (!StringUtils.isBlank(e.getMessage()) && e.getMessage().length() > ERROR_RESP_NUMBER_CHAR_LIMIT) {
                throw new RJSApiException(endpointUrl, e.getMessage().substring(0, ERROR_RESP_NUMBER_CHAR_LIMIT));
            } else {
                throw new RJSApiException(endpointUrl, e.getMessage());
            }
        }

        if (HttpStatus.SC_OK != statusCode) {
            if (retries > 0 && HttpStatus.SC_INTERNAL_SERVER_ERROR == statusCode) {
                LOGGER.warn("Retrying request to {} after HTTP 500", uri);
                return getDataAndGetResponse(endpointUrl, resource, requestHeaders, queryParams, clazz, retries - 1);
            }
            handleError(uri, httpStatus, responseJson);
        }

        final T object = getResponseObject(responseJson, clazz, uri);// wrap the object and the response into the dto
        return new RestClientResponseDto<>(object, headers);
    }

    /**
     * Post Data to a Rest Service. No response is returned.
     *
     * @param endpointUrl
     * @param resource
     * @param requestHeaders
     * @param queryParams
     * @param postObject
     * @return
     * @throws RJSApiException
     */
    @Override
    public void postData(final String endpointUrl, final String resource, final Map<String, String> requestHeaders,
                         final Map<String, String> queryParams, final Object postObject) {

        CloseableHttpResponse response = null;
        StatusLine httpStatus;
        String responseJson = StringUtils.EMPTY;
        URI uri = null;

        try {

            uri = createUri(endpointUrl, resource, queryParams);

            final HttpClient httpClient = getHttpClient();
            final HttpPost request = new HttpPost(uri);

            final String requestMsg = createObjectMapperWithUnknownENUMAsNull().writeValueAsString(postObject);

            request.setEntity(new StringEntity(requestMsg));

            addRequestHeaders(request, requestHeaders);

            LOGGER.debug("request at postData==> {}", uri.getQuery());

            response = (CloseableHttpResponse) httpClient.execute(request);

            httpStatus = response.getStatusLine();
            LOGGER.debug("Http response : [{}]", response.getStatusLine().getStatusCode());

            responseJson = getResponse(response);
            LOGGER.debug("Response : [{}]", responseJson);

            response.close();

        } catch (final Exception e) {
            if (response != null) {
                try {
                    response.close();
                } catch (final IOException ioe) {
                    LOGGER.error("Exception caught in rest client and response is not null");
                }
            }
            if (!StringUtils.isBlank(e.getMessage()) && e.getMessage().length() > ERROR_RESP_NUMBER_CHAR_LIMIT) {
                throw new RJSApiException(endpointUrl, e.getMessage().substring(0, ERROR_RESP_NUMBER_CHAR_LIMIT));
            } else {
                throw new RJSApiException(endpointUrl, e.getMessage());
            }
        }

        if (HttpStatus.SC_OK != httpStatus.getStatusCode() && HttpStatus.SC_NO_CONTENT != httpStatus.getStatusCode()
                && HttpStatus.SC_ACCEPTED != httpStatus.getStatusCode()) {
            handleError(uri, httpStatus, responseJson);
        }
    }

    /**
     * Post Data to a Rest Service. No response is returned.
     *
     * @param endpointUrl
     * @param resource
     * @param requestHeaders
     * @param queryParams
     * @param postObject
     * @return
     * @throws RJSApiException
     */
    @Override
    public <T> T postData(final String endpointUrl, final String resource, final Map<String, String> requestHeaders,
                          final Map<String, String> queryParams, final Object postObject, final Class<T> clazz) {

        final RestClientResponseDto<T> responseDto = postDataAndGetResponse(endpointUrl, resource, requestHeaders,
                queryParams, postObject, clazz);
        return responseDto.getObject();
    }

    /**
     * Post Data to a Rest Service and get the response object wrapped in a
     * <code>SubraRestClientResopnseDto</code>
     *
     * @param endpointUrl
     * @param resource
     * @param requestHeaders
     * @param queryParams
     * @param postObject
     * @param clazz
     * @return
     * @throws RJSApiException
     */
    @Override
    public <T> RestClientResponseDto<T> postDataAndGetResponse(final String endpointUrl, final String resource,
                                                               final Map<String, String> requestHeaders, final Map<String, String> queryParams, final Object postObject,
                                                               final Class<T> clazz) {

        CloseableHttpResponse response = null;
        StatusLine httpStatus;
        String responseJson = "";
        URI uri = null;
        Header[] headers = null;

        try {
            uri = createUri(endpointUrl, resource, queryParams);

            final HttpClient httpClient = getHttpClient();
            final HttpPost request = new HttpPost(uri);

            final String requestMsg = createObjectMapperWithUnknownENUMAsNull().writeValueAsString(postObject);

            request.setEntity(new StringEntity(requestMsg));

            addRequestHeaders(request, requestHeaders);

            LOGGER.debug("request at postDataAndGetResponse(...)==> {}", uri);

            response = (CloseableHttpResponse) httpClient.execute(request);
            headers = response.getAllHeaders();

            httpStatus = response.getStatusLine();
            LOGGER.debug("Http response : [{}]", response.getStatusLine().getStatusCode());

            responseJson = getResponse(response);
            LOGGER.debug("Response at postDataAndGetResponse(...): [{}]", responseJson);
            response.close();

        } catch (final Exception e) {
            if (response != null) {
                try {
                    response.close();
                } catch (final IOException ioe) {
                    LOGGER.error("Exception caught in rest client and response is not null");
                }
            }
            if (e.getMessage() != null && e.getMessage().length() > ERROR_RESP_NUMBER_CHAR_LIMIT) {
                throw new RJSApiException(endpointUrl, e.getMessage().substring(0, ERROR_RESP_NUMBER_CHAR_LIMIT));
            } else {
                throw new RJSApiException(endpointUrl, e.getMessage());
            }
        }

        if (HttpStatus.SC_OK != httpStatus.getStatusCode()) {
            handleError(uri, httpStatus, responseJson);
        }

        final T object = getResponseObject(responseJson, clazz, uri);

        // wrap the object and the response into the dto
        return new RestClientResponseDto<>(object, headers);
    }

    @Override
    public void readBinaryData(final String endpointUrl, final String resource,
                               final Map<String, String> requestHeaders, final Map<String, String> queryParams,
                               final Consumer<Header> contentTypeConsumer, final Supplier<OutputStream> target) {
        final StatusLine httpStatus;
        int statusCode;
        URI uri = null;
        try {
            uri = createUri(endpointUrl, resource, queryParams);
            final HttpClient httpClient = getHttpClient();
            final HttpGet request = new HttpGet(uri);
            addRequestHeaders(request, requestHeaders);

            LOGGER.debug("request ==> {}", uri);
            LOGGER.debug("headers  ==>{}", request);

            try (CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(request)) {
                httpStatus = response.getStatusLine();
                statusCode = httpStatus.getStatusCode();
                if (HttpStatus.SC_OK == statusCode) {
                    LOGGER.debug("Http response: [{}]", statusCode);
                    final HttpEntity e = response.getEntity();
                    contentTypeConsumer.accept(e.getContentType());
                    e.writeTo(target.get());
                } else {
                    LOGGER.error("Http response: [{}]", statusCode);
                    throw new RJSApiException(endpointUrl,
                            "Server returned error for URL  " + uri + ": " + httpStatus);
                }
            }
        } catch (final Exception e) {
            throw new RJSApiException(endpointUrl, e.getMessage());
        }
    }

    protected void addRequestHeaders(final HttpRequestBase request, final Map<String, String> headersMap) {

        final boolean isHeadersNotEmpty = !MapUtils.isEmpty(headersMap);
        if (isHeadersNotEmpty) {
            headersMap.keySet().forEach(key -> request.addHeader(key, headersMap.get(key)));
        }

        if (isHeadersNotEmpty && !headersMap.containsKey(HttpHeaders.ACCEPT)) {
            request.addHeader(HttpHeaders.ACCEPT, String.format("%s", HttpType.MEDIA_TYPE_JSON.value()));
        }
        if (isHeadersNotEmpty && !headersMap.containsKey(HttpHeaders.CONTENT_TYPE)) {
            request.addHeader(HttpHeaders.CONTENT_TYPE, HttpType.MEDIA_TYPE_FORM_URLENCODED.value());
        }
        if (isHeadersNotEmpty && !headersMap.containsKey(HttpHeaders.ACCEPT_CHARSET)) {
            request.addHeader(HttpHeaders.ACCEPT_CHARSET, HttpType.CHARSET_UTF_8.value());
        }
        if (isHeadersNotEmpty && !headersMap.containsKey(HttpHeaders.USER_AGENT)) {
            StringBuilder userAgentHeader = new StringBuilder();
            userAgentHeader.append(AEM_SUBRA_IDENTIFIER).append(headersMap.get(HttpType.X_USER_AGENT_TEXT.value()))
                    .append("|").append(headersMap.get(HttpType.X_IP_ADDRESS.value()));
            request.addHeader(HttpHeaders.USER_AGENT, userAgentHeader.toString());
        }

    }

    protected URI createUri(final String endpointUrl, final String resource, final Map<String, String> queryParams)
            throws URISyntaxException, MalformedURLException {

        if (StringUtils.isBlank(endpointUrl)) {
            LOGGER.error("createUri: missing enpoint url [{}]", endpointUrl);
            throw new IllegalArgumentException("URI is null");
        }

        final URL url = new URL(endpointUrl);

        final URIBuilder uriBuilder = new URIBuilder().setScheme(url.getProtocol()).setHost(url.getHost());
        if (url.getPort() != 0) {
            uriBuilder.setPort(url.getPort());
        }

        if (!StringUtils.isEmpty(resource)) {
            final String pathAndResourceFormat = url.getPath().endsWith("/") ? "%s%s" : "%s/%s";
            uriBuilder.setPath(String.format(pathAndResourceFormat, url.getPath(), resource));
        } else {
            uriBuilder.setPath(url.getPath());
        }

        if (!MapUtils.isEmpty(queryParams)) {
            queryParams.keySet().forEach(key -> uriBuilder.setParameter(key, queryParams.get(key)));
        }

        return uriBuilder.build();

    }

    protected String getResponse(final CloseableHttpResponse response) throws IOException {
        if (response.getEntity() != null) {
            return IOUtils.toString(response.getEntity().getContent(),
                    Charset.forName(HttpType.CHARSET_UTF_8.value()));
        }
        return StringUtils.EMPTY;
    }

    protected <T> T getResponseObject(final String responseAsJson, final Class<T> clazz, final URI uri) {
        if (clazz != null && String.class.equals(clazz)) {
            return clazz.cast(responseAsJson);
        }

        try {
            return OBJECT_MAPPER.readValue(responseAsJson, clazz);
        } catch (final Exception e) {
            throw new RJSApiException(
                    String.format("Failed to deserialize http response [%s] from [%s]", responseAsJson, uri), e);
        }
    }

    protected <T> T getResponseObjectFromResource(final String resourceFileName, final Class<T> clazz) {
        try {
            return OBJECT_MAPPER
                    .readValue(getClass().getResourceAsStream(resourceFileName.startsWith("/") ? resourceFileName
                            : String.format("/%s", resourceFileName.trim())), clazz);
        } catch (final Exception e) {
            throw new RJSApiException(String.format("Failed to deserialize json at [%s] into [%s]", resourceFileName,
                    clazz.getSimpleName()), e);
        }
    }

    protected void handleError(final URI uri, final StatusLine httpStatus, final String responseJson) {
        RJSApiError rjsApiError = null;
        if (!StringUtils.isBlank(responseJson)) {

            try {
                rjsApiError = getResponseObject(responseJson, RJSApiError.class, uri);
            } catch (final RJSApiException e) {
                LOGGER.error("Error deserializing SUBRA error message from {}: [{}]", uri, responseJson);
            }
        }
        if (rjsApiError == null) {
            rjsApiError = new RJSApiError();
        }
        rjsApiError.setHttpStatusCode(httpStatus.getStatusCode());
        rjsApiError.setHttpReason(httpStatus.getReasonPhrase());

        throw new RJSApiException(rjsApiError);
    }

    protected HttpClient getHttpClient() {
        return rjsHttpClient.getHttpClient();
    }

    protected void setHttpClient(final RJSHttpClient httpClient) {
        this.rjsHttpClient = httpClient;
    }

    protected ObjectMapper createObjectMapperWithUnknownENUMAsNull() {
        final ObjectMapper om = OBJECT_MAPPER;
        om.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        return om;
    }

    @Override
    public <T> T getDataFromResource(String resourceName, Class<T> clazz) {
        return getResponseObjectFromResource(resourceName, clazz);
    }

}
