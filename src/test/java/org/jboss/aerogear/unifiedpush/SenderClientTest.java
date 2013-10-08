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

import java.util.ArrayList;
import java.util.List;
import org.jboss.aerogear.unifiedpush.message.UnifiedMessage;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class SenderClientTest {

    private JavaSender defaultJavaSender;

    @Before
    public void setup() {
        defaultJavaSender = new SenderClient("http://localhost:8080/ag-push");
    }

    @Test
    public void sendSelectiveSendToOne() {
        long start = System.currentTimeMillis();

        List<String> identifiers = new ArrayList<String>();
        identifiers.add("mwessendorf2");

        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .pushApplicationId("c7fc6525-5506-4ca9-9cf1-55cc261ddb9c")
                .masterSecret("8b2f43a9-23c8-44fe-bee9-d6b0af9e316b")
                .alert("Hello from Java Sender API, via JUnit")
                .sound("default")
                .aliases(identifiers)
                .build();

        // send it out:
        defaultJavaSender.send(unifiedMessage);

        long end = System.currentTimeMillis();
        System.out.println("Took: " + (end - start));
    }
}
