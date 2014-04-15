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
import org.jboss.aerogear.unifiedpush.http.HttpClient;
import org.jboss.aerogear.unifiedpush.message.MessageResponseCallback;
import org.jboss.aerogear.unifiedpush.message.UnifiedMessage;
import org.jboss.aerogear.unifiedpush.model.ProxyConfig;
import org.jboss.aerogear.unifiedpush.model.TrustStoreConfig;

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SenderClient implements JavaSender {

    private static final Logger logger = Logger.getLogger(SenderClient.class.getName());

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private String serverURL;
    private ProxyConfig proxy;
    private TrustStoreConfig customTrustStore;

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
        this.proxy = builder.proxy;
        this.customTrustStore = builder.customTrustStore;
    }

    /**
     * Builder to build Client with more configuration.
     */
    public static class Builder {

        private String rootServerURL;
        private ProxyConfig proxy;
        private TrustStoreConfig customTrustStore;

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
         * Set a custom trustStore.
         * 
         * @param trustStorePath The trustStore file path.
         * @param trustStoreType The trustStore type. If null the default type iss used.
         * @param trustStorePassword The trustStore password.
         * @return the current {@link Builder} instance
         */
        public Builder customTrustStore(String trustStorePath, String trustStoreType, String trustStorePassword) {
            this.customTrustStore = new TrustStoreConfig(trustStorePath, trustStoreType, trustStorePassword);
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
            if (this.proxy == null) {
                this.proxy = new ProxyConfig(Proxy.Type.HTTP);
            }
            this.proxy.setProxyHost(proxyHost);
            this.proxy.setProxyPort(proxyPort);
            return this;
        }

        /**
         * If proxy needs authentication, specify User.
         * 
         * @param proxyUser Username for authentication.
         * @return the current {@link Builder} instance
         */
        public Builder proxyUser(String proxyUser) {
            if (this.proxy == null) {
                this.proxy = new ProxyConfig(Proxy.Type.HTTP);
            }
            this.proxy.setProxyUser(proxyUser);
            return this;
        }

        /**
         * Sets password used with specified user.
         * 
         * @param proxyPassword Password for user authentication.
         * @return the current {@link Builder} instance
         */
        public Builder proxyPassword(String proxyPassword) {
            if (this.proxy == null) {
                this.proxy = new ProxyConfig(Proxy.Type.HTTP);
            }
            this.proxy.setProxyPassword(proxyPassword);
            return this;
        }

        /**
         * Configure type of proxy.
         * 
         * @param proxyType Type of proxy as
         * @return the current {@link Builder} instance
         */
        public Builder proxyType(Proxy.Type proxyType) {
            if (this.proxy == null) {
                this.proxy = new ProxyConfig(Proxy.Type.HTTP);
            }
            this.proxy.setProxyType(proxyType);
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
        if (isEmpty(this.getServerURL())) {
            throw new IllegalStateException("server can not be null");
        }

        return this.getServerURL() + "rest/sender/";
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
        if(unifiedMessage.getTimeToLive() != null) {
            payloadObject.put("ttl", unifiedMessage.getTimeToLive().intValue());
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
    private void submitPayload(String url, String jsonPayloadObject, String pushApplicationId, String masterSecret,
            MessageResponseCallback callback) {
        String credentials = pushApplicationId + ":" + masterSecret;
        int statusCode = 0;

        HttpURLConnection httpURLConnection = null;
        try {
            String encoded = Base64.encodeBytes(credentials.getBytes(UTF_8));

            // POST the payload to the UnifiedPush Server
            httpURLConnection = (HttpURLConnection) HttpClient.post(url, encoded, jsonPayloadObject, UTF_8, this.proxy,
                    this.customTrustStore);

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
     * checks if the given status code is a redirect (301, 302 or 303 response status code)
     */
    private boolean isRedirect(int statusCode) {
        if (statusCode == HttpURLConnection.HTTP_MOVED_PERM || statusCode == HttpURLConnection.HTTP_MOVED_TEMP
                || statusCode == HttpURLConnection.HTTP_SEE_OTHER) {
            return true;
        }
        return false;
    }

    /**
     * A simple utility to transforms an {@link Object} into a json {@link String}
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
     * 
     * @return The Server that is used
     */
    public String getServerURL() {
        return serverURL;
    }

    /**
     * Get the proxy cofniguration.
     * 
     * @return {@link ProxyConfig}
     */
    public ProxyConfig getProxy() {
        return proxy;
    }

    /**
     * Get the custom trustStore configuration;
     * 
     * @return {@link TrustStoreConfig}
     */
    public TrustStoreConfig getCustomTrustStore() {
        return customTrustStore;
    }

    /**
     * Set the server URL that is used to send Messages.
     * 
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
}
