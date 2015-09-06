package org.mogware.messagebus;

import org.mogware.system.Disposable;
import org.mogware.system.delegates.Action1;

/**
* Represents partition used to separate activities over a single connection to
* messaging infrastructure.
* 
* Instances of this class are single threaded and should not be shared between
* threads.
*/

public interface MessagingChannel extends DeliveryContext, Disposable {
    /**
    * Initiates the process shutting down the channel to prevent additional
    * sends and/or receives from occurring on the channel.
    */
    void beginShutdown();

    /**
    * Begins receiving messages from the channel and dispatches them to the
    * callback provided.
    */
    void receive(Action1<DeliveryContext> callback);

    /**
    * Sends the message specified to the destinations provided.
    */
    void send(ChannelEnvelope envelope);
}
