# AeroGear Unified Push Java Client

[![Build Status](https://travis-ci.org/aerogear/aerogear-unifiedpush-java-client.png)](https://travis-ci.org/aerogear/aerogear-unifiedpush-java-client)
[![Maven Central](https://img.shields.io/maven-central/v/org.jboss.aerogear/unifiedpush-java-client.svg)](https://search.maven.org/artifact/org.jboss.aerogear/unifiedpush-java-client/)
[![Javadocs](http://www.javadoc.io/badge/org.jboss.aerogear/unifiedpush-java-client.svg?color=blue)](http://www.javadoc.io/doc/org.jboss.aerogear/unifiedpush-java-client)
[![License](https://img.shields.io/badge/-Apache%202.0-blue.svg)](https://opensource.org/s/Apache-2.0)

A Java API for sending Push Notifications to the [AeroGear UnifiedPush Server](https://github.com/aerogear/aerogear-unifiedpush-server).

|                          | Project Info                                            |
| ------------------------ | ------------------------------------------------------- |
| License:                 | Apache License, Version 2.0                             |
| Build:                   | Maven                                                   |
| End User Documentation:  | https://docs.aerogear.org                               |
| Community Documentation: | https://aerogear.org                                    |
| Issue tracker:           | https://issues.jboss.org/browse/AEROGEAR                |
| Mailing lists:           | https://groups.google.com/forum/#!forum/aerogear        |

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
    .withRootServerURL("<pushServerURL e.g http(s)//host:port/context>")
    .pushApplicationId("<pushApplicationId e.g. 1234456-234320>")
    .masterSecret("<masterSecret e.g. 1234456-234320>")
    .build();
```

You can also use an external config file:

_pushConfig.json_

```json
{
    "serverUrl": "<pushServerURL e.g http(s)//host:port/context>",
    "pushApplicationId": "<pushApplicationId e.g. 1234456-234320>",
    "masterSecret": "<masterSecret e.g. 1234456-234320>"
}
```

And then :

```java
PushSender defaultPushSender = DefaultPushSender
    .withConfig("pushConfig.json")
    .build();
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
    public void onComplete() {
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

## License 

 See [LICENSE file](./LICENSE.txt)

## Questions?

Join our [user mailing list](https://groups.google.com/forum/#!forum/aerogear) for any questions or help! We really hope you enjoy app development with AeroGear.

## Found a bug?

If you found a bug please create a ticket for us on [Jira](https://issues.jboss.org/browse/AEROGEAR) with some steps to reproduce it.
