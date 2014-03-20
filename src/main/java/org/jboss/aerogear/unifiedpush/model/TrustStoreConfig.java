/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.unifiedpush.model;

public class TrustStoreConfig {

    private String trustStorePath;

    private String trustStoreType;

    private String trustStorePassword;

    public TrustStoreConfig() {

    }

    public TrustStoreConfig(String trustStorePath, String trustStoreType, String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
        this.trustStorePath = trustStorePath;
        this.trustStoreType = trustStoreType;
    }

    /**
     * Get the path for a truststore to be used.
     * 
     * @return the custom truststore's file path
     */
    public String getTrustStorePath() {
        return trustStorePath;
    }

    /**
     * Set the path for a TrustStore to be used.
     * 
     * @param trustStorePath The path for the TrustStore.
     */
    public void setTrustStorePath(String trustStorePath) {
        this.trustStorePath = trustStorePath;
    }

    /**
     * Get the password for the TrustStore.
     * 
     * @return The TrustStore's password
     */
    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    /**
     * Set the password for the TrustStore.
     * 
     * @param trustStorePassword The password for the TrustStore.
     */
    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    /**
     * Get the type for the TrustStore.
     * 
     * @return The TrustStore's type
     */
    public String getTrustStoreType() {
        return trustStoreType;
    }

    /**
     * Set the type for the TrustStore.
     * 
     * @param trustStoreType The type for the TrustStore.
     */
    public void setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }
}
