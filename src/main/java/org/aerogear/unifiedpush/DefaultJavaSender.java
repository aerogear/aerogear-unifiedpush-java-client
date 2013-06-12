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
package org.aerogear.unifiedpush;

import java.util.List;
import java.util.Map;

public class DefaultJavaSender implements JavaSender{

    // final?
    private String serverURL;

    private Client client;

    public DefaultJavaSender(String rootServerURL, Client client) {
        if (rootServerURL == null) {
            throw new IllegalStateException("server can not be null");
        }

        if (! rootServerURL.endsWith("/") ) {
            rootServerURL = rootServerURL.concat("/");
        }
        this.serverURL = rootServerURL;
        this.client = client;
    }

    protected StringBuilder buildUrl(String type, String pushApplicationID) {
        //  build the broadcast URL:
        StringBuilder sb = new StringBuilder();
        sb.append(serverURL)
                .append("rest/sender/")
                .append(type + "/")
                .append(pushApplicationID);
        return sb;
    }

    @Override
    public void broadcast(Map<String, ? extends Object> json, String pushApplicationID) {
        StringBuilder sb = buildUrl("broadcast",pushApplicationID);
        client.post(json,sb.toString());
    }

    @Override
    public void sendTo(List<String> clientIdentifiers, Map<String, ? extends Object> json, String pushApplicationID) {
        StringBuilder sb = buildUrl("selected",pushApplicationID);
        client.post(json, clientIdentifiers, sb.toString());
    }
}
