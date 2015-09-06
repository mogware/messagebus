package org.mogware.messagebus;

import org.mogware.system.Disposable;
import org.mogware.system.delegates.Action1;

/**
* Represents a set of channels which operate connect to the same physical
* endpoint location and which as a cohesive unit.
*
* Instances of this class must be designed to be multi-thread safe such that
* they can be shared between threads.
*/

public interface ChannelGroup extends Disposable {
    /**
    * Gets a value indicating whether the channel group is a dispatch-only
    * (non-receiving) group.
    */
    boolean getDispatchOnly ();

    /**
    * Starts up the underlying connector, initializes all channels associated
    * with the group, and otherwise prepares the channel group to process and
    * dispatch messages.
    */
    void initialize();

    /**
    * Creates a messaging channel that is not controlled or tracked by channel
    * group and which is owned and controlled by the caller.
    */
    MessagingChannel openChannel();

    /**
    * Begins streaming any available inbound messages to the callback provided;
    * for dispatch-only groups it throws an exception.
    */
    void beginReceive(Action1<DeliveryContext> callback);

    /**
    * For dispatch-only channel groups, it adds the callback provided to an
    * in-memory queue for asynchronous invocation; for full-duplex channel
    * groups (send/receive), it throws an exception.
    */
    boolean beginDispatch(Action1<DispatchContext> callback);
}
