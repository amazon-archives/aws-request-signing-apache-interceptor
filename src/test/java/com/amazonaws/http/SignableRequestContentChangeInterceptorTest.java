package com.amazonaws.http;

import com.amazonaws.DefaultRequest;
import com.amazonaws.SignableRequest;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;

public class SignableRequestContentChangeInterceptorTest {
    @Test
    public void testContentChangeDetected() {
        SignableRequest<?> signableRequest = new DefaultRequest<>("test-service");
        signableRequest.setContent(new ByteArrayInputStream(new byte[]{0, 1}));
        SignableRequestContentChangeInterceptor<?> wrapped =
                new SignableRequestContentChangeInterceptor<>(signableRequest);
        Assert.assertFalse(wrapped.isContentChanged());
        wrapped.setContent(new ByteArrayInputStream(new byte[]{1, 2}));
        Assert.assertTrue(wrapped.isContentChanged());
    }
}