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

/**
 * A simple Callback interface used when sending {@link UnifiedMessage}
 *
 */
public interface MessageResponseCallback {

    /**
     * Will be called if the response status code is in the range of 2xx
     *
     * @param statusCode
     */
    void success(int statusCode);

    /**
     * Will be called whatever the response status code is. It's the developer
     * responsability to implement the status code handling.
     *
     * @param statusCode
     */
    void complete(int statusCode);

    /**
     * Will be called if an Exception occurs (i.e : {@link java.io.IOException} )
     *
     * @param throwable
     */
    void failure(Throwable throwable);

}
