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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A UnifiedMessage represents a message in the format expected from the Unified Push Server.
 * The message format is very simple: A generic JSON map is used to sent messages to Android and iOS devices.
 * The applications on the devices will receive the JSON map and are responsible for performing a lookup to read values of the given keys.
 * See the {@link <a href="http://www.aerogear.org/docs/specs/aerogear-push-messages/">Message Specification</a>} for more information.
 * <p>
 * To construct a message use the {@link Builder} like this :
 * <pre>
 * {@code
 * // For 'broadcast' messages
 * UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
 *       .pushApplicationId("c7fc6525-5506-4ca9-9cf1-55cc261ddb9c")
 *       .masterSecret("8b2f43a9-23c8-44fe-bee9-d6b0af9e316b")
 *       .alert("Hello")
 *       .sound("default")
 *       .badge("welcome")
 *       .build();
 *
 * // For 'selective' messages
 *  UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
 *       .pushApplicationId("c7fc6525-5506-4ca9-9cf1-55cc261ddb9c")
 *       .masterSecret("8b2f43a9-23c8-44fe-bee9-d6b0af9e316b")
 *       .alert("Hello")
 *       .sound("default")
 *       .variants(Arrays.asList("HR_Premium"))
 *       .aliases(Arrays.asList("mike", "john"))
 *       .deviceType(Arrays.asList("iPad","AndroidTablet"))
 *       .build();
 * }
 * </pre>
 */
public class UnifiedMessage {

    private String pushApplicationId;

    private String masterSecret;

    private List<String> variants;

    private List<String> aliases;

    private Map<String, Object> attributes;

    private String category;

    private List<String> deviceType;

    /**
     * A builder to provide a Fluid API
     */
    public static class Builder {

        private String pushApplicationId;

        private String masterSecret;

        private String category;

        private List<String> deviceType = new ArrayList<String>();

        private List<String> variants = new ArrayList<String>();

        private List<String> aliases = new ArrayList<String>();

        private Map<String, Object> attributes = new HashMap<String, Object>();

        private final String alert = "alert";
        private final String sound = "sound";
        private final String badge = "badge";
        private final String simplePush = "simple-push";

        /**
         * Specifies which Push Application the message is for.
         *
         * @param pushApplicationId The pushApplicationID
         * @return the current {@link Builder} instance
         */
        public Builder pushApplicationId(String pushApplicationId) {
            this.pushApplicationId = pushApplicationId;
            return this;
        }

        /**
         * Set the masterSecret used to authenticate against the Push Server
         *
         * @param masterSecret The masterSecret
         * @return the current {@link Builder} instance
         */
        public Builder masterSecret(String masterSecret) {
            this.masterSecret = masterSecret;
            return this;
        }

        /**
         * Sets a list of "identifiers", like username or email address.
         *
         * @param aliases a list of "identifiers", like username or email address.
         * @return the current {@link Builder} instance
         */
        public Builder aliases(List<String> aliases) {
            this.aliases = aliases;
            return this;
        }

        /**
         * A filter for notifying only specific mobile variants of the Push Application
         *
         * @param variants a list of mobile variants"
         * @return the current {@link Builder} instance
         */
        public Builder variants(List<String> variants) {
            this.variants = variants;
            return this;
        }

        /**
         * A category is a semantical tag.
         *
         * @param category a semantical tag
         * @return the current {@link Builder} instance
         */
        public Builder category(String category) {
            this.category = category;
            return this;
        }

        /**
         * A filter for notifying only users running a certain device.
         *
         * @param deviceType a list of devices i.e ["iPad","iPhone"]
         * @return the current {@link Builder} instance
         */
        public Builder deviceType(List<String> deviceType) {
            this.deviceType = deviceType;
            return this;
        }

        /**
         * A map containing various key-value pairs, that represent application
         * specific values. The mobile application is asked to look for those keys.
         *
         * @param attributes map containing several key-value pairs
         * @return the current {@link Builder} instance
         */
        public Builder attributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        /**
         * Adds an application specific value for the given key.
         *
         * @param key of an application specific entry
         * @param value of an application specific entry
         * @return the current {@link Builder} instance
         */
        public Builder attribute(String key, String value) {
            this.attributes.put(key, value);
            return this;
        }

        /**
         * Triggers a dialog, displaying the value.
         *
         * @param message that will be displayed on the alert UI element
         * @return the current {@link Builder} instance
         */
        public Builder alert(String message) {
            this.attributes.put(alert, message);
            return this;
        }

        /**
         * Plays a given sound - On iOS no API needs to be invoked to play the sound file.
         * However on other platforms custom API call may be required.
         *
         * @param sound i.e name of the sound file
         * @return the current {@link Builder} instance
         */
        public Builder sound(String sound) {
            this.attributes.put(this.sound, sound);
            return this;
        }

        /**
         * Sets the value of the badge icon - no iOS API needs to be invoked by the app developer.
         *
         * @param badge i.e file name of the icon
         * @return the current {@link Builder} instance
         */
        public Builder badge(String badge) {
            this.attributes.put(this.badge, badge);
            return this;
        }

        /**
         * Needed when broadcasting a message to a SimplePush Network
         * Note: Do not use this method for a "selective send".
         *
         * @param version to pass to the broadcast channel, i.e "version=5"
         * @return the current {@link Builder} instance
         */
        public Builder simplePush(String version) {
            this.attributes.put(simplePush, fixVersion(version));
            return this;
        }

        /**
         * Needed when doing a selective send to a SimplePush Network
         * Note: Do not use this method for a "broadcast send".
         *
         * @param entries representing a key:value where key is an alias (category) of the channel and value some version (i.e "version=5")
         * @return the current {@link Builder} instance
         */
        public Builder simplePush(Map<String, String> entries) {
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                entry.setValue(fixVersion(entry.getValue()));
            }
            this.attributes.put(simplePush, entries);
            return this;
        }

        public UnifiedMessage build() {
            return new UnifiedMessage(this);
        }

        private String fixVersion(String version) {
            if (version != null && !version.startsWith("version=")) {
                version = "version=" + version;
            }
            return version;
        }

    }

    /**
     * private constructor as UnifiedMessage can only be created through the Builder
     * @param builder
     */
    private UnifiedMessage(Builder builder) {
        this.attributes = builder.attributes;
        this.aliases = builder.aliases;
        this.variants = builder.variants;
        this.category = builder.category;
        this.deviceType = builder.deviceType;
        this.pushApplicationId = builder.pushApplicationId;
        this.masterSecret = builder.masterSecret;
    }

    public String getPushApplicationId() {
        return pushApplicationId;
    }

    public void setPushApplicationId(String pushApplicationId) {
        this.pushApplicationId = pushApplicationId;
    }

    public String getMasterSecret() {
        return masterSecret;
    }

    public void setMasterSecret(String masterSecret) {
        this.masterSecret = masterSecret;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(List<String> deviceType) {
        this.deviceType = deviceType;
    }
}
