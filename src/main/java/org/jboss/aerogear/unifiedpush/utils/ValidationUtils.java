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
package org.jboss.aerogear.unifiedpush.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Utility class offering methods for validating instances.
 * 
 */
public final class ValidationUtils {

    private ValidationUtils() {
    }

    /**
     * Checks if the given {@link Collection} is null or empty.
     * 
     * @param c the collection to be checked
     * @return true if null or empty and false otherwise
     */
    public static <E> boolean isEmpty(Collection<E> c) {
        return c == null || c.isEmpty();
    }

    /**
     * Checks if the given {@link Map} is null or empty.
     * 
     * @param map the map to be checked
     * @return true if null or empty and false otherwise
     */
    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Checks if the given {@link String} is null or empty.
     * 
     * @param str the string to be checked
     * @return true if null or empty and false otherwise
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    /**
     * Checks if the given http status code is in the 2xx range
     * 
     * @param statusCode the int containing the status code
     * @return true if in the range otherwise false
     */
    public static boolean isSuccess(int statusCode) {
        return statusCode != 0 && (statusCode >= 200 && statusCode < 300);
    }

}
