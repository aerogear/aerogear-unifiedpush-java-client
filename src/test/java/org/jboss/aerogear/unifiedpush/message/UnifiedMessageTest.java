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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

public class UnifiedMessageTest {

    @Test
    public void specialKeysTests() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .alert("Hello from Java Sender API, via JUnit")
                .sound("default")
                .badge("1")
                .config()
                    .timeToLive(3600)
                .build();
        assertEquals("Hello from Java Sender API, via JUnit", unifiedMessage.getMessage().getAttributes().get("alert"));
        assertEquals("default", unifiedMessage.getMessage().getAttributes().get("sound"));
        assertEquals(1, unifiedMessage.getMessage().getAttributes().get("badge"));
        assertEquals(3600, unifiedMessage.getConfig().getAttributes().get("ttl"));
    }

    @Test
    public void simpleSelectiveMessageWithAliasesTest() {
        List<String> aliases = new ArrayList<String>();
        aliases.add("mike");

        UnifiedMessage unifiedMessage = UnifiedMessage.withCriteria()
                .aliases(aliases)
                .build();
        assertEquals(1, ((List) unifiedMessage.getCriteria().getAttributes().get("alias")).size());
    }

    @Test
    public void simpleSelectiveMessageWithVariantsTest() {
        List<String> variants = new ArrayList<String>();
        variants.add("c3f0a94f-48de-4b77-a08e-68114460857e"); // e.g. HR Premium
        variants.add("444939cd-ae63-4ce1-96a4-de74b77e3737"); // e.g. HR Free

        UnifiedMessage unifiedMessage = UnifiedMessage.withCriteria()
                .variants(variants)
                .build();
        assertEquals(2, ((List) unifiedMessage.getCriteria().getAttributes().get("variants")).size());
    }

    @Test
    public void simpleSelectiveMessageWithDevicesTest() {
        List<String> devices = new ArrayList<String>();
        devices.add("iPad");

        UnifiedMessage unifiedMessage = UnifiedMessage.withCriteria()
                .deviceType(devices)
                .build();
        assertEquals(1, ((List) unifiedMessage.getCriteria().getAttributes().get("deviceType")).size());
    }

    @Test
    public void simpleSelectiveMessageWithCategoriesTest() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withCriteria()
                .categories("sports", "world cup")
                .build();
        assertEquals(2, ((Set) unifiedMessage.getCriteria().getAttributes().get("categories")).size());
    }

    @Test
    public void simpleSelectiveMessageWithCategoriesAsSetTest() {
        final Set<String> categories = new HashSet<String>();
        categories.add("sports");
        categories.add("world cup");
        UnifiedMessage unifiedMessage = UnifiedMessage.withCriteria()
                .categories(categories)
                .build();
        assertEquals(2, ((Set) unifiedMessage.getCriteria().getAttributes().get("categories")).size());
    }

    @Test
    public void simplePushVersionMessageTest() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .simplePush("version=1")
                .build();
        assertEquals("version=1", unifiedMessage.getMessage().getAttributes().get("simple-push"));
    }

    @Test
    public void simplePushWrongVersionFormatMessageTest() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .simplePush("1")
                .build();
        assertEquals("version=1", unifiedMessage.getMessage().getAttributes().get("simple-push"));
    }

    @Test
    public void contentAvailable() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .contentAvailable()
                .build();
        assertTrue((Boolean) unifiedMessage.getMessage().getAttributes().get("content-available"));
    }

    @Test
    public void noContentAvailable() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage().build();
        assertNull(unifiedMessage.getMessage().getAttributes().get("content-available"));
    }

    @Test
    public void actionCategoryTest() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .actionCategory("myInteractiveNotification")
                .build();
        assertEquals("myInteractiveNotification", unifiedMessage.getMessage().getAttributes().get("action-category"));
    }

    @Test
    public void customAttributes() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .userData("foo-key", "foo-value")
                .userData("bar-key", "bar-value")
                .build();
        assertEquals("foo-value", ((Map) unifiedMessage.getMessage().getAttributes().get("user-data")).get("foo-key"));
        assertEquals("bar-value", ((Map) unifiedMessage.getMessage().getAttributes().get("user-data")).get("bar-key"));
    }

    @Test
    public void customAttributesAsMap() {
        final Map<String, Object> customAttributes = new HashMap<String, Object>();
        customAttributes.put("foo-key", "foo-value");
        customAttributes.put("bar-key", "bar-value");
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .userData(customAttributes)
                .build();
        assertEquals("foo-value", ((Map) unifiedMessage.getMessage().getAttributes().get("user-data")).get("foo-key"));
        assertEquals("bar-value", ((Map) unifiedMessage.getMessage().getAttributes().get("user-data")).get("bar-key"));
    }
}
