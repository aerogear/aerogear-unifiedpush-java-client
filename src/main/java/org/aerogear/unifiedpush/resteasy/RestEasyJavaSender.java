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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.aerogear.unifiedpush.JavaSender;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

public class RestEasyJavaSender implements JavaSender {
    
    private static final Logger logger = Logger.getLogger(RestEasyJavaSender.class.getName());
    
    // final?
    private String serverURL;
    
    public RestEasyJavaSender(String rootServerURL) {
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

        //  build the broadcast URL:
        StringBuilder sb = new StringBuilder();
        sb.append(serverURL)
          .append("rest/sender/broadcast/")
          .append(pushApplicationID);

        // fire !
        submitPayload(sb.toString(), json, pushApplicationID);
        
    }

    @Override
    public void sendTo(List<String> clientIdentifiers,
            Map<String, ? extends Object> json, String pushApplicationID) {

        //  build the broadcast URL:
        StringBuilder sb = new StringBuilder();
        sb.append(serverURL)
          .append("rest/sender/selected/")
          .append(pushApplicationID);

        
        final Map<String, Object> selectedPayloadObject = 
                new LinkedHashMap<String, Object>();
        
        // add the "clientIdentifiers" to the "alias" fie;d
        selectedPayloadObject.put("alias", clientIdentifiers);
        selectedPayloadObject.put("message", json);
        
        // fire the prepared JSON
        submitPayload(sb.toString(),selectedPayloadObject, pushApplicationID);
    }

    private void submitPayload(String url, Map<String, ? extends Object> json,
            String pushApplicationID) {
        final ClientRequest req = new ClientRequest(url);

        // this all is really just JSON:
        req.accept(MediaType.APPLICATION_JSON_TYPE);
        req.body(MediaType.APPLICATION_JSON_TYPE, json); 

        // issue post against the Unified Push server:
        ClientResponse<String> resp = null;
        try {
            resp = req.post(String.class);
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
