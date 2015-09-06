package org.mogware.messagebus;

import java.net.URI;
import org.mogware.system.Disposable;

/**
* Represents the attempt to delivery a set of logical messages to the
* associated message handlers.
* 
* Instances of this class are single threaded and should not be shared between
* threads.
*/

public interface HandlerContext extends DeliveryContext, Disposable {
    /**
    * Gets a value indicating whether or not processing of the given channel
    * message should continue.
    */
    boolean getContinueHandling();

    /**
    * Stops handling the current channel message and consumes the message.
    */
    void dropMessage();

    /**
    * Stops handling the channel message and re-enqueues it for later delivery.
    */
    void deferMessage();

    /**
    * Forwards the current channel message to each of the recipients provided
    * and continues handling the message.
    */
    void forwardMessage(Iterable<URI> recipients);
}
