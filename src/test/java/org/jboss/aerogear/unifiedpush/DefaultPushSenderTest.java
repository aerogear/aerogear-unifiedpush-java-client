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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HttpsURLConnection;

import org.jboss.aerogear.unifiedpush.http.HttpClient;
import org.jboss.aerogear.unifiedpush.message.MessageResponseCallback;
import org.jboss.aerogear.unifiedpush.message.UnifiedMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpClient.class)
public class DefaultPushSenderTest {

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
    private PushSender defaultSenderClient;
    private PushSender secureSenderClient;

    private URLConnection connection;
    private URLConnection secureConnection;

    @Before
    public void setup() throws Exception {
        // mock output stream
        OutputStream out = PowerMockito.mock(OutputStream.class);
        PowerMockito.doNothing().when(out).write(any(byte[].class));
        // mock connection
        setConnection(PowerMockito.mock(HttpURLConnection.class));
        setSecureConnection(PowerMockito.mock(HttpsURLConnection.class));
        when(connection.getOutputStream()).thenReturn(out);
        when(secureConnection.getOutputStream()).thenReturn(out);
        // mock getConnection method
        setDefaultSenderClient(PowerMockito.spy(DefaultPushSender.withRootServerURL("http://aerogear.example.com/ag-push").build()));
        setSecureSenderClient(PowerMockito.spy(DefaultPushSender.withRootServerURL("https://aerogear.example.com/ag-push").build()));
        PowerMockito.spy(HttpClient.class);
        PowerMockito.doReturn(connection).when(HttpClient.class, "getConnection", Matchers.startsWith("http://"), any());
        PowerMockito.doReturn(secureConnection).when(HttpClient.class, "getConnection", Matchers.startsWith("https://"), any());
    }

    @Test
    public void initWithExternalConfig() {
        defaultSenderClient = DefaultPushSender.withConfig("pushConfig.json").build();
        assertEquals(PUSH_APPLICATION_ID, defaultSenderClient.getPushApplicationId());
        assertEquals(MASTER_SECRET, defaultSenderClient.getMasterSecret());
        assertEquals("http://aerogear.example.com/ag-push", defaultSenderClient.getServerURL());
    }

    @Test
    public void sendSendWithCallback404() throws Exception {
        // return 404
        int STATUS_NOT_FOUND = 404;

        when(((HttpURLConnection) getConnnection()).getResponseCode()).thenReturn(STATUS_NOT_FOUND);

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

        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .alert(ALERT_MSG)
                .sound(DEFAULT_SOUND)
                .criteria().aliases(IDENTIFIERS_LIST)
                .build();

        defaultSenderClient.send(unifiedMessage, callback);

        latch.await(1000, TimeUnit.MILLISECONDS);
        // onError callback should not be called
        assertFalse(onFailCalled.get());
        assertNotNull(returnedStatusList);
        assertEquals(1, returnedStatusList.size());
        assertEquals(STATUS_NOT_FOUND, returnedStatusList.get(0).intValue());
    }

    @Test
    public void sendSendWithCallback404_SSL() throws Exception {
        // return 404
        int STATUS_NOT_FOUND = 404;

        when(((HttpURLConnection) getSecureConnection()).getResponseCode()).thenReturn(STATUS_NOT_FOUND);

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

        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .alert(ALERT_MSG)
                .sound(DEFAULT_SOUND)
                .criteria().aliases(IDENTIFIERS_LIST)
                .build();

        secureSenderClient.send(unifiedMessage, callback);

        latch.await(1000, TimeUnit.MILLISECONDS);
        // onError callback should not be called
        assertFalse(onFailCalled.get());
        assertNotNull(returnedStatusList);
        assertEquals(1, returnedStatusList.size());
        assertEquals(STATUS_NOT_FOUND, returnedStatusList.get(0).intValue());
    }

    @Test
    public void sendSendWithCallbackAndException() throws Exception {
        // throw IOException when posting
        PowerMockito.doThrow(new IOException()).when(HttpClient.class, "post", anyString(), anyString(), anyString(), any(),
                any(), any());

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

        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .alert(ALERT_MSG)
                .sound(DEFAULT_SOUND)
                .criteria().aliases(IDENTIFIERS_LIST)
                .build();

        defaultSenderClient.send(unifiedMessage, callback);

        latch.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(onFailCalled.get());
        assertEquals(IOException.class, exceptionReference.get().getClass());

    }

    @Test
    public void sendSendWithCallbackAndException_SSL() throws Exception {
        // throw IOException when posting
        PowerMockito.doThrow(new IOException()).when(HttpClient.class, "post", anyString(), anyString(), anyString(), any(),
                any(), any());

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

        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .alert(ALERT_MSG)
                .sound(DEFAULT_SOUND)
                .criteria().aliases(IDENTIFIERS_LIST)
                .build();

        secureSenderClient.send(unifiedMessage, callback);

        latch.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(onFailCalled.get());
        assertEquals(IOException.class, exceptionReference.get().getClass());

    }

    @Test
    public void sendSendWithCallback200() throws Exception {
        // return 200
        int STATUS_OK = 200;

        when(((HttpURLConnection) getConnnection()).getResponseCode()).thenReturn(STATUS_OK);

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

        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .alert(ALERT_MSG)
                .sound(DEFAULT_SOUND)
                .criteria().aliases(IDENTIFIERS_LIST)
                .build();

        defaultSenderClient.send(unifiedMessage, callback);

        latch.await(1000, TimeUnit.MILLISECONDS);
        // onError callback should not be called
        assertFalse(onFailCalled.get());
        assertNotNull(returnedStatusList);
        assertEquals(1, returnedStatusList.size());
        assertEquals(STATUS_OK, returnedStatusList.get(0).intValue());
    }

    @Test
    public void sendSendWithInfiniteRedirect() throws Exception {
        // return 301
        int STATUS_REDIRECT = 301;
        when(((HttpURLConnection) getConnnection()).getResponseCode()).thenReturn(STATUS_REDIRECT);
        when(getConnnection().getHeaderField("Location")).thenReturn("http://aerogear.example.com/ag-push");

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean onFailCalled = new AtomicBoolean(false);
        final List<Throwable> throwableList = new ArrayList<Throwable>(1);

        MessageResponseCallback callback = new MessageResponseCallback() {
            @Override
            public void onComplete(int statusCode) {
                latch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                onFailCalled.set(true);
                throwableList.add(throwable);
                latch.countDown();
            }
        };

        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .alert(ALERT_MSG)
                .sound(DEFAULT_SOUND)
                .criteria().aliases(IDENTIFIERS_LIST)
                .build();

        defaultSenderClient.send(unifiedMessage,callback);
        latch.await(1000, TimeUnit.MILLISECONDS);
        // onError callback should not be called
        assertTrue(onFailCalled.get());
        assertEquals(throwableList.get(0).getMessage(), "The site contains an infinite redirect loop! Duplicate url: http://aerogear.example.com/ag-push");
    }

    @Test
    public void sendSendWithCallback200_SSL() throws Exception {
        // return 200
        int STATUS_OK = 200;

        when(((HttpURLConnection) getSecureConnection()).getResponseCode()).thenReturn(STATUS_OK);

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

        UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                .alert(ALERT_MSG)
                .sound(DEFAULT_SOUND)
                .criteria().aliases(IDENTIFIERS_LIST)
                .build();

        secureSenderClient.send(unifiedMessage, callback);

        latch.await(1000, TimeUnit.MILLISECONDS);
        // onError callback should not be called
        assertFalse(onFailCalled.get());
        assertNotNull(returnedStatusList);
        assertEquals(1, returnedStatusList.size());
        assertEquals(STATUS_OK, returnedStatusList.get(0).intValue());
    }

    @Test(expected = IllegalStateException.class)
    public void emptyServerURL() throws Exception {
        DefaultPushSender.withRootServerURL(null).build();
    }

    @Test
    public void testClientBuilderProxySettings() {
        DefaultPushSender client = DefaultPushSender.withRootServerURL("http://aerogear.example.com/ag-push")
                .proxy("proxy", 8080)
                .proxyType(Proxy.Type.HTTP)
                .build();

        assertEquals("http://aerogear.example.com/ag-push/", client.getServerURL());
        assertEquals("proxy", client.getProxy().getProxyHost());
        assertEquals(8080, client.getProxy().getProxyPort());
        assertEquals(Proxy.Type.HTTP, client.getProxy().getProxyType());
    }

    @Test
    public void testClientBuildertrustStoreSettings() {
        DefaultPushSender client = DefaultPushSender.withRootServerURL("https://aerogear.example.com/ag-push")
                .customTrustStore("../test.truststore", null, "aerogear")
                .build();

        assertEquals("https://aerogear.example.com/ag-push/", client.getServerURL());
        assertEquals("../test.truststore", client.getCustomTrustStore().getTrustStorePath());
        assertNull(client.getCustomTrustStore().getTrustStoreType());
        assertEquals("aerogear", client.getCustomTrustStore().getTrustStorePassword());
    }

    @Test
    public void testClientBuilderPushAppIdAndMasterSecret() {
        DefaultPushSender client = DefaultPushSender.withRootServerURL("https://aerogear.example.com/ag-push")
                .pushApplicationId(PUSH_APPLICATION_ID)
                .masterSecret(MASTER_SECRET)
                .build();

        assertEquals("https://aerogear.example.com/ag-push/", client.getServerURL());
        assertEquals(PUSH_APPLICATION_ID, client.getPushApplicationId());
        assertEquals(MASTER_SECRET, client.getMasterSecret());
    }

    public PushSender getDefaultSenderClient() {
        return defaultSenderClient;
    }

    public void setDefaultSenderClient(DefaultPushSender defaultSenderClient) {
        this.defaultSenderClient = defaultSenderClient;
    }

    public URLConnection getConnnection() {
        return connection;
    }

    public void setConnection(URLConnection con) {
        connection = con;
    }

    public PushSender getSecureSenderClient() {
        return secureSenderClient;
    }

    public void setSecureSenderClient(PushSender secureSenderClient) {
        this.secureSenderClient = secureSenderClient;
    }

    public URLConnection getSecureConnection() {
        return secureConnection;
    }

    public void setSecureConnection(URLConnection secureConnection) {
        this.secureConnection = secureConnection;
    }
}