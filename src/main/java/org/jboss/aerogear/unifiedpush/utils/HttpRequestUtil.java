/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.unifiedpush.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.jboss.aerogear.unifiedpush.ca.TrustStoreManagerService;
import org.jboss.aerogear.unifiedpush.model.ProxyConfig;
import org.jboss.aerogear.unifiedpush.model.TrustStoreConfig;

/**
 * Util class for URLConnection creation
 */
public class HttpRequestUtil {

    private HttpRequestUtil() {
        // no-op
    }

    /**
     * Returns URLConnection that 'posts' the given JSON to the given UnifiedPush Server URL.
     *
     * @param url
     * @param encodedCredentials
     * @param jsonPayloadObject
     * @param charset
     * @param proxy
     * @param customTrustStore
     * @return {@link URLConnection}
     * @throws Exception
     */
    public static URLConnection post(String url, String encodedCredentials, String jsonPayloadObject, Charset charset,
            ProxyConfig proxy, TrustStoreConfig customTrustStore) throws Exception {

        if (url == null || encodedCredentials == null || jsonPayloadObject == null) {
            throw new IllegalArgumentException("arguments cannot be null");
        }

        byte[] bytes = jsonPayloadObject.getBytes(charset);
        URLConnection conn = getConnection(url, proxy);

        if (customTrustStore != null && customTrustStore.getTrustStorePath() != null && conn instanceof HttpsURLConnection) {
            KeyStore trustStore = TrustStoreManagerService
                    .getInstance()
                    .getTrustStoreManager()
                    .loadTrustStore(customTrustStore.getTrustStorePath(), customTrustStore.getTrustStoreType(),
                            customTrustStore.getTrustStorePassword());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tmf.getTrustManagers(), null);
            SSLSocketFactory sslSocketFactory = ctx.getSocketFactory();
            ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
        }

        conn.setDoOutput(true);
        conn.setUseCaches(false);
        ((HttpURLConnection) conn).setFixedLengthStreamingMode(bytes.length);
        conn.setRequestProperty("Authorization", "Basic " + encodedCredentials);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json, text/plain");

        // custom header, for UPS
        conn.setRequestProperty("aerogear-sender", "AeroGear Java Sender");
        ((HttpURLConnection) conn).setRequestMethod("POST");
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
     * Method to open/establish a URLConnection.
     *
     * @param url The URL to connect to.
     * @param proxy The proxy configuration.
     * @return {@link URLConnection}
     * @throws IOException
     */
    private static URLConnection getConnection(String url, final ProxyConfig proxy) throws IOException {
        URLConnection conn = null;

        if (proxy != null && proxy.getProxyUser() != null) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxy.getProxyUser(), proxy.getProxyPassword().toCharArray());
                }

            });
        }

        if (proxy != null) {
            Proxy thisProxy = new Proxy(proxy.getProxyType(), new InetSocketAddress(proxy.getProxyHost(), proxy.getProxyPort()));
            conn = new URL(url).openConnection(thisProxy);
        } else {
            conn = new URL(url).openConnection();
        }

        return conn;
    }
}
