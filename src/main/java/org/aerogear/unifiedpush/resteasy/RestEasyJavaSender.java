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

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.aerogear.unifiedpush.JavaSender;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

public class RestEasyJavaSender implements JavaSender {
    
    // final?
    private String serverURL;
    
    public RestEasyJavaSender(String serverURL) {
        this.serverURL = serverURL;
    }

    @Override
    public void broadcast(Map<String, ? extends Object> json,
            String pushApplicationID) {
        
        // setup the client class:
        StringBuilder sb = new StringBuilder();
        sb.append(serverURL)
          .append("/")
          .append(pushApplicationID);

        final ClientRequest req = new ClientRequest(sb.toString());

        // this all is really just JSON:
        req.accept(MediaType.APPLICATION_JSON_TYPE);
        req.body(MediaType.APPLICATION_JSON_TYPE, json); 

        // issue post against the Unified Push server:
        ClientResponse resp = null;
        try {
            resp = req.post();
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
