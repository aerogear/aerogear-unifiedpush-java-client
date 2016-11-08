# aerogear-unifiedpush-java-client [![Build Status](https://travis-ci.org/aerogear/aerogear-unifiedpush-java-client.png)](https://travis-ci.org/aerogear/aerogear-unifiedpush-java-client)

A Java API for sending Push Notifications to the [AeroGear UnifiedPush Server](https://github.com/aerogear/aerogear-unifiedpush-server). For more information, please visit the [Java Sender Tutorial](http://aerogear.org/docs/unifiedpush/GetStartedwithJavaSender/) for example usages.

|                 | Project Info  |
| --------------- | ------------- |
| License:        | Apache License, Version 2.0  |
| Build:          | Maven  |
| Documentation:  | https://aerogear.org/push/  |
| Issue tracker:  | https://issues.jboss.org/browse/AGPUSH  |
| Mailing lists:  | [aerogear-users](http://aerogear-users.1116366.n5.nabble.com/) ([subscribe](https://lists.jboss.org/mailman/listinfo/aerogear-users))  |
|                 | [aerogear-dev](http://aerogear-dev.1069024.n5.nabble.com/) ([subscribe](https://lists.jboss.org/mailman/listinfo/aerogear-dev))  |

## Getting started

Add the following dependencies to your ```pom.xml``` file:

```xml
<dependency>
    <groupId>org.jboss.aerogear</groupId>
    <artifactId>unifiedpush-java-client</artifactId>
    <version>1.1.0.Final</version>
</dependency>
```

## Usage

Create a ```JavaSender```:

```java
PushSender defaultPushSender = DefaultPushSender
    .withRootServerURL("http://localhost:8080/ag-push")
    .pushApplicationId("c7fc6525-5506-4ca9-9cf1-55cc261ddb9c")
    .masterSecret("8b2f43a9-23c8-44fe-bee9-d6b0af9e316b")
    .build();
```

You can also use an external config file:

_pushConfig.json_

```json
{
    "serverUrl": "http://aerogear.example.com/ag-push",
    "pushApplicationId": "c7fc6525-5506-4ca9-9cf1-55cc261ddb9c",
    "masterSecret": "8b2f43a9-23c8-44fe-bee9-d6b0af9e316b"}
}
```

And then :

```java
PushSender defaultPushSender = DefaultPushSender
    .withConfig("pushConfig.json").build();
```


To connect via proxy:

```java
PushSender defaultPushSender = DefaultPushSender
    .withConfig("pushConfig.json")
    .proxy("proxy.example.com", 8080)
    .proxyUser("proxyuser")
    .proxyPassword("password")
    .proxyType(Proxy.Type.HTTP)
    .build();

```

To use a custom TrustStore:

```java
PushSender defaultPushSender = DefaultPushSender
    .withConfig("pushConfig.json")
    .customTrustStore("setup/aerogear.truststore", "jks", "aerogear")
    .build();
```

### Send a message

Construct a ```UnifiedMessage``` using the ```Builder``` :

```java
UnifiedMessage unifiedMessage = UnifiedMessage.withCriteria()
    .aliases("john", "maria")
    .message()
        .alert("Hello from Java Sender API!")
        .sound("default")
        // iOS specific
        .badge("1")
        // optional specific to your app
        .userData("some_key", "some_value")
        // optional cordova Android specific attribute (default is appName)
        .userData("title", "Cool Title") 
    .build();
```

Create a callback

```java
MessageResponseCallback callback = new MessageResponseCallback() {
    @Override
    public void onComplete(int statusCode) {
        //do cool stuff
    }
};
```

Send the message

```java
defaultPushSender.send(unifiedMessage, callback); 
```

You can also omit the callback

```java
defaultPushSender.send(unifiedMessage); 
```

## Known issues

On Java7 you might see a ```SSLProtocolException: handshake alert: unrecognized_name``` expection when the UnifiedPush server is running on https. There are a few workarounds:

* JBoss' ```standalone.xml``` configuration file:
 
```xml
...
</extensions>

<system-properties>
    <property name="jsse.enableSNIExtension" value="false"/>
</system-properties>
```

* in the Java app, that is _using_ the Java Client SDK: ```System.setProperty("jsse.enableSNIExtension", "false");```
* Or via commandline argument: ```-Djsse.enableSNIExtension=false```

## Documentation

For more details about the current release, please consult [our documentation](https://aerogear.org/docs/unifiedpush/).

## Development

If you would like to help develop AeroGear you can join our [developer's mailing list](https://lists.jboss.org/mailman/listinfo/aerogear-dev), join #aerogear on Freenode, or shout at us on Twitter @aerogears.

Also takes some time and skim the [contributor guide](http://aerogear.org/docs/guides/Contributing/)

## Questions?

Join our [user mailing list](https://lists.jboss.org/mailman/listinfo/aerogear-users) for any questions or help! We really hope you enjoy app development with AeroGear!

## Found a bug?

If you found a bug please create a ticket for us on [Jira](https://issues.jboss.org/browse/AGPUSH) with some steps to reproduce it.
