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

import org.jboss.aerogear.unifiedpush.PushSenderException;

/**
 * A simple Callback interface used when sending {@link UnifiedMessage}
 */
public interface MessageResponseCallback {

    /**
     * Will be called if response from server is successful.
     *
     * @param statusCode the status code as returned by the server.
     */
    void onComplete(int statusCode);

    /**
     * Will be called if an Exception occurs (i.e : {@link java.io.IOException} )
     *
     * {@link PushSenderException} is passed as a parameter when returned statusCode is erroneous.
     *
     * @param throwable contains failure details.
     */
    void onError(Throwable throwable);

}
