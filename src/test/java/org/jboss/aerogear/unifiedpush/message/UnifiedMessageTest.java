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

import org.jboss.aerogear.unifiedpush.message.windows.BadgeType;
import org.jboss.aerogear.unifiedpush.message.windows.TileType;
import org.jboss.aerogear.unifiedpush.message.windows.ToastType;
import org.jboss.aerogear.unifiedpush.message.windows.Type;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;

import static org.junit.Assert.*;

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
        assertEquals("Hello from Java Sender API, via JUnit", unifiedMessage.getMessage().getObject().getAlert());
        assertEquals("default", unifiedMessage.getMessage().getObject().getSound());
        assertEquals(1, unifiedMessage.getMessage().getObject().getBadge());
        assertEquals(3600, unifiedMessage.getConfig().getObject().getTimeToLive());
    }

    @Test
    public void simpleSelectiveMessageWithAliasesTest() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withCriteria()
                .aliases("mike")
                .build();
        assertEquals(1, unifiedMessage.getCriteria().getObject().getAliases().size());
    }

    @Test
    public void simpleSelectiveMessageWithVariantsTest() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withCriteria()
                .variants("c3f0a94f-48de-4b77-a08e-68114460857e", "444939cd-ae63-4ce1-96a4-de74b77e3737")
                .build();
        assertEquals(2, unifiedMessage.getCriteria().getObject().getVariants().size());
    }

    @Test
    public void simpleSelectiveMessageWithDevicesTest() {
        List<String> devices = new ArrayList<String>();
        devices.add("iPad");

        UnifiedMessage unifiedMessage = UnifiedMessage.withCriteria()
                .deviceType(devices)
                .build();
        assertEquals(1, unifiedMessage.getCriteria().getObject().getDeviceTypes().size());
    }

    @Test
    public void simpleSelectiveMessageWithCategoriesTest() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withCriteria()
                .categories("sports", "world cup")
                .build();
        assertEquals(2, unifiedMessage.getCriteria().getObject().getCategories().size());
    }

    @Test
    public void simpleSelectiveMessageWithCategoriesAsSetTest() {
        final Set<String> categories = new HashSet<String>();
        categories.add("sports");
        categories.add("world cup");
        UnifiedMessage unifiedMessage = UnifiedMessage.withCriteria()
                .categories(categories)
                .build();
        assertEquals(2, unifiedMessage.getCriteria().getObject().getCategories().size());
    }

    @Test
    public void simplePushVersionMessageTest() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .simplePush("version=1")
                .build();
        assertEquals("version=1", unifiedMessage.getMessage().getObject().getSimplePush());
    }

    @Test
    public void simplePushWrongVersionFormatMessageTest() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .simplePush("1")
                .build();
        assertEquals("version=1", unifiedMessage.getMessage().getObject().getSimplePush());
    }

    @Test
    public void contentAvailable() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .apns()
                    .contentAvailable()
                    .build()
                .build();
        assertTrue(unifiedMessage.getMessage().getObject().getApns().isContentAvailable());
    }

    @Test
    public void noContentAvailable() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage().build();
        assertFalse(unifiedMessage.getMessage().getObject().getApns().isContentAvailable());
    }

    @Test
    public void actionCategoryTest() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .apns()
                   .actionCategory("myInteractiveNotification")
                   .build()
                .build();
        assertEquals("myInteractiveNotification", unifiedMessage.getMessage().getObject().getApns().getActionCategory());
    }

    @Test
    public void customAttributes() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .userData("foo-key", "foo-value")
                .userData("bar-key", "bar-value")
                .build();
        assertEquals("foo-value", ((Map) unifiedMessage.getMessage().getObject().getUserData()).get("foo-key"));
        assertEquals("bar-value", ((Map) unifiedMessage.getMessage().getObject().getUserData()).get("bar-key"));
    }

    @Test
    public void customAttributesAsMap() {
        final Map<String, Object> customAttributes = new HashMap<String, Object>();
        customAttributes.put("foo-key", "foo-value");
        customAttributes.put("bar-key", "bar-value");
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .userData(customAttributes)
                .build();
        assertEquals("foo-value", ((Map) unifiedMessage.getMessage().getObject().getUserData()).get("foo-key"));
        assertEquals("bar-value", ((Map) unifiedMessage.getMessage().getObject().getUserData()).get("bar-key"));
    }

    @Test
    public void windowsBadgeMessage() {

        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .badge("5")
                .windows()
                    .badgeType(BadgeType.busy)
                    .build()
                .build();
        assertEquals(Type.badge, unifiedMessage.getMessage().getObject().getWindows().getType());
        assertEquals(BadgeType.busy, unifiedMessage.getMessage().getObject().getWindows().getBadge());
    }

    @Test
    public void windowsTileMessage() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .windows()
                 .tileType(TileType.TileSquarePeekImageAndText01)
                 .textFields(Arrays.asList("bob","alice"))
                 .images(Arrays.asList("img/bob.png","img/alice.png"))
                 .build()
                .build();
        assertEquals(Type.tile, unifiedMessage.getMessage().getObject().getWindows().getType());
        assertEquals(TileType.TileSquarePeekImageAndText01, unifiedMessage.getMessage().getObject().getWindows().getTileType());
        assertEquals(2, unifiedMessage.getMessage().getObject().getWindows().getTextFields().size());
        assertEquals(2,unifiedMessage.getMessage().getObject().getWindows().getImages().size());
    }

    @Test
    public void windowsToastMessage() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .windows()
                 .toastType(ToastType.ToastText01)
                 .build()
                .build();
        assertEquals(Type.toast, unifiedMessage.getMessage().getObject().getWindows().getType());
        assertEquals(ToastType.ToastText01, unifiedMessage.getMessage().getObject().getWindows().getToastType());
    }

    @Test
    public void windowsRawMessage() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .windows()
                 .raw()
                 .build()
                .build();
        assertEquals(Type.raw, unifiedMessage.getMessage().getObject().getWindows().getType());
    }

    @Test
    public void windowsPageTest() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .windows()
                .page("/MainPage.xaml")
                .build()
                .build();
        assertEquals("/MainPage.xaml", unifiedMessage.getMessage().getObject().getWindows().getPage());
    }

    @Test
    public void windowsAndIosCombinedMessage() {
        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .windows()
                  .raw()
                  .build()
                .apns()
                  .contentAvailable()
                  .build()
                .build();
        assertEquals(Type.raw, unifiedMessage.getMessage().getObject().getWindows().getType());
        assertEquals(true, unifiedMessage.getMessage().getObject().getApns().isContentAvailable());
    }


}
