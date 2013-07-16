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
package org.jboss.aerogear.unifiedpush;

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

public class SenderClient implements JavaSender {

    private static final Logger logger = Logger.getLogger(SenderClient.class.getName());

    // final?
    private String serverURL;

    public SenderClient(String rootServerURL) {
        if (rootServerURL == null) {
            throw new IllegalStateException("server can not be null");
        }

        if (!rootServerURL.endsWith("/")) {
            rootServerURL = rootServerURL.concat("/");
        }
        this.serverURL = rootServerURL;
    }

    protected StringBuilder buildUrl(String type, String pushApplicationID) {
        //  build the broadcast URL:
        StringBuilder sb = new StringBuilder();
        sb.append(serverURL)
                .append("rest/sender/")
                .append(type);
        return sb;
    }

    @Override
    public void broadcast(Map<String, ? extends Object> json, String pushApplicationID, String masterSecret) {
        StringBuilder sb = buildUrl("broadcast", pushApplicationID);
        // transform JSON:
        String payload = transformJSON(json);
        // fire!
        submitPayload(sb.toString(), payload, pushApplicationID, masterSecret);
    }

    @Override
    public void sendTo(List<String> clientIdentifiers, Map<String, ? extends Object> json, String pushApplicationID, String masterSecret) {
        StringBuilder sb = buildUrl("selected", pushApplicationID);
        // build the URL:
        final Map<String, Object> selectedPayloadObject =
                new LinkedHashMap<String, Object>();
        // add the "clientIdentifiers" to the "alias" fie;d
        selectedPayloadObject.put("alias", clientIdentifiers);
        selectedPayloadObject.put("message", json);
        // transform to JSONString:
        String payload = transformJSON(selectedPayloadObject);
        // fire!
        submitPayload(sb.toString(), payload, pushApplicationID, masterSecret);
    }

    private void submitPayload(String url, String jsonPayloadObject, String pushApplicationId, String masterSecret) {
        HttpURLConnection httpURLConnection = null;
        try {
            URL pushUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) pushUrl.openConnection();
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
        } finally {
            httpURLConnection.disconnect();
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
