# aerogear-unifiedpush-java-client [![Build Status](https://travis-ci.org/aerogear/aerogear-unifiedpush-java-client.png)](https://travis-ci.org/aerogear/aerogear-unifiedpush-java-client)

A Java API for sending Push Notifications to the [AeroGear UnifiedPush Server](https://github.com/aerogear/aerogear-unifiedpush-server). For more information, please visit the [Java Sender Tutorial](http://aerogear.org/docs/unifiedpush/GetStartedwithJavaSender/) for example usages.

## Getting started

Add the following dependencies to your ```pom.xml``` file:

        <dependency>
             <groupId>org.jboss.aerogear</groupId>
             <artifactId>unifiedpush-java-client</artifactId>
             <version>1.1.0</version>
        </dependency>

## Usage

Create a ```JavaSender```:

```
PushSender defaultPushSender = new DefaultPushSender.Builder("http://localhost:8080/ag-push")
                .pushApplicationId("c7fc6525-5506-4ca9-9cf1-55cc261ddb9c")
                .masterSecret("8b2f43a9-23c8-44fe-bee9-d6b0af9e316b")
                .build();
```

To connect via proxy:

```
PushSender defaultPushSender = new DefaultPushSender.Builder("http://localhost:8080/ag-push")
                .pushApplicationId("c7fc6525-5506-4ca9-9cf1-55cc261ddb9c")
                .masterSecret("8b2f43a9-23c8-44fe-bee9-d6b0af9e316b")
                .proxy("proxy.example.com", 8080)
                .proxyUser("proxyuser")
                .proxyPassword("password")
                .proxyType(Proxy.Type.HTTP)
                .build();
```

To use a custom TrustStore:

```
PushSender defaultPushSender = new DefaultPushSender.Builder("http://localhost:8080/ag-push")
                .pushApplicationId("c7fc6525-5506-4ca9-9cf1-55cc261ddb9c")
                .masterSecret("8b2f43a9-23c8-44fe-bee9-d6b0af9e316b")
                .customTrustStore("setup/aerogear.truststore", "jks", "aerogear")
                .build();
```

### Send a message

Construct a ``` UnifiedMessage ``` using the ``` Builder ``` :

```
 UnifiedMessage unifiedMessage = UnifiedMessage.withCriteria()
                .aliases("john", "maria")
                .message()
                  .alert("Hello from Java Sender API!")
                  .sound("default")
                  .badge("1") // iOS specific
                  .userData("some_key", "some_value") // optional attributes specific to your app
                  .userData("title", "Cool Title") // optional cordova Android specific attribute (default is appName)
                .build();
```

Create a callback

```
 MessageResponseCallback callback = new MessageResponseCallback() {

            @Override
            public void onComplete(int statusCode) {
              //do cool stuff
            }

            @Override
            public void onError(Throwable throwable) {
              //bring out the bad news
            }
        };
```

Send the message


``` defaultPushSender.send(unifiedMessage, callback); ```


You can also omit the callback


``` defaultPushSender.send(unifiedMessage); ```


## Known issues

On Java7 you might see a ```SSLProtocolException: handshake alert: unrecognized_name``` expection when the UnifiedPush server is running on https. There are a few workarounds:

* JBoss' ```standalone.xml``` configuration file:
```
...
</extensions>

<system-properties>
   <property name="jsse.enableSNIExtension" value="false"/>
</system-properties>
```

* in the Java app, that is _using_ the Java Client SDK: ```System.setProperty("jsse.enableSNIExtension", "false");```
* Or via commandline argument: ```-Djsse.enableSNIExtension=false```




 
