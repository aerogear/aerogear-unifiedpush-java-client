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
package org.jboss.aerogear.unifiedpush.async;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Realm;
import com.ning.http.client.Response;
import org.codehaus.jackson.map.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.jboss.aerogear.unifiedpush.Client;

public class AsyncClient implements Client {

    private static final Logger logger = Logger.getLogger(AsyncClient.class.getName());

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
        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        try {
            Realm realm = new Realm.RealmBuilder()
                    .setPrincipal(pushApplicationId)
                    .setPassword(masterSecret)
                    .setUsePreemptiveAuth(true)
                    .setScheme(Realm.AuthScheme.BASIC)
                    .build();
            // currently, not really async...
            Response response =
                    asyncHttpClient.preparePost(url)
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-type", "application/json")
                            .setRealm(realm)
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

