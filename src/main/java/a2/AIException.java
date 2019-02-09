package a2;

/**
 *  Exception class to encapsulate exceptions that come from the Chatbot itself.
 *  This is a checked exception.  It will be thrown when the Chatbot AI goes
 *  into a bad state and must be restarted, or when the input is malformed.
 *  This exception requires a message.
 *
 *  @author Prof. Chatterbot
 */
public class AIException extends Exception {
    public AIException(String message) {
        super(message);
    }
}
