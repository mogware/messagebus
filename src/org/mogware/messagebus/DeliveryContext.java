package org.mogware.messagebus;

/**
* Represents the delivery of a single message on a particular channel.  Where
* transactional messaging is a available, the send operation will occur within
* the bounds of the receiving transaction.
*
* Instances of this class are single threaded and should not be shared between
* threads.
*/

public interface DeliveryContext {
    /**
    * Gets a value indicating whether the current delivery and subsequent
    * dispatches can continue successfully.
    */
    boolean getActive();

    /**
    * Gets the current inbound message being handled on the channel.
    */
    ChannelMessage getCurrentMessage();

    /**
    * Gets an optional reference to the object used to resolve dependencies.
    */
    DependencyResolver getCurrentResolver();

    /**
    * Gets the current transaction associated with the channel, if transactions
    * are available.
    */
    ChannelTransaction getCurrentTransaction();

    /**
    * Gets the current configuration associated with the channel.
    */
    ChannelGroupConfiguration getCurrentConfiguration();

    /**
    * Prepares a dispatch for transmission.
    */
    DispatchContext prepareDispatch(Object message, MessagingChannel channel);

}
