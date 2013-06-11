/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aerogear.unifiedpush;

import java.util.List;
import java.util.Map;

/**
 * Interface to encapsulate the connection library details
 *
 */
public interface Client {
    /**
     * Setup or reset the connection
     * @param url
     */
    void initialize(String url);

    /**
     * Mainly used for broadcast messages
     * @param payload
     * @param url
     */
    void post(Map<String, ? extends Object>  payload, String url);

    /**
     * Mainly used for selective messages
     * @param payload
     * @param clientIdentifiers
     * @param url
     */
    void post(Map<String, ? extends Object>  payload, List<String> clientIdentifiers, String url);
}
