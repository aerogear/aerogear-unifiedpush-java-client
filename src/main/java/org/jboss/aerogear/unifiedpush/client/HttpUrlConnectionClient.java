/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.unifiedpush.client;


import net.iharder.Base64;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class HttpUrlConnectionClient implements Client {

    private static final Logger logger = Logger.getLogger(HttpUrlConnectionClient.class.getName());

    @Override
    public void post(Map<String, ? extends Object> json, String url, String pushApplicationId, String masterSecret) {
        // transform JSON:
        String payload = transformJSON(json);
        // fire!
        submitPayload(url, payload, pushApplicationId, masterSecret);
    }

    @Override
    public void post(Map<String, ? extends Object> json, List<String> clientIdentifiers, String url, String pushApplicationId, String masterSecret) {
        // build the URL:
        final Map<String, Object> selectedPayloadObject =
                new LinkedHashMap<String, Object>();
        // add the "clientIdentifiers" to the "alias" fie;d
        selectedPayloadObject.put("alias", clientIdentifiers);
        selectedPayloadObject.put("message", json);
        // transform to JSONString:
        String payload = transformJSON(selectedPayloadObject);
        // fire!
        submitPayload(url, payload, pushApplicationId, masterSecret);
    }

    private void submitPayload(String url, String jsonPayloadObject, String pushApplicationId, String masterSecret) {
        try {
            URL pushUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) pushUrl.openConnection();
            httpURLConnection.setRequestMethod("POST");
            String credentials = pushApplicationId + ":" + masterSecret;
            String encoded = Base64.encodeBytes(credentials.getBytes("UTF-8"));
            httpURLConnection.setRequestProperty("Authorization", "Basic " + encoded);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();
            byte[] outputBytes = jsonPayloadObject.getBytes("UTF-8");
            OutputStream os = httpURLConnection.getOutputStream();
            os.write(outputBytes);
            os.close();
            int status = httpURLConnection.getResponseCode();

        } catch (MalformedURLException e) {
            logger.severe("Invalid Server URL");
        } catch (IOException e) {
            logger.severe("IO Exception");
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
