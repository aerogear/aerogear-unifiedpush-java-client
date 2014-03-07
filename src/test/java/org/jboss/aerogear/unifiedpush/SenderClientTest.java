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
package org.jboss.aerogear.unifiedpush;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.aerogear.unifiedpush.message.MessageResponseCallback;
import org.jboss.aerogear.unifiedpush.message.UnifiedMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SenderClient.class)
public class SenderClientTest {

    /* -- testing data -- */
    private static final String PUSH_APPLICATION_ID = "c7fc6525-5506-4ca9-9cf1-55cc261ddb9c";
    private static final String MASTER_SECRET = "8b2f43a9-23c8-44fe-bee9-d6b0af9e316b";
    private static final String ALERT_MSG = "Hello from Java Sender API, via JUnit";
    private static final String DEFAULT_SOUND = "default";
    private static final List<String> IDENTIFIERS_LIST = new ArrayList<String>();

    static {
        IDENTIFIERS_LIST.add("mwessendorf2");
    }

    /* -- mocks -- */
    private SenderClient defaultSenderClient;
    private HttpURLConnection connection;

    @Before
    public void setup() throws Exception {
        // mock output stream
        OutputStream out = PowerMockito.mock(OutputStream.class);
        PowerMockito.doNothing().when(out).write(any(byte[].class));
        // mock connection
        this.setConnection(PowerMockito.mock(HttpURLConnection.class));
        when(connection.getOutputStream()).thenReturn(out);
        // mock private getConnection method
        this.setDefaultSenderClient(PowerMockito.spy(new SenderClient("http://aerogear.example.com/ag-push")));
        PowerMockito.doReturn(connection).when(defaultSenderClient, "getConnection", anyString());
    }

    @Test
    public void sendSendWithCallback404() throws IOException, InterruptedException {
        // return 404
        int STATUS_NOT_FOUND = 404;

        when(this.getConnnection().getResponseCode()).thenReturn(STATUS_NOT_FOUND);

        final CountDownLatch latch = new CountDownLatch(1);
        final List<Integer> returnedStatusList = new ArrayList<Integer>(1);
        final AtomicBoolean onFailCalled = new AtomicBoolean(false);

        MessageResponseCallback callback = new MessageResponseCallback() {
            @Override
            public void onComplete(int statusCode) {
                returnedStatusList.add(statusCode);
                latch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                onFailCalled.set(true);
                latch.countDown();
            }
        };

        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                            .pushApplicationId(PUSH_APPLICATION_ID)
                            .masterSecret(MASTER_SECRET)
                            .alert(ALERT_MSG)
                            .sound(DEFAULT_SOUND)
                            .aliases(IDENTIFIERS_LIST)
                            .build();

        defaultSenderClient.send(unifiedMessage, callback);

        latch.await(1000, TimeUnit.MILLISECONDS);
        // onError callback should not be called
        assertFalse(onFailCalled.get());
        assertNotNull(returnedStatusList);
        assertTrue(returnedStatusList.size() == 1);
        assertEquals(STATUS_NOT_FOUND, returnedStatusList.get(0).intValue());
    }

    @Test
    public void sendSendWithCallbackAndException() throws Exception {
        // throw IOException when posting
        PowerMockito.doThrow(new IOException()).when(defaultSenderClient, "post", anyString(), anyString(), anyString());

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean onFailCalled = new AtomicBoolean(false);
        final AtomicReference<Throwable> exceptionReference = new AtomicReference<Throwable>();

        MessageResponseCallback callback = new MessageResponseCallback() {
            @Override
            public void onComplete(int statusCode) {
                latch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                onFailCalled.set(true);
                exceptionReference.set(throwable);
                latch.countDown();
            }
        };

        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                        .pushApplicationId(PUSH_APPLICATION_ID)
                        .masterSecret(MASTER_SECRET)
                        .alert(ALERT_MSG)
                        .sound(DEFAULT_SOUND)
                        .aliases(IDENTIFIERS_LIST)
                        .build();

        defaultSenderClient.send(unifiedMessage, callback);

        latch.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(onFailCalled.get());
        assertEquals(IOException.class, exceptionReference.get().getClass());

    }

    @Test
    public void sendSendWithCallback200() throws IOException, InterruptedException {
        // return 200
        int STATUS_OK = 200;

        when(this.getConnnection().getResponseCode()).thenReturn(STATUS_OK);

        final CountDownLatch latch = new CountDownLatch(1);
        final List<Integer> returnedStatusList = new ArrayList<Integer>(1);
        final AtomicBoolean onFailCalled = new AtomicBoolean(false);

        MessageResponseCallback callback = new MessageResponseCallback() {
            @Override
            public void onComplete(int statusCode) {
                returnedStatusList.add(statusCode);
                latch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                onFailCalled.set(true);
                latch.countDown();
            }
        };

        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                            .pushApplicationId(PUSH_APPLICATION_ID)
                            .masterSecret(MASTER_SECRET)
                            .alert(ALERT_MSG)
                            .sound(DEFAULT_SOUND)
                            .aliases(IDENTIFIERS_LIST)
                            .build();

        defaultSenderClient.send(unifiedMessage, callback);

        latch.await(1000, TimeUnit.MILLISECONDS);
        // onError callback should not be called
        assertFalse(onFailCalled.get());
        assertNotNull(returnedStatusList);
        assertTrue(returnedStatusList.size() == 1);
        assertEquals(STATUS_OK, returnedStatusList.get(0).intValue());
    }

    @Test(expected = IllegalStateException.class)
    public void emptyServerURL() throws IOException, InterruptedException {
        defaultSenderClient.setServerURL(null);

        MessageResponseCallback callback = new MessageResponseCallback() {
            @Override
            public void onComplete(int statusCode) {
                // empty body by intention
            }

            @Override
            public void onError(Throwable throwable) {
                // empty body by intention
            }
        };

        UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                            .pushApplicationId(PUSH_APPLICATION_ID)
                            .masterSecret(MASTER_SECRET)
                            .alert(ALERT_MSG)
                            .sound(DEFAULT_SOUND)
                            .aliases(IDENTIFIERS_LIST)
                            .build();

        defaultSenderClient.send(unifiedMessage, callback);
    }

    @Test
    public void testClientBuilderProxySettings() {
        SenderClient client = new SenderClient.Builder()
                .rootServerURL("http://aerogear.example.com/ag-push")
                .proxy("proxy", 8080)
                .proxyType(Proxy.Type.HTTP)
                .build();
        
        assertEquals(client.getServerURL(), "http://aerogear.example.com/ag-push/");
        assertEquals(client.getProxyHost(), "proxy");
        assertEquals(client.getProxyPort(), 8080);
        assertEquals(client.getProxyType(), Proxy.Type.HTTP);
    }
    
    public SenderClient getDefaultSenderClient() {
        return defaultSenderClient;
    }

    public void setDefaultSenderClient(SenderClient defaultSenderClient) {
        this.defaultSenderClient = defaultSenderClient;
    }

    public HttpURLConnection getConnnection() {
        return connection;
    }

    public void setConnection(HttpURLConnection con) {
        this.connection = con;
    }
}