package org.jboss.aerogear.unifiedpush;

/**
 * Called when Sender failed to community with UnifiedPush Sender
 */
public class PushSenderException extends RuntimeException {

    private static final long serialVersionUID = -1946523107970810661L;

    private int statusCode;

    public PushSenderException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
