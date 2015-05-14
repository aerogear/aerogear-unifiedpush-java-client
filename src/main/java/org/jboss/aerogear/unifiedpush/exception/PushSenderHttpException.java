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
package org.jboss.aerogear.unifiedpush.exception;

/**
 * Thrown when sending the push delivery request fails due to invalid credentials or Push Server error.
 */
public class PushSenderHttpException extends PushSenderException {

    static final long serialVersionUID = -234897190745766939L;

    private int statusCode = -1;

    /**
     * Constructs a new push sender runtime exception with the given http status code.
     * @param statusCode
     */
    public PushSenderHttpException(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * If present, returns the error status code from the Unified Push server
     * @return if present, the status code, otherwise -1
     */
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int httpErrorStatusCode) {
        this.statusCode = httpErrorStatusCode;
    }

}
