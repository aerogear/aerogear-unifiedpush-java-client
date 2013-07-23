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
import org.jboss.aerogear.unifiedpush.message.UnifiedMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SenderClient implements JavaSender {

    private static final Logger logger = Logger.getLogger(SenderClient.class.getName());

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private String serverURL;

    public SenderClient(String rootServerURL) {
        if (rootServerURL == null) {
            throw new IllegalStateException("server can not be null");
        }
        this.setServerURL(rootServerURL);
    }

    public SenderClient() {

    }

    /**
     * Construct the URL fired against the Unified Push Server
     * @param type , a String defining the sending method, could be "broadcast" or "selected"
     * @param pushApplicationID, the push Application identifier
     * @return a stringBuilder containing the constructed URL
     */
    protected StringBuilder buildUrl(String type, String pushApplicationID) {
        if(serverURL == null){
           throw new IllegalStateException("server can not be null");
        }
        //  build the broadcast URL:
        StringBuilder sb = new StringBuilder();
        sb.append(serverURL)
                .append("rest/sender/")
                .append(type);
        return sb;
    }

    @Override
    public void broadcast(UnifiedMessage unifiedMessage) {
        StringBuilder sb = buildUrl("broadcast", unifiedMessage.getPushApplicationId());
        // transform JSON:
        String payload = transformJSON(unifiedMessage.getAttributes());
        // fire!
        submitPayload(sb.toString(), payload, unifiedMessage.getPushApplicationId(), unifiedMessage.getMasterSecret());
    }

    @Override
    public void sendTo(UnifiedMessage unifiedMessage) {
        StringBuilder sb = buildUrl("selected", unifiedMessage.getPushApplicationId());
        // build the URL:
        final Map<String, Object> selectedPayloadObject =
                new LinkedHashMap<String, Object>();
        // add the "clientIdentifiers" to the "alias" fie;d

        selectedPayloadObject.put("alias", unifiedMessage.getAliases());

        selectedPayloadObject.put("message", unifiedMessage.getAttributes());
        // transform to JSONString:
        String payload = transformJSON(selectedPayloadObject);

        // fire!
        submitPayload(sb.toString(), payload, unifiedMessage.getPushApplicationId(), unifiedMessage.getMasterSecret());
    }

    private void submitPayload(String url, String jsonPayloadObject, String pushApplicationId, String masterSecret) {
        String credentials = pushApplicationId + ":" + masterSecret;
        
        HttpURLConnection httpURLConnection = null;
        try {
            String encoded = Base64.encodeBytes(credentials.getBytes(UTF_8));

            // POST the payload to the UnifiedPush Server
            httpURLConnection = post(url, encoded, jsonPayloadObject);

            int status = httpURLConnection.getResponseCode();
            logger.info(String.format("HTTP Response code form UnifiedPush Server: %s", status));

        } catch (MalformedURLException e) {
            logger.severe("Invalid Server URL");
        } catch (IOException e) {
            logger.severe("IO Exception");
        } finally {
            // tear down
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }
    /**
     * Returns HttpURLConnection that 'posts' the given JSON to the given UnifiedPush Server URL.
     */
    private HttpURLConnection post(String url, String encodedCredentials, String jsonPayloadObject) throws IOException {

        if (url == null || encodedCredentials == null || jsonPayloadObject == null ) {
            throw new IllegalArgumentException("arguments cannot be null");
        }

        byte[] bytes = jsonPayloadObject.getBytes(UTF_8);
        HttpURLConnection conn = getConnection(url);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setFixedLengthStreamingMode(bytes.length);
        conn.setRequestProperty("Authorization", "Basic " + encodedCredentials);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestMethod("POST");
        OutputStream out = null;
        try {
            out = conn.getOutputStream();
            out.write(bytes);
        } finally {
            // in case something blows up, while writing
            // the payload, we wanna close the stream:
            if (out != null) {
                out.close();
            }
        }
        return conn;
    }

    /**
     * Convenience method to open/establish a HttpURLConnection.
     */
    private HttpURLConnection getConnection(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        return conn;
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

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
         if (!serverURL.endsWith("/")) {
             serverURL = serverURL.concat("/");
        }
        this.serverURL = serverURL;
    }
}
