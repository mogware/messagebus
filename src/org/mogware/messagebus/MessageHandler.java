package org.mogware.messagebus;

/**
* Provides the ability to understand and handle a logical message.
* The type of message to be handled.
* 
* Instances of this class may be either single or multi-threaded depending upon
* their registration.
*/

public interface MessageHandler<T> {
    /**
    * Handles the message provided.
    */
    void handle(T message);
}
