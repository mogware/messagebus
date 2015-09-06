package org.mogware.messagebus;

import org.mogware.system.Disposable;
import org.mogware.system.threading.CancellationToken;
import org.mogware.system.delegates.Action0;

public class TaskWorker<T extends Disposable> implements WorkItem<T> {
    private final CancellationToken token;
    private final int minWorkers;
    private final T state;

    public TaskWorker(T state, CancellationToken token, int minWorkers,
            int maxWorkers) {
        if (state == null)
            throw new NullPointerException("state must not be null");
        this.state = state;
        this.token = token;
        this.minWorkers = minWorkers;
    }

    @Override
    public int getActiveWorkers() {
        return this.minWorkers;
    }

    @Override
    public T getState() {
        return this.state;
    }

    @Override
    public void performOperation(Action0 operation) {
        if (operation == null)
            throw new NullPointerException("operation must not be null");
        if (!this.token.isCancellationRequested())
            operation.run();
    }
}
