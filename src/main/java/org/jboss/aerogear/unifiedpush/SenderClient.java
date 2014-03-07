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
import net.iharder.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.aerogear.unifiedpush.message.MessageResponseCallback;
import org.jboss.aerogear.unifiedpush.message.UnifiedMessage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SenderClient implements JavaSender {

    private static final Logger logger = Logger.getLogger(SenderClient.class.getName());

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private String serverURL;
    private String proxyHost;
    private int proxyPort;
    private String proxyUser;
    private String proxyPassword;
    private Proxy.Type proxyType;

    public SenderClient(String rootServerURL) {
        this.setServerURL(rootServerURL);
    }

    public SenderClient() {

    }

    /**
     * Only called by builder.
     */
    private SenderClient(Builder builder) {
        this.setServerURL(builder.rootServerURL);
        this.proxyHost = builder.proxyHost;
        this.proxyPort = builder.proxyPort;
        this.proxyUser = builder.proxyUser;
        this.proxyPassword = builder.proxyPassword;
        this.proxyType = builder.proxyType;
    }

    /**
     * Builder to build Client with more configuration.
     */
    public static class Builder {

        private String rootServerURL;
        private String proxyHost;
        private int proxyPort;
        private String proxyUser;
        private String proxyPassword;
        private Proxy.Type proxyType = Proxy.Type.HTTP;

        /**
         * Set the root URL to connect.
         *
         * @param rootServerURL The root URL.
         * @return the current {@link Builder} instance
         */
        public Builder rootServerURL(String rootServerURL) {
            this.rootServerURL = rootServerURL;
            return this;
        }

        /**
         * Specify proxy that should be used to connect.
         *
         * @param proxyHost Hostname of proxy.
         * @param proxyPort Port of proxy.
         * @return the current {@link Builder} instance
         */
        public Builder proxy(String proxyHost, int proxyPort) {
            this.proxyHost = proxyHost;
            this.proxyPort = proxyPort;
            return this;
        }

        /**
         * If proxy needs authentication, specify User.
         *
         * @param proxyUser Username for authentication.
         * @return the current {@link Builder} instance
         */
        public Builder proxyUser(String proxyUser) {
            this.proxyUser = proxyUser;
            return this;
        }

        /**
         * Sets password used with specified user.
         *
         * @param proxyPassword Password for user authentication.
         * @return the current {@link Builder} instance
         */
        public Builder proxyPassword(String proxyPassword) {
            this.proxyPassword = proxyPassword;
            return this;
        }

        /**
         * Configure type of proxy.
         *
         * @param proxyType Type of proxy as
         * @return the current {@link Builder} instance
         */
        public Builder proxyType(Proxy.Type proxyType) {
            this.proxyType = proxyType;
            return this;
        }

        /**
         * Build the {@link SenderClient}.
         *
         * @return the built up {@link SenderClient}
         */
        public SenderClient build() {
            return new SenderClient(this);
        }
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
        final Map<String, Object> payloadObject = prepareMessage(unifiedMessage);
        // transform to JSONString:
        String jsonString = toJSONString(payloadObject);
        // fire!
        submitPayload(buildUrl(), jsonString, unifiedMessage.getPushApplicationId(), unifiedMessage.getMasterSecret(), callback);
    }

    @Override
    public void send(UnifiedMessage unifiedMessage) {
        this.send(unifiedMessage, null);
    }

    /**
     * Flatten the given {@link UnifiedMessage} into a {@link Map}
     *
     * @param {@link UnifiedMessage} to be flatten
     * @return a {@link Map}
     */
    private Map<String, Object> prepareMessage(UnifiedMessage unifiedMessage) {

        final Map<String, Object> payloadObject = new LinkedHashMap<String, Object>();

        if (!isEmpty(unifiedMessage.getAliases())) {
            payloadObject.put("alias", unifiedMessage.getAliases());
        }

        if (!isEmpty(unifiedMessage.getCategories())) {
            payloadObject.put("categories", unifiedMessage.getCategories());
        }

        if (!isEmpty(unifiedMessage.getDeviceType())) {
            payloadObject.put("deviceType", unifiedMessage.getDeviceType());
        }

        if (!isEmpty(unifiedMessage.getVariants())) {
            payloadObject.put("variants", unifiedMessage.getVariants());
        }

        if (!isEmpty(unifiedMessage.getAttributes())) {
            payloadObject.put("message", unifiedMessage.getAttributes());
        }
        if (!isEmpty(unifiedMessage.getSimplePush())) {
            payloadObject.put("simple-push", unifiedMessage.getSimplePush());
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
            logger.info(String.format("HTTP Response code from UnifiedPush Server: %s", statusCode));

            // if we got a redirect, let's extract the 'Location' header from the response
            // and submit the payload again
            if (isRedirect(statusCode)) {
                String redirectURL = httpURLConnection.getHeaderField("Location");
                logger.info(String.format("Performing redirect to '%s'", redirectURL));
                // execute the 'redirect'
                this.submitPayload(redirectURL, jsonPayloadObject, pushApplicationId, masterSecret, callback);
            } else {
                if (callback != null) {
                    callback.onComplete(statusCode);
                }
            }

        } catch (Exception e) {
            logger.severe("Send did not succeed: " + e.getMessage());
            if (callback != null) {
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
     * Returns HttpURLConnection that 'posts' the given JSON to the given
     * UnifiedPush Server URL.
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
        HttpURLConnection conn = null;

        if (proxyUser != null) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
                }

            });
        }

        if (proxyHost != null) {
            Proxy proxy = new Proxy(proxyType, new InetSocketAddress(proxyHost, proxyPort));
            conn = (HttpURLConnection) new URL(url).openConnection(proxy);
        } else {
            conn = (HttpURLConnection) new URL(url).openConnection();
        }

        return conn;
    }

    /**
     * checks if the given status code is a redirect (301, 302 or 303 response
     * status code)
     */
    private boolean isRedirect(int statusCode) {
        if (statusCode == HttpURLConnection.HTTP_MOVED_PERM || statusCode == HttpURLConnection.HTTP_MOVED_TEMP || statusCode == HttpURLConnection.HTTP_SEE_OTHER) {
            return true;
        }
        return false;
    }

    /**
     * A simple utility to transforms an {@link Object} into a json
     * {@link String}
     */
    private String toJSONString(Object value) {
        ObjectMapper om = new ObjectMapper();
        String stringPayload = null;
        try {
            stringPayload = om.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encode JSON payload");
        }
        return stringPayload;
    }

    /**
     * Get the used server URL.
     * @return The Server that is used
     */
    public String getServerURL() {
        return serverURL;
    }

    /**
     * Set the server URL that is used to send Messages.
     * @param serverURL A server URL
     */
    public void setServerURL(String serverURL) {
        if (isEmpty(serverURL)) {
            throw new IllegalStateException("server can not be null");
        } else if (!serverURL.endsWith("/")) {
            serverURL = serverURL.concat("/");
        }
        this.serverURL = serverURL;
    }

    /**
     * Get the proxy Hostname that is configured.
     * @return A proxy hostname
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * Get the proxy port.
     * @return A proxy port
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * Get the specified proxy user.
     * @return Proxy username
     */
    public String getProxyUser() {
        return proxyUser;
    }

    /**
     * Get the password for proxy user.
     * @return proxy user password
     */
    public String getProxyPassword() {
        return proxyPassword;
    }

    /**
     * Get the proxy type that is used in proxy connection.
     * @return A {@link Proxy.Type}
     */
    public Proxy.Type getProxyType() {
        return proxyType;
    }
}
