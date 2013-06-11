package org.aerogear.unifiedpush;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sebastien
 * Date: 6/10/13
 * Time: 6:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultJavaSender implements JavaSender{
    // final?
    private String serverURL;

    private Client client;

    public DefaultJavaSender(String rootServerURL, Client client) {
        if (rootServerURL == null) {
            throw new IllegalStateException("server can not be null");
        }

        if (! rootServerURL.endsWith("/") ) {
            rootServerURL = rootServerURL.concat("/");
        }
        this.serverURL = rootServerURL;
        this.client = client;
    }

    protected StringBuilder buildUrl(String type, String pushApplicationID) {
        //  build the broadcast URL:
        StringBuilder sb = new StringBuilder();
        sb.append(serverURL)
                .append("rest/sender/")
                .append(type + "/")
                .append(pushApplicationID);
        return sb;
    }

    @Override
    public void broadcast(Map<String, ? extends Object> json, String pushApplicationID) {
        client.initialize(serverURL);
        StringBuilder sb = buildUrl("broadcast",pushApplicationID);
        client.post(json,sb.toString());
    }

    @Override
    public void sendTo(List<String> clientIdentifiers, Map<String, ? extends Object> json, String pushApplicationID) {
        client.initialize(serverURL);
        StringBuilder sb = buildUrl("selected",pushApplicationID);
        client.post(json, clientIdentifiers, sb.toString());
    }
}
