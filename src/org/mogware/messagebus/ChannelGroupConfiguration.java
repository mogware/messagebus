package org.mogware.messagebus;

import java.net.URI;
import org.mogware.system.threading.TimeSpan;

/**
* Represents the minimum configuration necessary to establish a channel group.
*
* Instances of this class must be designed to be multi-thread safe such that
* they can be shared between threads.
*/

public interface ChannelGroupConfiguration {
    /**
    * Gets the value which uniquely identifies the named configuration or
    * channel group.
    */
    String getGroupName();

    /**
    * Gets a value indicating whether the particular configuration should
    * support asynchronous operations.
    */
    boolean getSynchronous();

    /**
    * Gets a value indicating whether the connection is configured for dispatch
    * or full duplex.
    */
    boolean getDispatchOnly();

    /**
    * Gets a value indicating the maximum number of items allowed in the
    * dispatch-only buffer
    */
    int getMaxDispatchBuffer();

    /**
    * Gets a value indicating the minimum number of workers to be allocated
    * for work.
    */
    int getMinWorkers();

    /**
    * Gets a value indicating the maximum allowable number of workers to be
    * allocated for work.
    */
    int getMaxWorkers();

    /**
    * Gets the URI representing the address to which all reply messages will
    * be sent.
    */
    URI getReturnAddress();

    /**
    * Gets a reference to the object instance used to build new, outbound
    * channel messages.
    */
    ChannelMessageBuilder getMessageBuilder();

    /**
    * Gets the length of time to await the receipt of a message from a channel
    * before resume other work.
    */
    TimeSpan getReceiveTimeout();

    /**
    * Gets an optional reference to resolver used to manage dependencies.
    */
    DependencyResolver getDependencyResolver();

    /**
    * Gets a reference to the dispatch table to determine the appropriate
    * recipients for a given type of message.
    */
    DispatchTable getDispatchTable();
}
