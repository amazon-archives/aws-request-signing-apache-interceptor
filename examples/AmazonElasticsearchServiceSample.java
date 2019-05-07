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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.io.IOException;

/**
 * <p>An AWS Request Signing Interceptor sample for arbitrary HTTP requests to an Amazon Elasticsearch Service domain.</p>
 * <p>The interceptor can also be used with the Elasticsearch REST clients for additional convenience and serialization.</p>
 * <p>Example usage with the Elasticsearch low-level REST client:</p>
 * <pre>
 * String serviceName = "es";
 * AWS4Signer signer = new AWS4Signer();
 * signer.setServiceName(serviceName);
 * signer.setRegionName("us-east-1");
 *
 * HttpRequestInterceptor interceptor =
 *     new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider);
 *
 * return RestClient
 *     .builder(HttpHost.create("https://search-my-es-endpoint-gjhfgfhgfhg.us-east-1.amazonaws.com"))
 *     .setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor))
 *     .build();
 * </pre>
 * <p>Example usage with the Elasticsearch high-level REST client:</p>
 * <pre>
 * String serviceName = "es";
 * AWS4Signer signer = new AWS4Signer();
 * signer.setServiceName(serviceName);
 * signer.setRegionName("us-east-1");
 *
 * HttpRequestInterceptor interceptor =
 *     new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider);
 * 
 * return new RestHighLevelClient(RestClient
 *     .builder(HttpHost.create("https://search-my-es-endpoint-gjhfgfhgfhg.us-east-1.amazonaws.com"))
 *     .setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));
 * </pre>
 */
public class AmazonElasticsearchServiceSample extends Sample {
    private static final String AES_ENDPOINT = "https://search-my-es-endpoint-gjhfgfhgfhg.us-east-1.amazonaws.com";
    public static void main(String[] args) throws IOException {
        AmazonElasticsearchServiceSample aesSample = new AmazonElasticsearchServiceSample();
        aesSample.makeAESRequest();
        aesSample.indexDocument();
    }

    private void makeAESRequest() throws IOException {
        HttpGet httpGet = new HttpGet(AES_ENDPOINT);
        logRequest("es", httpGet);
    }

    private void indexDocument() throws IOException {
        String payload = "{\"test\": \"val\"}";
        HttpPost httpPost = new HttpPost(AES_ENDPOINT + "/index_name/type_name/document_id");
        httpPost.setEntity(stringEntity(payload));
        httpPost.addHeader("Content-Type", "application/json");
        logRequest("es", httpPost);
    }
}
