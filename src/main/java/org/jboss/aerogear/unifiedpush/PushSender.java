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
package org.jboss.aerogear.unifiedpush;

import org.jboss.aerogear.unifiedpush.message.MessageResponseCallback;
import org.jboss.aerogear.unifiedpush.message.UnifiedMessage;
import org.jboss.aerogear.unifiedpush.model.ProxyConfig;
import org.jboss.aerogear.unifiedpush.model.TrustStoreConfig;

public interface PushSender {

    /**
     * Sends the given payload to installations of the referenced PushApplication.
     * We also pass a {@link MessageResponseCallback} to handle the message
     * 
     * @param unifiedMessage the {@link UnifiedMessage} to send.
     * @param callback the {@link MessageResponseCallback}.
     */
    void send(UnifiedMessage unifiedMessage, MessageResponseCallback callback);

    /**
     * Sends the given payload to installations of the referenced PushApplication.
     * 
     * @param unifiedMessage The {@link UnifiedMessage} to send.
     */
    void send(UnifiedMessage unifiedMessage);

    /**
     * Returns the current configured server URL
     * 
     * @return the current configured server URL.
     */
    String getServerURL();

    /**
     * Get the proxy cofniguration.
     *
     * @return {@link ProxyConfig}
     */
    ProxyConfig getProxy();


    /**
     * Get the custom trustStore configuration;
     *
     * @return {@link TrustStoreConfig}
     */
    TrustStoreConfig getCustomTrustStore();

    /**
     * Get the used pushApplicationId.
     *
     * @return pushApplicationId that is used
     */
    String getPushApplicationId();

    /**
     * Get the used masterSecret.
     *
     * @return masterSecret that is used
     */
    String getMasterSecret();
}
