package org.aerogear.unifiedpush;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sebastien
 * Date: 6/11/13
 * Time: 8:20 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Client {
    void initialize(String url);
    void post(Map<String, ? extends Object>  payload, String url);
    void post(Map<String, ? extends Object>  payload, List<String> clientIdentifiers, String url);
}
