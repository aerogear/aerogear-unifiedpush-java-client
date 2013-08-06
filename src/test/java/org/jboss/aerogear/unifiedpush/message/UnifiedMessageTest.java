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
package org.jboss.aerogear.unifiedpush.message;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class UnifiedMessageTest {

    @Test
    public void simpleBroadcastMessageTest() {
        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .pushApplicationId("c7fc6525-5506-4ca9-9cf1-55cc261ddb9c")
                .masterSecret("8b2f43a9-23c8-44fe-bee9-d6b0af9e316b")
                .attribute("custom", "customValue")
                .build();
        assertEquals("c7fc6525-5506-4ca9-9cf1-55cc261ddb9c", unifiedMessage.getPushApplicationId());
        assertEquals("8b2f43a9-23c8-44fe-bee9-d6b0af9e316b", unifiedMessage.getMasterSecret());
        assertEquals("customValue", unifiedMessage.getAttributes().get("custom"));
    }

    @Test
    public void specialKeysTests() {
        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .alert("Hello from Java Sender API, via JUnit")
                .sound("default")
                .badge("1")
                .build();
        assertEquals("Hello from Java Sender API, via JUnit", unifiedMessage.getAttributes().get("alert"));
        assertEquals("default", unifiedMessage.getAttributes().get("sound"));
        assertEquals(1, unifiedMessage.getAttributes().get("badge"));
    }

    @Test
    public void simpleSelectiveMessageWithAliasesTest() {
        List aliases = new ArrayList<String>();
        aliases.add("mike");

        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .aliases(aliases)
                .build();
        assertEquals(1, unifiedMessage.getAliases().size());
    }

    @Test
    public void simpleSelectiveMessageWithVariantsTest() {
        List variants = new ArrayList<String>();
        variants.add("c3f0a94f-48de-4b77-a08e-68114460857e"); // e.g. HR Premium
        variants.add("444939cd-ae63-4ce1-96a4-de74b77e3737"); // e.g. HR Free

        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .variants(variants)
                .build();
        assertEquals(2, unifiedMessage.getVariants().size());
    }

    @Test
    public void simpleSelectiveMessageWithDevicesTest() {
        List devices = new ArrayList<String>();
        devices.add("iPad");

        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .deviceType(devices)
                .build();
        assertEquals(1, unifiedMessage.getDeviceType().size());
    }

    @Test
    public void simplePushBroadcastMessageTest() {
        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .simplePush("version=1")
                .build();
        assertEquals("version=1", unifiedMessage.getAttributes().get("simple-push"));
    }

    @Test
    public void simplePushBroadcastWrongVersionFormatMessageTest() {
        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .simplePush("2")
                .build();
        assertEquals("version=2", unifiedMessage.getAttributes().get("simple-push"));
    }

    @Test
    public void simplePushSelectiveVersionMessageTest() {
        Map<String, String> simplePush = new HashMap<String, String>();
        simplePush.put("channel1", "version=1");
        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .simplePush(simplePush)
                .build();
        assertEquals("version=1", unifiedMessage.getSimplePushMap().get("channel1"));
    }

    @Test
    public void simplePushSelectiveWrongVersionFormatMessageTest() {
        Map<String, String> simplePush = new HashMap<String, String>();
        simplePush.put("channel1", "1");
        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .simplePush(simplePush)
                .build();
        assertEquals("version=1", unifiedMessage.getSimplePushMap().get("channel1"));
    }
}
