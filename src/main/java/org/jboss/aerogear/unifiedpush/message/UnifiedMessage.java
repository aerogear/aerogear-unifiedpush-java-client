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
 * 
 * <pre>
 * {
 *     &#064;code
 *     // Sending an UnifiedMessage
 *     UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
 *             .message()
 *              .alert(&quot;Hello&quot;)
 *              .sound(&quot;default&quot;)
 *              .build()
 *             .criteria()
 *              .variants(Arrays.asList(&quot;c3f0a94f-48de-4b77-a08e-68114460857e&quot;)) // e.g. HR_Premium
 *              .aliases(Arrays.asList(&quot;mike&quot;, &quot;john&quot;))
 *              .categories(Arrays.asList(&quot;sport&quot;, &quot;world cup&quot;))
 *              .deviceType(Arrays.asList(&quot;iPad&quot;, &quot;AndroidTablet&quot;))
 *              .build()
 *             .build();
 * }
 * </pre>
 */
public class UnifiedMessage {

    private MessageBuilder message;

    private CriteriaBuilder criteria;

    private ConfigBuilder config;

    public static MessageBuilder withMessage() {
        return new Builder().message();
    }

    public static CriteriaBuilder withCriteria() {
        return new Builder().criteria();
    }

    public static ConfigBuilder withConfig() {
        return new Builder().config();
    }

    /**
     * A builder to provide a fluent API
     *
     * @deprecated Please use one of the static methods in {@link UnifiedMessage}
     * @see UnifiedMessage#withMessage()
     * @see UnifiedMessage#withCriteria()
     * @see UnifiedMessage#withConfig()
     */
    public static class Builder {

        private MessageBuilder messageBuilder;
        private CriteriaBuilder criteriaBuilder;
        private ConfigBuilder configBuilder;

        /**
         * 
         * Returns the criteria builder
         * 
         * @return the current {@link CriteriaBuilder} instance
         */
        public CriteriaBuilder criteria() {
            if (criteriaBuilder == null) {
                criteriaBuilder = new CriteriaBuilder(this);
            }
            return criteriaBuilder;
        }

        /**
         * 
         * Returns the message builder
         * 
         * @return the current {@link MessageBuilder} instance
         */
        public MessageBuilder message() {
            if (messageBuilder == null) {
                messageBuilder = new MessageBuilder(this);
            }
            return messageBuilder;
        }

        /**
         * 
         * Returns the config builder
         * 
         * @return the current {@link ConfigBuilder} instance
         */
        public ConfigBuilder config() {
            if (configBuilder == null) {
                configBuilder = new ConfigBuilder(this);
            }
            return configBuilder;
        }

        public UnifiedMessage build() {
            return new UnifiedMessage(this);
        }

    }

    public static class CriteriaBuilder {

        public CriteriaBuilder(Builder builder) {
            this.builder = builder;
        }

        private final Builder builder;

        private final String aliases = "alias";
        private final String variants = "variants";
        private final String categories = "categories";
        private final String deviceType = "deviceType";

        private Map<String, Object> attributes = new HashMap<String, Object>();

        /**
         * Sets a list of "identifiers", like username or email address.
         * 
         * @param aliases a list of "identifiers", like username or email address
         * @return the current {@link CriteriaBuilder} instance
         */
        public CriteriaBuilder aliases(List<String> aliases) {
            attributes.put(this.aliases, aliases);
            return this;
        }

        /**
         * A filter for notifying only specific mobile variants of the Push Application.
         * 
         * @param variants a list of mobile variants ids
         * @return the current {@link CriteriaBuilder} instance
         */
        public CriteriaBuilder variants(List<String> variants) {
            attributes.put(this.variants, variants);
            return this;
        }

        /**
         * A list of categories. A Category is a semantical tag.
         * 
         * @param categories a set of categories
         * @return the current {@link CriteriaBuilder} instance
         */
        public CriteriaBuilder categories(Set<String> categories) {
            attributes.put(this.categories, categories);
            return this;
        }

        /**
         * A list of categories. A Category is a semantical tag.
         * 
         * @param categories a list of categories
         * @return the current {@link CriteriaBuilder} instance
         */
        public CriteriaBuilder categories(String... categories) {
            attributes.put(this.categories, new HashSet<String>(Arrays.asList(categories)));
            return this;
        }

        /**
         * A filter for notifying only users running a certain device.
         * 
         * @param deviceType a list of devices i.e ["iPad","iPhone"]
         * @return the current {@link Builder} instance
         */
        public CriteriaBuilder deviceType(List<String> deviceType) {
            attributes.put(this.deviceType, deviceType);
            return this;
        }

        public MessageBuilder message() {
            if (builder.messageBuilder == null) {
                builder.messageBuilder = new MessageBuilder(builder);
            }
            return builder.messageBuilder;
        }

        public ConfigBuilder config() {
            if (builder.configBuilder == null) {
                builder.configBuilder = new ConfigBuilder(builder);
            }
            return builder.configBuilder;
        }

        public UnifiedMessage build() {
            return builder.build();
        }

        public Map getAttributes() {
            return attributes;
        }
    }

    public static class MessageBuilder {

        public MessageBuilder(Builder builder) {
            this.builder = builder;
        }

        private final Builder builder;
        private final String alert = "alert";
        private final String sound = "sound";
        private final String badge = "badge";
        private final String contentAvailable = "content-available";
        private final String actionCategory = "action-category";
        private final String userData = "user-data";
        private final String simplePush = "simple-push";

        private Map<String, Object> attributes = new HashMap<String, Object>();

        private Map<String, Object> userDataAttributes = new HashMap<String, Object>();

        /**
         * Triggers a dialog, displaying the value.
         * 
         * @param message that will be displayed on the alert UI element
         * @return the current {@link MessageBuilder} instance
         */
        public MessageBuilder alert(String message) {
            this.attributes.put(alert, message);
            return this;
        }

        /**
         * Plays a given sound - On iOS no API needs to be invoked to play the sound file.
         * However on other platforms custom API call may be required.
         * 
         * @param sound i.e name of the sound file
         * @return the current {@link MessageBuilder} instance
         */
        public MessageBuilder sound(String sound) {
            this.attributes.put(this.sound, sound);
            return this;
        }

        /**
         * Sets the value of the badge icon - no iOS API needs to be invoked by the app developer.
         * 
         * @param badge i.e file name of the icon
         * @return the current {@link MessageBuilder} instance
         */
        public MessageBuilder badge(String badge) {
            this.attributes.put(this.badge, Integer.parseInt(badge));
            return this;
        }

        /**
         * An iOS specific argument to mark the payload as 'content-available'. The feature is
         * needed when sending notifications to Newsstand applications and submitting silent iOS notifications (iOS7)
         * 
         * @return the current {@link MessageBuilder} instance
         */
        public MessageBuilder contentAvailable() {
            this.attributes.put(this.contentAvailable, true);
            return this;
        }

        /**
         * An iOS specific argument to pass an Action Category for interaction notifications ( iOS8)
         * 
         * @param actionCategory , the identifier of the action category for the interactive notification
         * @return the current {@link MessageBuilder} instance
         */
        public MessageBuilder actionCategory(String actionCategory) {
            this.attributes.put(this.actionCategory, actionCategory);
            return this;
        }

        /**
         * Needed when sending a message to a SimplePush Network
         * 
         * @param version to pass to the broadcast channel, i.e "version=5"
         * @return the current {@link MessageBuilder} instance
         */
        public MessageBuilder simplePush(String version) {
            this.attributes.put(this.simplePush, fixVersion(version));
            return this;
        }

        /**
         * Adds an custom value for the given key. This is used to pass user data to
         * the UnifiedPush Server
         *
         * @param key of an user data entry
         * @param value of an user data entry
         * @return  the current {@link MessageBuilder} instance
         */
        public MessageBuilder userData(String key, String value) {
            this.userDataAttributes.put(key, value);
            return this;
        }

        /**
         * Adds a map containing custom key/value entries. This is used to pass user data to
         * the UnifiedPush Server
         * @param userDataMap containing custom key/value entries
         * @return the current {@link MessageBuilder} instance
         */
        public MessageBuilder userData(Map<String, Object> userDataMap) {
            this.userDataAttributes = userDataMap;
            return this;
        }

        public CriteriaBuilder criteria() {
            if (builder.criteriaBuilder == null) {
                builder.criteriaBuilder = new CriteriaBuilder(builder);
            }
            return builder.criteriaBuilder;
        }

        public ConfigBuilder config() {
            if (builder.configBuilder == null) {
                builder.configBuilder = new ConfigBuilder(builder);
            }
            return builder.configBuilder;
        }

        private String fixVersion(String version) {
            if (version != null && !version.startsWith("version=")) {
                version = "version=" + version;
            }
            return version;
        }

        public UnifiedMessage build() {
            return builder.build();
        }

        public Map getAttributes() {
            attributes.put(this.userData, userDataAttributes);
            return attributes;
        }

    }

    public static class ConfigBuilder {

        private final Builder builder;

        private Map<String, Object> attributes = new HashMap<String, Object>();

        private final String timeToLive = "ttl";

        public ConfigBuilder(Builder builder) {
            this.builder = builder;
        }

        /**
         * Specify the Time To Live of the message, used by the APNs/GCM Push Networks.
         * If the device is offline for a longer time than the ttl value, the supported Push Networks may not deliver the message to the client.
         * 
         * @param seconds , the amount of seconds of the Time To Live
         * @return the current {@link ConfigBuilder} instance
         */
        public ConfigBuilder timeToLive(int seconds) {
            attributes.put(this.timeToLive, seconds);
            return this;
        }

        public Map getAttributes() {
            return attributes;
        }

        public MessageBuilder message() {
            if (builder.messageBuilder == null) {
                builder.messageBuilder = new MessageBuilder(builder);

            }
            return builder.messageBuilder;
        }

        public CriteriaBuilder criteria() {
            if (builder.criteriaBuilder == null) {
                builder.criteriaBuilder = new CriteriaBuilder(builder);
            }
            return builder.criteriaBuilder;
        }

        public UnifiedMessage build() {
            return builder.build();
        }

    }

    /**
     * private constructor as UnifiedMessage can only be created through the Builder.
     * 
     * @param builder The builder object that would be used to construct the UnifiedMessage
     */
    private UnifiedMessage(Builder builder) {
        this.criteria = builder.criteriaBuilder;
        this.config = builder.configBuilder;
        this.message = builder.messageBuilder;
    }

    public MessageBuilder getMessage() {
        return message;
    }

    public CriteriaBuilder getCriteria() {
        return criteria;

    }

    public ConfigBuilder getConfig() {
        return config;
    }
}
