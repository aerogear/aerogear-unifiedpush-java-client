A Java API for sending Push Notifications with the [AeroGear UnifiedPush Sender](https://github.com/aerogear/aerogear-unified-push-server).

## Getting started

        <dependency>
             <groupId>org.jboss.aerogear.unifiedpush</groupId>
             <artifactId>unified-push-java-client</artifactId>
             <version>0.2.1</version>
        </dependency>

## Usage

Create a ```JavaSender```:

    JavaSender defaultJavaSender =
      new SenderClient("http://localhost:8080/ag-push");

### Send a Broadcast message

Construct a ``` UnifiedMessage ``` using the ``` Builder ``` :

```
 UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .pushApplicationId("c7fc6525-5506-4ca9-9cf1-55cc261ddb9c")
                .masterSecret("8b2f43a9-23c8-44fe-bee9-d6b0af9e316b")
                .alert("Hello from Java Sender API, via JUnit")
                .sound("default")
                .build();
```

Send the message

``` defaultJavaSender.broadcast(unifiedMessage); ```

### Send a Selective message

Construct a ``` UnifiedMessage ``` using the ``` Builder ``` :

```
 List<String> identifiers = new ArrayList<String>();
 identifiers.add("john");
 UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                .pushApplicationId("c7fc6525-5506-4ca9-9cf1-55cc261ddb9c")
                .masterSecret("8b2f43a9-23c8-44fe-bee9-d6b0af9e316b")
                .alert("Hello from Java Sender API, via JUnit")
                .sound("default")
                .aliases(identifiers)
                .build();
```

Send the message

``` defaultJavaSender.sendTo(unifiedMessage); ```

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




 
