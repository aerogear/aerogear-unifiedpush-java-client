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

import static org.jboss.aerogear.unifiedpush.utils.ValidationUtils.isEmpty;
import static org.jboss.aerogear.unifiedpush.utils.ValidationUtils.isSuccess;
import net.iharder.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.aerogear.unifiedpush.message.MessageResponseCallback;
import org.jboss.aerogear.unifiedpush.message.UnifiedMessage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
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
        if (isEmpty(rootServerURL)) {
            throw new IllegalStateException("server can not be null");
        }
        this.setServerURL(rootServerURL);
    }

    public SenderClient() {

    }

    /**
     * Construct the URL fired against the Unified Push Server
     *
     * @return a StringBuilder containing the constructed URL
     */
    protected String buildUrl() {
        if (isEmpty(serverURL)) {
            throw new IllegalStateException("server can not be null");
        }

        return serverURL + "rest/sender/";
    }

    @Override
    public void send(UnifiedMessage unifiedMessage, MessageResponseCallback callback) {
        final Map<String, Object> selectedPayloadObject = prepareMessage(unifiedMessage);
        int statusCode;
        // transform to JSONString:
        String payload = transformJSON(selectedPayloadObject);
        // fire!
        submitPayload(buildUrl(), payload, unifiedMessage.getPushApplicationId(), unifiedMessage.getMasterSecret(), callback);
    }

    @Override
    public void send(UnifiedMessage unifiedMessage) {
        final Map<String, Object> selectedPayloadObject = prepareMessage(unifiedMessage);
        int statusCode;
        // transform to JSONString:
        String payload = transformJSON(selectedPayloadObject);
        // fire!
        submitPayload(buildUrl(), payload, unifiedMessage.getPushApplicationId(), unifiedMessage.getMasterSecret(), null);
    }

    /**
     * Flatten the given {@link UnifiedMessage} into a {@link Map}
     * @param {@link UnifiedMessage} to be flatten
     * @return a {@link Map}
     */
    private Map<String, Object> prepareMessage(UnifiedMessage unifiedMessage) {

        final Map<String, Object> payloadObject =
                new LinkedHashMap<String, Object>();

        if (!isEmpty(unifiedMessage.getAliases())) {
            payloadObject.put("alias", unifiedMessage.getAliases());
        }

        if (!isEmpty(unifiedMessage.getCategory())) {
            payloadObject.put("category", unifiedMessage.getCategory());
        }

        if (!isEmpty(unifiedMessage.getDeviceType())) {
            payloadObject.put("deviceType", unifiedMessage.getDeviceType());
        }

        if (!isEmpty(unifiedMessage.getVariants())) {
            payloadObject.put("variants", unifiedMessage.getVariants());
        }

        if (!isEmpty(unifiedMessage.getSimplePushMap())) {
            payloadObject.put("simple-push", unifiedMessage.getSimplePushMap());
        }

        if (!isEmpty(unifiedMessage.getAttributes())) {
            payloadObject.put("message", unifiedMessage.getAttributes());
        }
        return payloadObject;
    }

    /**
     * The actual method that does the real send and connection handling
     *
     * @param url
     * @param jsonPayloadObject
     * @param pushApplicationId
     * @param masterSecret
     * @param callback
     */
    private void submitPayload(String url, String jsonPayloadObject, String pushApplicationId, String masterSecret, MessageResponseCallback callback) {
        String credentials = pushApplicationId + ":" + masterSecret;
        int statusCode = 0;

        HttpURLConnection httpURLConnection = null;
        try {
            String encoded = Base64.encodeBytes(credentials.getBytes(UTF_8));

            // POST the payload to the UnifiedPush Server
            httpURLConnection = post(url, encoded, jsonPayloadObject);

            statusCode = httpURLConnection.getResponseCode();
            logger.info(String.format("HTTP Response code form UnifiedPush Server: %s", statusCode));

            // if we got a redirect, let's extract the 'Location' header from the response
            // and submit the payload again
            if (isRedirect(statusCode)) {
                String redirectURL = httpURLConnection.getHeaderField("Location");
                logger.info(String.format("Performing redirect to '%s'", redirectURL));
                // execute the 'redirect'
                this.submitPayload(redirectURL, jsonPayloadObject, pushApplicationId, masterSecret, callback);
            } else {
                if(callback != null){
                    callback.onComplete(statusCode);
                }
            }

        } catch (Exception e) {
            logger.severe("Send did not succeed: " + e.getMessage());
            if(callback != null){
                callback.onError(e);
            }
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

        if (url == null || encodedCredentials == null || jsonPayloadObject == null) {
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

    /**
     * checks if the given status code is a redirect (301, 302 or 303 response status code)
     */
    private boolean isRedirect(int statusCode) {
        if (statusCode == HttpURLConnection.HTTP_MOVED_PERM || statusCode == HttpURLConnection.HTTP_MOVED_TEMP || statusCode == HttpURLConnection.HTTP_SEE_OTHER) {
            return true;
        }
        return false;
    }

    /**
     * A simple utility to tranform an {@link Object} into a json {@link String}
     */
    private String transformJSON(Object value) {
        ObjectMapper om = new ObjectMapper();
        String stringPayload = null;
        try {
            stringPayload = om.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encode JSON payload");
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
