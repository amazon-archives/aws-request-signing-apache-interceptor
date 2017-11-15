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

import java.io.IOException;

public class APIGatewaySample extends Sample {
    /**
     * The invoke URL for your API which is usually https://api_id.execute-api.api-region.amazonaws.com/stage
     */
    private static final String INVOKE_URL = "https://api_id.execute-api.api-region.amazonaws.com/stage";

    public static void main(String[] args) throws IOException {
        APIGatewaySample apiGatewaySample = new APIGatewaySample();
        apiGatewaySample.makeAPIGGetRequest();
    }

    private void makeAPIGGetRequest() throws IOException {
        HttpGet Http = new HttpGet(INVOKE_URL + "/some/path?and=param");
        logRequest("execute-api", Http);
    }
}