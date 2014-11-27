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

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class PushConfiguration {

    private static final Logger logger = Logger.getLogger(PushConfiguration.class.getName());
    private String serverUrl;
    private String pushApplicationId;
    private String masterSecret;

    public PushConfiguration(){
    }

    public String getMasterSecret() {
        return masterSecret;
    }

    public void setMasterSecret(String masterSecret) {
        this.masterSecret = masterSecret;
    }

    public String getPushApplicationId() {
        return pushApplicationId;
    }

    public void setPushApplicationId(String pushApplicationId) {
        this.pushApplicationId = pushApplicationId;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public static PushConfiguration read(String location) throws IOException {
        BufferedReader bufferedReader = null;
        PushConfiguration pushConfiguration = null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(location)));
            Gson gson = new Gson();
            pushConfiguration =  gson.fromJson(bufferedReader, PushConfiguration.class);
        }
       finally {
           if (bufferedReader != null) {
               bufferedReader.close();
           }
        }
        return pushConfiguration;
    }
}