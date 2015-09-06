package org.mogware.messagebus;

import org.mogware.system.Disposable;
import org.mogware.system.delegates.Action1;

/**
* For supported channels, represents a set of messaging activities on the
* channel (such as receive and send) that happen as a unit or not all.
*
* Instances of this class are single threaded and should not be shared between
* threads.
*/

public interface ChannelTransaction extends Disposable {
    /**
    * Gets a value indicating whether the transaction has been committed,
    * rolled back, or disposed;
    */
    boolean getFinished();

    /**
    * Registers the associated action with the transaction.
    */
    void register(Action1 callback);

    /**
    * Invokes the registered callbacks to mark the transaction as complete.
    */
    void commit();

    /**
    * Rolls back any work performed.
    */
    void rollback();
}
