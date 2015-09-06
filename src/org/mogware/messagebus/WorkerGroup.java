package org.mogware.messagebus;

import org.mogware.system.Disposable;
import org.mogware.system.delegates.Action1;
import org.mogware.system.delegates.Func0;

/**
* Represents a set of concurrent workers that perform activities.
* The state held by the worker.
* Instances of this class must be designed to be multi-thread safe such that
* they can be shared between threads.
*/

public interface WorkerGroup<T extends Disposable> extends Disposable {
    /**
    * Initializes the factory and causes all future worker groups to use the
    * callbacks provided.
    */
    void initialize(Func0<T> state, Func0<Boolean> restart);

    /**
    * Builds a worker group which starts performing the activity specified.
    */
    void startActivity(Action1<WorkItem<T>> activity);

    /**
    * Builds a worker group which watches a work item queue.
    */
    void startQueue();

    /**
    * Initiates the stopping and restarting of the activity currently being
    * performed.
    */
    void restart();

    /**
    * Adds a work item to be performed by one of the workers within the worker
    * group.  Work items can safely be added at any time during the lifetime of
    * the object instance.
    */
    boolean enqueue(Action1<WorkItem<T>> workItem);
}
