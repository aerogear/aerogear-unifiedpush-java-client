# aerogear-unified-push-java-client [![Build Status](https://travis-ci.org/aerogear/aerogear-unified-push-java-client.png)](https://travis-ci.org/aerogear/aerogear-unified-push-java-client)


A Java API for sending Push Notifications with the [AeroGear UnifiedPush Sender](https://github.com/aerogear/aerogear-unified-push-server).

## Getting started

        <dependency>
             <groupId>org.jboss.aerogear.unifiedpush</groupId>
             <artifactId>java-sender</artifactId>
             <version>1.0-SNAPSHOT</version>
        </dependency>

## Usage

Create a ```JavaSender```:

    DefaultJavaSender defaultJavaSender =
      new DefaultJavaSender("http://localhost:8080/ag-push", new RestEasyClient());


Construct the JSON payload:

     Map<String, String> jsonPlayload = new HashMap<String, String>();
     jsonPlayload.put("alert", "Hello from Java Sender API, via JUnit");
     jsonPlayload.put("sound", "default");

Send it to a ```PushApplication```:

    defaultJavaSender.broadcast(jsonPlayload, pushApplicationID);
        
        


