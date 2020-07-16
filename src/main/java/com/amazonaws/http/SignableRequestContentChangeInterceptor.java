package com.amazonaws.http;

import com.amazonaws.ReadLimitInfo;
import com.amazonaws.SignableRequest;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Wrapper for SignableRequest which intercepts if the request content is changed.
 *
 * @param <T>
 */
public class SignableRequestContentChangeInterceptor<T> implements SignableRequest<T> {
    private final SignableRequest<T> original;
    private boolean contentChanged = false;

    public SignableRequestContentChangeInterceptor(final SignableRequest<T> original) {
        this.original = original;
    }

    @Override
    public void addHeader(final String name, final String value) {
        original.addHeader(name, value);
    }

    @Override
    public void addParameter(final String name, final String value) {
        original.addParameter(name, value);
    }

    @Override
    public void setContent(final InputStream inputStream) {
        original.setContent(inputStream);
        contentChanged = true;
    }

    @Override
    public Map<String, String> getHeaders() {
        return original.getHeaders();
    }

    @Override
    public String getResourcePath() {
        return original.getResourcePath();
    }

    @Override
    public Map<String, List<String>> getParameters() {
        return original.getParameters();
    }

    @Override
    public URI getEndpoint() {
        return original.getEndpoint();
    }

    @Override
    public HttpMethodName getHttpMethod() {
        return original.getHttpMethod();
    }

    @Override
    public int getTimeOffset() {
        return original.getTimeOffset();
    }

    @Override
    public InputStream getContent() {
        return original.getContent();
    }

    @Override
    public InputStream getContentUnwrapped() {
        return original.getContentUnwrapped();
    }

    @Override
    public ReadLimitInfo getReadLimitInfo() {
        return original.getReadLimitInfo();
    }

    @Override
    public Object getOriginalRequestObject() {
        return original.getOriginalRequestObject();
    }

    public boolean isContentChanged() {
        return contentChanged;
    }
}
