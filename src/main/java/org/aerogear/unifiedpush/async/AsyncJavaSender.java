/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aerogear.unifiedpush.async;

import java.util.Map;

import org.aerogear.unifiedpush.JavaSender;
import org.codehaus.jackson.map.ObjectMapper;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public class AsyncJavaSender implements JavaSender{
    // final?
    private String serverURL;
    
    public AsyncJavaSender(String serverURL) {
        this.serverURL = serverURL;
    }


    @Override
    public void broadcast(Map<String, ? extends Object> json,
            String pushApplicationID) {
        // setup:
        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        StringBuilder sb = new StringBuilder();
        sb.append(serverURL)
          .append("/")
          .append(pushApplicationID);
        
        // transform JSON:
        ObjectMapper om = new ObjectMapper();
        String stringPayload = null;
        try {
            stringPayload = om.writeValueAsString(json);
        } catch (Exception e) {
            new IllegalStateException("Failed to encode JSON payload");
        }
        

        try {
            // currently, not really async...
            Response response = 
                    asyncHttpClient.preparePost(sb.toString())
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-type", "application/json")
                        .setBody(stringPayload)
                        .execute().get();

            if (response.getStatusCode() != 200) {
                // LOG warning
            }
        } catch (Exception e) { 
            e.printStackTrace();
        } finally {
            asyncHttpClient.closeAsynchronously();
        }
    }

}
