# aerogear-unifiedpush-java-client

A Java API for sending Push Notifications to the [AeroGear UnifiedPush Server](https://github.com/aerogear/aerogear-unifiedpush-server).

## Getting started

Add the following dependencies to your ```pom.xml``` file:

        <dependency>
             <groupId>org.jboss.aerogear.unifiedpush</groupId>
             <artifactId>unified-push-java-client</artifactId>
             <version>0.2.2</version>
        </dependency>

        <!-- RestEasy's Jackson Plugin -->
        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-jackson-provider</artifactId>
            <version>2.3.2.Final</version>
        </dependency>

## Usage

Create a ```JavaSender```:

    JavaSender defaultJavaSender =
      new SenderClient("http://localhost:8080/ag-push");

### Send a message

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




 
