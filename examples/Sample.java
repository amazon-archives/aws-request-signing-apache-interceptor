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
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.http.AWSRequestSigningApacheInterceptor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class Sample {
    static final String AWS_REGION = "us-east-1";
    static final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

    public static void main(String[] args) throws IOException {
        Sample sampleClass = new Sample();
        sampleClass.makeGetRequest();
        sampleClass.makePostRequest();
    }

    private void makeGetRequest() throws IOException {
        HttpGet httpGet = new HttpGet("http://targethost/homepage");
        logRequest("", httpGet);
    }

    private void makePostRequest() throws IOException {
        HttpPost httpPost = new HttpPost("http://targethost/login");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("username", "vip"));
        nvps.add(new BasicNameValuePair("password", "secret"));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        logRequest("", httpPost);
    }

    void logRequest(String serviceName, HttpUriRequest request) throws IOException {
        System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
        CloseableHttpClient httpClient = signingClientForServiceName(serviceName);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            System.out.println(response.getStatusLine());
            String inputLine ;
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            try {
                while ((inputLine = br.readLine()) != null) {
                    System.out.println(inputLine);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    CloseableHttpClient signingClientForServiceName(String serviceName) {
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(serviceName);
        signer.setRegionName(AWS_REGION);

        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider);
        return HttpClients.custom()
                .addInterceptorLast(interceptor)
                .build();
    }

    HttpEntity stringEntity(final String body) throws UnsupportedEncodingException {
        BasicHttpEntity httpEntity = new BasicHttpEntity();
        httpEntity.setContent(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8.name())));
        return httpEntity;
    }
}
