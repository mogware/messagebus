package org.mogware.messagebus;

import java.net.URI;
import java.util.Map;
import org.mogware.system.Guid;

/**
* Provides the ability to assemble a message and associated metadata piece by
* piece for transmission.
* 
* Instances of this class are single threaded and should not be shared between
* threads.
*/

public interface DispatchContext {
    /**
    * Gets the number of logical, business-specific messages pending dispatch.
    */
    int getMessageCount();

    /**
    * Gets the number of headers that have been added to the pending dispatch.
    */
    int getHeaderCount();

    /**
    * Appends a single message to the dispatch.
    */
    DispatchContext withMessage(Object message);

    /**
    * Appends a set of messages to the dispatch.
    */
    DispatchContext withMessages(Object[] messages);

    /**
    * Assigns the correlation identifier specified to the dispatch.
    */
    DispatchContext withCorrelationId(Guid correlationId);

    /**
    * Appends a header to the message metadata.
    */
    DispatchContext withHeader(String key, String value);

    /**
    * Appends a set of headers to the message metadata.
    */
    DispatchContext withHeaders(Map<String,String> headers);

    /**
    * Specifies an additional recipient for the dispatch beyond those obtained for the underlying dispatch table.
    */
    DispatchContext withRecipient(URI recipient);

    /**
    * Specifies additional state to be added to the outbound dispatch envelope for evaluation by the upstream pipeline
    * while in the current application process space.
    */
    DispatchContext withState(Object state);

    /**
    * Pushes the message onto the underlying channel sending it to any interested parties and completes the context.
    */
    ChannelTransaction send(Object[] messages);

    /**
    * Pushes the message onto the underlying channel publishing it to all interested parties, and completes the context.
    */
    ChannelTransaction publish(Object[] messages);

    /**
    * Pushes the message onto the channel sending it to the original sender, if any, and completes the context.
    */
    ChannelTransaction reply(Object[] messages);
}
