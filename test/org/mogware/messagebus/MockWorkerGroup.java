package org.mogware.messagebus;

import org.mogware.system.Disposable;
import org.mogware.system.delegates.Action1;
import org.mogware.system.delegates.Action2;
import org.mogware.system.delegates.Func0;

public class MockWorkerGroup<T extends Disposable> implements WorkerGroup<T> {
    protected Action2<Func0<T>, Func0<Boolean>> initializeCallback;
    protected Action1<Action1<WorkItem<T>>> startActivityCallback;
    protected Action1<Action1<WorkItem<T>>> enqueueCallback;
    
    protected int initializeCallCount = 0;
    protected int startActivityCallCount = 0;
    protected int startQueueCallCount = 0;    
    protected int restartCallCount = 0;        
    protected int enqueueCallCount = 0;
    protected int disposeCallCount = 0;    
    
    protected boolean enqueueReturnValue = true;
    
    @Override
    public void initialize(Func0<T> state, Func0<Boolean> restart) {
        if (this.initializeCallback != null)
            this.initializeCallback.run(state, restart);
        this.initializeCallCount++;
    }

    @Override
    public void startActivity(Action1<WorkItem<T>> activity) {
        if (this.startActivityCallback != null)
            this.startActivityCallback.run(activity);
        this.startActivityCallCount++;
    }

    @Override
    public void startQueue() {
        this.startQueueCallCount++;
    }

    @Override
    public void restart() { 
        this.restartCallCount++;
    }

    @Override
    public boolean enqueue(Action1<WorkItem<T>> workItem) {
        if (enqueueCallback != null)
            enqueueCallback.run(workItem);
        this.enqueueCallCount++;
        return this.enqueueReturnValue;
    }

    @Override
    public void dispose() {
        this.disposeCallCount++;
    }
}
