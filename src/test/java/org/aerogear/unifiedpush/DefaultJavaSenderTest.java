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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class DefaultJavaSenderTest {

    private DefaultJavaSender defaultJavaSender;

    private Client client;

    @Before
    public void setup() {
        client = mock(Client.class);
        defaultJavaSender = new DefaultJavaSender("http://localhost:8080/ag-push", client);
     }

    @Test
    public void sendSingleBroadcastMessage() {
        long start = System.currentTimeMillis();
        Map<String, String> jsonPlayload = new HashMap<String, String>();

        jsonPlayload.put("alert", "Hello from Java Sender API, via JUnit");
        jsonPlayload.put("sound", "default");
        // send it out:
        defaultJavaSender.broadcast(jsonPlayload, "8e976eab-b628-46e5-8790-13f70289af37");

        long end = System.currentTimeMillis();
        System.out.println("Took: " + (end-start));
    }

    @Test
    public void sendMultipleBroadcastMessages() {
        long start = System.currentTimeMillis();

        for (int i=0; i<10;i++) {
            Map<String, String> jsonPlayload = new HashMap<String, String>();
            jsonPlayload.put("alert", "Count  : " + i );
            jsonPlayload.put("sound", "default");

            // send it out:
            defaultJavaSender.broadcast(jsonPlayload, "8e976eab-b628-46e5-8790-13f70289af37");
        }

        long end = System.currentTimeMillis();
        System.out.println("Took: " + (end-start));
    }

    @Test
    public void sendSelectiveSendToOne() {
        long start = System.currentTimeMillis();

        List<String> identifiers = new ArrayList<String>();
        identifiers.add("mwessendorf2");

        Map<String, String> jsonPlayload = new HashMap<String, String>();
        jsonPlayload.put("alert", "Hello from Java Sender API, via JUnit");
        jsonPlayload.put("sound", "default");

        // send it out:
        defaultJavaSender.sendTo(identifiers, jsonPlayload, "8e976eab-b628-46e5-8790-13f70289af37");

        long end = System.currentTimeMillis();
        System.out.println("Took: " + (end-start));
    }
}
