/*
 * Copyright 2012-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.amazonaws.http;

import com.amazonaws.SignableRequest;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.Signer;
import com.amazonaws.util.IOUtils;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AWSRequestSigningApacheInterceptorTest {

    private static AWSRequestSigningApacheInterceptor createInterceptor(Signer signer) {
        AWSCredentialsProvider anonymousCredentialsProvider =
                new AWSStaticCredentialsProvider(new AnonymousAWSCredentials());
        return new AWSRequestSigningApacheInterceptor("servicename",
                signer,
                anonymousCredentialsProvider);
    }

    @Test
    public void testSimpleSigner() throws Exception {
        HttpEntityEnclosingRequest request =
                new BasicHttpEntityEnclosingRequest("GET", "/query?a=b");
        request.setEntity(new StringEntity("I'm an entity"));
        request.addHeader("foo", "bar");
        request.addHeader("content-length", "0");

        HttpCoreContext context = new HttpCoreContext();
        context.setTargetHost(HttpHost.create("localhost"));

        createInterceptor(new AddHeaderSigner("Signature", "wuzzle"))
                .process(request, context);

        assertEquals("bar", request.getFirstHeader("foo").getValue());
        assertEquals("wuzzle", request.getFirstHeader("Signature").getValue());
        assertNull(request.getFirstHeader("content-length"));
    }

    @Test(expected = IOException.class)
    public void testBadRequest() throws Exception {
        HttpRequest badRequest = new BasicHttpRequest("GET", "?#!@*%");
        createInterceptor(new AddHeaderSigner("Signature", "wuzzle"))
                .process(badRequest, new BasicHttpContext());
    }

    @Test
    public void testHttpEntityIsCorrect() throws Exception {
        HttpEntityEnclosingRequest request =
                new BasicHttpEntityEnclosingRequest("GET", "/query?a=b");
        BasicHttpEntity httpEntity = new BasicHttpEntity();
        InputStream content = new ByteArrayInputStream(new byte[]{0, 1});
        httpEntity.setContent(content);
        httpEntity.setContentEncoding("gzip");
        request.setEntity(httpEntity);

        HttpCoreContext context = new HttpCoreContext();
        context.setTargetHost(HttpHost.create("localhost"));

        createInterceptor(new AddHeaderSigner("Signature", "wuzzle"))
                .process(request, context);

        assertNotNull(request.getEntity());
        assertNotNull(request.getEntity().getContentEncoding());
        assertEquals("gzip", request.getEntity().getContentEncoding().getValue());
        assertEquals(content, request.getEntity().getContent());
    }

    @Test
    public void testReplaceContentSigner() throws Exception {
        HttpEntityEnclosingRequest request =
                new BasicHttpEntityEnclosingRequest("GET", "/query?a=b");
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity("I'm an entity".getBytes(),
                ContentType.TEXT_PLAIN);
        request.setEntity(byteArrayEntity);
        request.addHeader("foo", "bar");
        request.addHeader("content-length", "0");
        request.addHeader(byteArrayEntity.getContentType());

        HttpCoreContext context = new HttpCoreContext();
        context.setTargetHost(HttpHost.create("localhost"));

        createInterceptor(new ReplaceContentSigner("new content"))
                .process(request, context);
        assertNotNull(request.getEntity().getContentType());
        assertEquals(ContentType.TEXT_PLAIN.toString(),
                request.getEntity().getContentType().getValue());
        assertArrayEquals("new content".getBytes(),
                IOUtils.toByteArray(request.getEntity().getContent()));
    }

    @Test
    public void testEncodedUriSigner() throws Exception {
        HttpEntityEnclosingRequest request =
                new BasicHttpEntityEnclosingRequest("GET",
                        "/foo-2017-02-25%2Cfoo-2017-02-26/_search?a=b");
        request.setEntity(new StringEntity("I'm an entity"));
        request.addHeader("foo", "bar");
        request.addHeader("content-length", "0");

        HttpCoreContext context = new HttpCoreContext();
        context.setTargetHost(HttpHost.create("localhost"));

        createInterceptor(new AddHeaderSigner("Signature", "wuzzle"))
                .process(request, context);

        assertEquals("bar", request.getFirstHeader("foo").getValue());
        assertEquals("wuzzle", request.getFirstHeader("Signature").getValue());
        assertNull(request.getFirstHeader("content-length"));
        assertEquals("/foo-2017-02-25%2Cfoo-2017-02-26/_search",
                request.getFirstHeader("resourcePath").getValue());
    }

    private static class AddHeaderSigner implements Signer {
        private final String name;
        private final String value;

        private AddHeaderSigner(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public void sign(SignableRequest<?> request, AWSCredentials credentials) {
            request.addHeader(name, value);
            request.addHeader("resourcePath", request.getResourcePath());
        }
    }

    private static class ReplaceContentSigner implements Signer {
        private final String content;

        public ReplaceContentSigner(final String content) {
            this.content = content;
        }

        @Override
        public void sign(SignableRequest<?> request, AWSCredentials credentials) {
            request.setContent(new ByteArrayInputStream(content.getBytes()));
        }
    }
}