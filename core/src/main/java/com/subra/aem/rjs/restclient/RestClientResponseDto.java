package com.subra.aem.rjs.restclient;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.subra.commons.constants.HttpType;

import java.util.Arrays;

public class RestClientResponseDto<T> {

    private T object;
    private Header[] headers;

    public RestClientResponseDto(final T object, final Header[] headers) {
        this.object = object;
        this.headers = headers;
    }

    public T getObject() {
        return object;
    }

    public String getHeaderValue(final HttpType headerName) {
        if (headerName != null && headers != null)
            return Arrays.stream(headers).filter(h -> StringUtils.equals(h.getName(), headerName.value())).findFirst().map(Header::getValue).orElse(null);

        return StringUtils.EMPTY;
    }
}
