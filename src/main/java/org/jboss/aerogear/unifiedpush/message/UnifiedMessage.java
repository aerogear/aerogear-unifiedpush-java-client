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


public class UnifiedMessage {

    private String pushApplicationId;

    private String masterSecret;

    private List<String> identifiers;

    private Map<String, Object> attributes;

    public static class Builder {

        private String pushApplicationId;

        private String masterSecret;

        private List<String> identifiers = new ArrayList<String>();

        private Map<String, Object> attributes = new HashMap<String, Object>();

        public Builder pushApplicationId(String pushApplicationId) {
            this.pushApplicationId = pushApplicationId;
            return this;
        }

        public Builder masterSecret(String masterSecret) {
            this.masterSecret = masterSecret;
            return this;
        }

        public Builder identifiers(List<String> identifiers) {
            this.identifiers = identifiers;
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

    private UnifiedMessage(Builder builder) {
        this.attributes  = builder.attributes;
        this.identifiers = builder.identifiers;
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

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
