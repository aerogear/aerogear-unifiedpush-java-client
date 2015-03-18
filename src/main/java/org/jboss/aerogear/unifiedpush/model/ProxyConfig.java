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

import java.net.Proxy;

public class ProxyConfig {

    private String proxyHost;

    private int proxyPort;

    private String proxyUser;

    private String proxyPassword;

    private Proxy.Type proxyType;

    public ProxyConfig() {

    }

    public ProxyConfig(Proxy.Type proxyType) {
        this.proxyType = proxyType;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public void setProxyType(Proxy.Type proxyType) {
        this.proxyType = proxyType;
    }

    /**
     * Get the proxy Hostname that is configured.
     * 
     * @return A proxy hostname
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * Get the proxy port.
     * 
     * @return A proxy port
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * Get the specified proxy user.
     * 
     * @return Proxy username
     */
    public String getProxyUser() {
        return proxyUser;
    }

    /**
     * Get the password for proxy user.
     * 
     * @return proxy user password
     */
    public String getProxyPassword() {
        return proxyPassword;
    }

    /**
     * Get the proxy type that is used in proxy connection.
     * 
     * @return A {@link Proxy Type}
     */
    public Proxy.Type getProxyType() {
        return proxyType;
    }
}
