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
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UnifiedMessageTest {

    @Test
    public void specialKeysTests() {
        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .alert("Hello from Java Sender API, via JUnit")
                .sound("default")
                .badge("1")
                .timeToLive(3600)
                .build();
        assertEquals("Hello from Java Sender API, via JUnit", unifiedMessage.getAttributes().get("alert"));
        assertEquals("default", unifiedMessage.getAttributes().get("sound"));
        assertEquals(1, unifiedMessage.getAttributes().get("badge"));
        assertEquals(3600, unifiedMessage.getAttributes().get("ttl"));
    }

    @Test
    public void simpleSelectiveMessageWithAliasesTest() {
        List<String> aliases = new ArrayList<String>();
        aliases.add("mike");

        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .aliases(aliases)
                .build();
        assertEquals(1, unifiedMessage.getAliases().size());
    }

    @Test
    public void simpleSelectiveMessageWithVariantsTest() {
        List<String> variants = new ArrayList<String>();
        variants.add("c3f0a94f-48de-4b77-a08e-68114460857e"); // e.g. HR Premium
        variants.add("444939cd-ae63-4ce1-96a4-de74b77e3737"); // e.g. HR Free

        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .variants(variants)
                .build();
        assertEquals(2, unifiedMessage.getVariants().size());
    }

    @Test
    public void simpleSelectiveMessageWithDevicesTest() {
        List<String> devices = new ArrayList<String>();
        devices.add("iPad");

        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .deviceType(devices)
                .build();
        assertEquals(1, unifiedMessage.getDeviceType().size());
    }

    @Test
    public void simpleSelectiveMessageWithCategoriesTest() {
        List<String> categories = new ArrayList<String>();
        categories.add("sports");
        categories.add("world cup");

        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .deviceType(categories)
                .build();
        assertEquals(2, unifiedMessage.getDeviceType().size());
    }

    @Test
    public void simplePushVersionMessageTest() {
        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .simplePush("version=1")
                .build();
        assertEquals("version=1", unifiedMessage.getSimplePush());
    }

    @Test
    public void simplePushWrongVersionFormatMessageTest() {
        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .simplePush("1")
                .build();
        assertEquals("version=1", unifiedMessage.getSimplePush());
    }
}
