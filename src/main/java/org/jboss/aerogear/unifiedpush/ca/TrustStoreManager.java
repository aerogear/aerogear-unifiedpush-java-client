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
package org.jboss.aerogear.unifiedpush.ca;

import java.security.KeyStore;

public interface TrustStoreManager {

    /**
     * Loads the TrustStore file given its path, type and password.
     * 
     * @param trustStorePath The trustStore's path.
     * @param trustStoreType The trustStore's type.
     * @param trustStorePassword The trustStore's password.
     * @throws Exception
     */
    KeyStore loadTrustStore(String trustStorePath, String trustStoreType, String trustStorePassword) throws Exception;
}
