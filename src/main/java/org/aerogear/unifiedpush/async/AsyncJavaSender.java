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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.aerogear.unifiedpush.JavaSender;
import org.codehaus.jackson.map.ObjectMapper;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public class AsyncJavaSender implements JavaSender{
    
    private static final Logger logger = Logger.getLogger(AsyncJavaSender.class.getName());
    
    // final?
    private String serverURL;
    
    public AsyncJavaSender(String rootServerURL) {
        if (rootServerURL == null) {
            throw new IllegalStateException("server can not be null");
        }
        
        if (! rootServerURL.endsWith("/") ) {
            rootServerURL = rootServerURL.concat("/"); 
        }
        this.serverURL = rootServerURL;
    }


    @Override
    public void broadcast(Map<String, ? extends Object> json,
            String pushApplicationID) {
        // build the URL:
        StringBuilder sb = new StringBuilder();
        sb.append(serverURL)
          .append("rest/sender/broadcast/")
          .append(pushApplicationID);
        
        // transform JSON:
        String payload = transformJSON(json);
        

        // fire!
        submitPayload(sb.toString(), payload);
        
    }

    @Override
    public void sendTo(List<String> clientIdentifiers,
            Map<String, ? extends Object> json, String pushApplicationID) {
        // build the URL:
        StringBuilder sb = new StringBuilder();
        sb.append(serverURL)
          .append("rest/sender/selected/")
          .append(pushApplicationID);
        
        // build Map/JSON:
        final Map<String, Object> selectedPayloadObject = 
                new LinkedHashMap<String, Object>();
        
        // add the "clientIdentifiers" to the "alias" fie;d
        selectedPayloadObject.put("alias", clientIdentifiers);
        selectedPayloadObject.put("message", json);

        
        // transform to JSONString:
        String payload = transformJSON(selectedPayloadObject);
        

        // fire!
        submitPayload(sb.toString(), payload);
    }


    private void submitPayload(String url, String jsonPayloadObject) {
        // setup:
        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        

        try {
            // currently, not really async...
            Response response = 
                    asyncHttpClient.preparePost(url)
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-type", "application/json")
                        .setBody(jsonPayloadObject)
                        .execute().get();

            int statusCode = response.getStatusCode();
            if (statusCode != 200) {
                logger.severe("Receiving status code: " + statusCode);
            }
        } catch (Exception e) { 
            e.printStackTrace();
        } finally {
            asyncHttpClient.closeAsynchronously();
        }
    }
    
    private String transformJSON(Object value) {
        ObjectMapper om = new ObjectMapper();
        String stringPayload = null;
        try {
            stringPayload = om.writeValueAsString(value);
        } catch (Exception e) {
            new IllegalStateException("Failed to encode JSON payload");
        }
        return stringPayload;
    }
    
}
