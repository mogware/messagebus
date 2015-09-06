package org.mogware.messagebus;

/**
* Provides the ability to wrap the delivery of a message.
*
* Instances of this class must be designed to be multi-thread safe such that
* they can be shared between threads.
*/
public interface DeliveryHandler extends MessageHandler<DeliveryContext> { }
