package org.mogware.messagebus;

import org.mogware.system.Disposable;
import org.mogware.system.delegates.Action0;
import org.mogware.system.delegates.Action1;

public class MockWorkItem<T extends Disposable> implements WorkItem<T> {
    protected T state;
    protected int performOperationCallCount = 0;        
    protected Action1<Action0> performOperationCallback;
    
    @Override
    public int getActiveWorkers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T getState() {
        return this.state;
    }

    @Override
    public void performOperation(Action0 operation) {
        this.performOperationCallCount++;
        if (this.performOperationCallback != null)
            this.performOperationCallback.run(operation);
    }
}
