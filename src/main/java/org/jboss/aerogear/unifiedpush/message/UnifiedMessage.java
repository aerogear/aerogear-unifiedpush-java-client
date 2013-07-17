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
 * an UnifiedMessage represents a message in the format expected from the Unified Push Server
 * The message format is very simple: A generic JSON map is used to sent messages to Android and iOS devices.
 * The applications on the devices will receive the JSON map and are responsible for performing a lookup to read values of the given keys.
 * @see <a href="http://www.aerogear.org/docs/specs/aerogear-push-messages/">http://www.aerogear.org/docs/specs/aerogear-push-messages/</a>
 */
public class UnifiedMessage {

    private String pushApplicationId;

    private String masterSecret;

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

        private List<String> aliases = new ArrayList<String>();

        private Map<String, Object> attributes = new HashMap<String, Object>();

        public Builder pushApplicationId(String pushApplicationId) {
            this.pushApplicationId = pushApplicationId;
            return this;
        }

        public Builder masterSecret(String masterSecret) {
            this.masterSecret = masterSecret;
            return this;
        }

        public Builder aliases(List<String> aliases) {
            this.aliases = aliases;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder deviceType(List<String> deviceType) {
            this.deviceType = deviceType;
            return this;
        }

        public Builder attributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder attribute(String key, String value) {
            this.attributes.put(key,value);
            return this;
        }

        public Builder alert(String message) {
            this.attributes.put("alert",message);
            return this;
        }

        public Builder sound(String sound) {
            this.attributes.put("sound",sound);
            return this;
        }

        public UnifiedMessage build() {
            return new UnifiedMessage(this);
        }


    }

    /**
     * private constructor as UnifiedMessage can only be created through the Builder
     * @param builder
     */
    private UnifiedMessage(Builder builder) {
        this.attributes  = builder.attributes;
        this.aliases = builder.aliases;
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
