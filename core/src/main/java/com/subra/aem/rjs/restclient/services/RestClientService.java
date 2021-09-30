package com.subra.aem.rjs.restclient.services;

import com.subra.aem.rjs.restclient.RestClientResponseDto;
import org.apache.http.Header;

import java.io.OutputStream;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;


public interface RestClientService {

    <T> T getDataFromResource(final String resourceName, final Class<T> clazz);

    <T> T getData(final String endpointUrl, final String resource, final Map<String, String> requestHeaders, final Map<String, String> queryParams, final Class<T> clazz);

    <T> RestClientResponseDto<T> getDataAndGetResponse(String endpointUrl, String resource, Map<String, String> requestHeaders, Map<String, String> queryParams, Class<T> clazz);

    <T> T getData(final String endpointUrl, final String resource, final Map<String, String> requestHeaders, final Map<String, String> queryParams, final Class<T> clazz, int retries);

    <T> RestClientResponseDto<T> getDataAndGetResponse(String endpointUrl, String resource, Map<String, String> requestHeaders, Map<String, String> queryParams, Class<T> clazz, int retries);

    void postData(String endpointUrl, String resource, Map<String, String> requestHeaders, Map<String, String> queryParams, Object postObject);

    <T> T postData(String endpointUrl, String resource, Map<String, String> requestHeaders, Map<String, String> queryParams, Object postObject, Class<T> clazz);

    <T> RestClientResponseDto<T> postDataAndGetResponse(String endpointUrl, String resource, Map<String, String> requestHeaders, Map<String, String> queryParams, Object postObject, Class<T> clazz);

    void readBinaryData(String endpointUrl, String resource, Map<String, String> requestHeaders, Map<String, String> queryParams, Consumer<Header> contentTypeConsumer, Supplier<OutputStream> target);

}
