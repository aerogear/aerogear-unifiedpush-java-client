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

import java.util.*;

/**
 * A UnifiedMessage represents a message in the format expected from the Unified Push Server.
 * The message format is very simple: A generic JSON map is used to sent messages to Android and iOS devices.
 * The applications on the devices will receive the JSON map and are responsible for performing a lookup to read values of the given keys.
 * See the <a href="http://www.aerogear.org/docs/specs/aerogear-push-messages/">Message Specification</a> for more information.
 * <p>
 * To construct a message use the {@link Builder} like this :
 * <pre>
 * {@code
 * // Sending an UnifiedMessage
 *  UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
 *       .pushApplicationId("c7fc6525-5506-4ca9-9cf1-55cc261ddb9c")
 *       .masterSecret("8b2f43a9-23c8-44fe-bee9-d6b0af9e316b")
 *       .alert("Hello")
 *       .sound("default")
 *       .variants(Arrays.asList("c3f0a94f-48de-4b77-a08e-68114460857e")) //e.g. HR_Premium
 *       .aliases(Arrays.asList("mike", "john"))
 *       .categories(Arrays.asList("sport","world cup"))
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

    private Set<String> categories;

    private List<String> deviceType;

    private String simplePush;

    /**
     * A builder to provide a fluent API
     */
    public static class Builder {

        private String pushApplicationId;

        private String masterSecret;

        private Set<String> categories = new HashSet<String>();

        private String simplePush;

        private List<String> deviceType = new ArrayList<String>();

        private List<String> variants = new ArrayList<String>();

        private List<String> aliases = new ArrayList<String>();

        private Map<String, Object> attributes = new HashMap<String, Object>();

        private final String alert = "alert";
        private final String sound = "sound";
        private final String badge = "badge";
        private final String ttl = "ttl";
        private final String contentAvailable = "content-available";

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
         * Set the masterSecret used to authenticate against the Push Server.
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
         * @param aliases a list of "identifiers", like username or email address
         * @return the current {@link Builder} instance
         */
        public Builder aliases(List<String> aliases) {
            this.aliases = aliases;
            return this;
        }

        /**
         * A filter for notifying only specific mobile variants of the Push Application.
         *
         * @param variants a list of mobile variants ids
         * @return the current {@link Builder} instance
         */
        public Builder variants(List<String> variants) {
            this.variants = variants;
            return this;
        }

        /**
         * A list of categories. A Category is a semantical tag.
         *
         * @param  set of categories
         * @return the current {@link Builder} instance
         */
        public Builder categories(Set categories) {
            this.categories = categories;
            return this;
        }

        /**
         * A list of categories. A Category is a semantical tag.
         *
         * @param  a list of categories
         * @return the current {@link Builder} instance
         */
        public Builder categories(String... categories) {
            this.categories = new HashSet<String>(Arrays.asList(categories));
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
            this.attributes.put(this.badge, Integer.parseInt(badge));
            return this;
        }

        /**
         * An iOS specific argument to mark the payload as 'content-available'. The feature is
         * needed when sending notifications to Newsstand applications and submitting silent iOS notifications (iOS7)
         *
         * @return the current {@link Builder} instance
         */
        public Builder contentAvailable() {
            this.attributes.put(this.contentAvailable, true);
            return this;
        }

        /**
         * Needed when sending a message to a SimplePush Network
         *
         * @param version to pass to the broadcast channel, i.e "version=5"
         * @return the current {@link Builder} instance
         */
        public Builder simplePush(String version) {
            this.simplePush = fixVersion(version);
            return this;
        }

        /**
         * Specify the Time To Live of the message, used by the APNs/GCM Push Networks.
         * If the device is offline for a longer time than the ttl value, the supported Push Networks may not deliver the message to the client.
         *
         * @param seconds , the amount of seconds of the Time To Live
         * @return the current {@link Builder} instance
         */
        public Builder timeToLive(int seconds) {
            this.attributes.put(this.ttl, seconds);
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
     * private constructor as UnifiedMessage can only be created through the Builder.
     *
     * @param builder The builder object that would be used to construct the UnifiedMessage
     */
    private UnifiedMessage(Builder builder) {
        this.attributes = builder.attributes;
        this.aliases = builder.aliases;
        this.variants = builder.variants;
        this.categories = builder.categories;
        this.deviceType = builder.deviceType;
        this.pushApplicationId = builder.pushApplicationId;
        this.masterSecret = builder.masterSecret;
        this.simplePush = builder.simplePush;
    }

    /**
     * Get the push Application Id.
     *
     * @return the push Application Id
     */
    public String getPushApplicationId() {
        return pushApplicationId;
    }

    /**
     * Get the masterSecret used to authenticate against the Push Server.
     *
     * @return the master Secret
     */
    public String getMasterSecret() {
        return masterSecret;
    }

    /**
     * Get a list of "identifiers", like username or email address.
     *
     * @return a list of "identifiers", like username or email address
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * Get A filter for notifying only specific mobile variants of the Push Application.
     *
     * @return A filter for notifying only specific mobile variants of the Push Application
     */
    public List<String> getVariants() {
        return variants;
    }

    /**
     * Get a map containing various key-value pairs, that represent application
     * specific values. The mobile application is asked to look for those keys.
     *
     * @return A map containing various key-value pairs, that represent application
     *         specific values. The mobile application is asked to look for those keys
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Get a category list, a category is a semantical tag.
     *
     * @return the category list
     */
    public Set getCategories() {
        return categories;
    }

    /**
     * Get a filter for notifying only users running a certain device.
     *
     * @return a filter for notifying only users running a certain device
     */
    public List<String> getDeviceType() {
        return deviceType;
    }

    /**
     * Get the key-value pair represented by a String and
     * used by the Simple Push Networks.
     *
     * @return a String in the form of "version=5"
     */
    public String getSimplePush() {
        return simplePush;
    }
}
