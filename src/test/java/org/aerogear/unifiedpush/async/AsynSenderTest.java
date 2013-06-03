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

package org.aerogear.unifiedpush.async;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class AsynSenderTest {
    private AsyncJavaSender client = null;
    
    @Before
    public void setup() {
        client = new AsyncJavaSender("http://localhost:8080/ag-push/rest/sender/broadcast");
    }
    
    @Test
    public void sendBroadcastMessage() {
        Map<String, String> jsonPlayload = new HashMap<String, String>();
        
        jsonPlayload.put("alert", "Hello from Java Sender API, via JUnit ");
        jsonPlayload.put("sound", "default");

        // send it out:
        client.broadcast(jsonPlayload, "98a0c039-7ec3-44f9-ba7e-98a293f87b80");
    }
}
