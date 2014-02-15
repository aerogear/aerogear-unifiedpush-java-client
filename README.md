# aerogear-unifiedpush-java-client

A Java API for sending Push Notifications to the [AeroGear UnifiedPush Server](https://github.com/aerogear/aerogear-unifiedpush-server). For more information, please visit the [Java Sender Tutorial](http://aerogear.org/docs/guides/GetStartedwithJavaSender/) for example usages.

## Getting started

Add the following dependencies to your ```pom.xml``` file:

        <dependency>
             <groupId>org.jboss.aerogear</groupId>
             <artifactId>unifiedpush-java-client</artifactId>
             <version>0.6.0-SNAPSHOT</version>
        </dependency>

        <!-- Jackson2 -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.3.1</version>
        </dependency>

## Usage

Create a ```JavaSender```:

    JavaSender defaultJavaSender =
      new SenderClient("http://localhost:8080/ag-push");

### Send a message

Construct a ``` UnifiedMessage ``` using the ``` Builder ``` :

```
 UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .pushApplicationId("c7fc6525-5506-4ca9-9cf1-55cc261ddb9c")
                .masterSecret("8b2f43a9-23c8-44fe-bee9-d6b0af9e316b")
                .aliases(Arrays.asList("john", "maria"))
                .alert("Hello from Java Sender API!")
                .sound("default")
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


``` defaultJavaSender.send(unifiedMessage, callback); ```


You can also omit the callback


``` defaultJavaSender.send(unifiedMessage); ```


## Known issues

On Java7 you might see a ```SSLProtocolException: handshake alert: unrecognized_name``` expection when the UnifiedPush server is running on https. There are a few workarounds:

* JBoss' ```standalone.xml``` configuration file:
```
<system-properties>
   <property name="jsse.enableSNIExtension" value="false"/>
</system-properties>
```

* in the Java app, that is _using_ the Java Client SDK: ```System.setProperty("jsse.enableSNIExtension", "false");```
* Or via commandline argument: ```-Djsse.enableSNIExtension=false```




 
