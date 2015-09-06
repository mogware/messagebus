package org.mogware.messagebus;

import org.mogware.system.Disposable;
import org.mogware.system.delegates.Action0;

/**
* Provides the ability to track state for activity being performed.
* The state held by the worker.
*/

public interface WorkItem<T extends Disposable> {
    /**
    * Gets the value which indicates the number of active workers performing
    * the activity.
    */
    int getActiveWorkers();

    /**
    * Gets the state associated with the activity.
    */
    T getState();

    /**
    * Instructs the worker to perform the operation indicated.
    */
    void performOperation(Action0 operation);
}
