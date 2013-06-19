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

package org.aerogear.unifiedpush.resteasy;

import org.aerogear.unifiedpush.Client;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;

import javax.ws.rs.core.MediaType;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class RestEasyClient implements Client {

    private static final Logger logger = Logger.getLogger(RestEasyClient.class.getName());

    @Override
    public void post(Map<String, ? extends Object> json, String url, String pushApplicationID, String masterSecret) {
        // fire !
        submitPayload(url, json, pushApplicationID, masterSecret);
    }

    @Override
    public void post(Map<String, ? extends Object> json, List<String> clientIdentifiers, String url, String pushApplicationID, String masterSecret) {
        final Map<String, Object> selectedPayloadObject =
                new LinkedHashMap<String, Object>();

        // add the "clientIdentifiers" to the "alias" field
        selectedPayloadObject.put("alias", clientIdentifiers);
        selectedPayloadObject.put("message", json);

        // fire the prepared JSON
        submitPayload(url, selectedPayloadObject, pushApplicationID, masterSecret);
    }

    private void submitPayload(String url, Map<String, ? extends Object> json, String pushApplicationID, String masterSecret) {

        URL parsedUrl = null;
        try {
            parsedUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpHost targetHost = new HttpHost(parsedUrl.getHost(), parsedUrl.getPort(), "http");
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(pushApplicationID, masterSecret)
        );
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);
        BasicHttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.AUTH_CACHE, authCache);
        ClientExecutor clientExecutor = new ApacheHttpClient4Executor(
                httpClient, localContext);
        final ClientRequest clientRequest = new ClientRequest(url, clientExecutor);

        // this all is really just JSON:
        clientRequest.accept(MediaType.APPLICATION_JSON_TYPE);

        clientRequest.body(MediaType.APPLICATION_JSON_TYPE, json);

        // issue post against the Unified Push server:
        ClientResponse<String> resp = null;
        try {
            resp = clientRequest.post(String.class);
            int statusCode = resp.getStatus();
            if (statusCode != 200) {
                logger.severe("Receiving status code: " + statusCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // clean up:
            if (resp != null) {
                resp.releaseConnection();
            }
        }
    }
}
