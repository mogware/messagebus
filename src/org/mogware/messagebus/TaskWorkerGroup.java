package org.mogware.messagebus;

import java.util.LinkedList;
import java.util.List;
import org.mogware.system.Disposable;
import org.mogware.system.ObjectDisposedException;
import org.mogware.system.OperationCanceledException;
import org.mogware.system.delegates.Action1;
import org.mogware.system.delegates.Action2;
import org.mogware.system.delegates.Func0;
import org.mogware.system.threading.CancellationToken;
import org.mogware.system.threading.CancellationTokenSource;
import org.mogware.system.threading.ProducerConsumerQueue;
import org.mogware.system.threading.ProducerConsumerQueueFactory;
import org.mogware.system.threading.SpinWait;
import org.mogware.system.threading.Task;
import org.mogware.system.threading.TaskFactory;

public class TaskWorkerGroup <T extends Disposable>  implements WorkerGroup<T> {
    private final ProducerConsumerQueue<Action1<WorkItem<T>>> workItems;
    private final List<Task> workers = new LinkedList<>();
    private final int minWorkers;
    private final int maxWorkers;
    private CancellationTokenSource tokenSource;
    private Action2<WorkItem<T>, CancellationToken> activityCallback;
    private Func0<T> stateCallback;
    private Func0<Boolean> restartCallback;
    private boolean initialized;
    private boolean started;
    private boolean restarting;
    private boolean disposed;

    public TaskWorkerGroup(int minWorkers, int maxWorkers, int maxQueueSize) {
        if (minWorkers <= 0)
            throw new IllegalArgumentException(
                    "The minimum number of workers is 1.");
        if (maxWorkers < minWorkers)
            throw new IllegalArgumentException(
                    "The maximum number of workers must be at least equal to " +
                    "the minimum number of workers.");
        if (maxQueueSize <= 0)
            throw new IllegalArgumentException(
                    "The minimum size of work queue is 1.");
        this.minWorkers = minWorkers;
        this.maxWorkers = maxWorkers;
        this.workItems = ProducerConsumerQueueFactory.linkedQueue(maxQueueSize);
    }

    @Override
    public void initialize(Func0<T> state, Func0<Boolean> restart) {
        if (state == null)
            throw new NullPointerException("state must not be null.");
        if (restart == null)
            throw new NullPointerException("restart must not be null.");
        synchronized(this) {
            if (this.disposed)
                throw new ObjectDisposedException(this.getClass().getName());
            if (this.initialized)
                throw new IllegalStateException(
                        "The worker group has already been initialized.");
            this.initialized = true;
            this.stateCallback = state;
            this.restartCallback = restart;
        }
    }

    @Override
    public void startActivity(Action1<WorkItem<T>> activity) {
        if (activity == null)
            throw new NullPointerException("activity must not be null.");
        this.tryStartWorkers((worker, token) -> activity.run(worker));
    }

    protected void tryStartWorkers(Action2<WorkItem<T>,
            CancellationToken> activity) {
        synchronized (this) {
            if (this.disposed)
                throw new ObjectDisposedException(this.getClass().getName());
            if (!this.initialized)
                throw new IllegalStateException(
                    "The work group has not been initialized.");
            if (this.started)
                throw new IllegalStateException(
                    "The worker group has already been started.");
            this.started = true;
            this.tokenSource = new CancellationTokenSource();
            this.activityCallback = activity;
            this.workers.clear();

            CancellationToken token = this.tokenSource.getToken();
            for (int i = 0; i < this.minWorkers; i++) {
                this.workers.add(this.startWorker(
                        () -> this.runActivity(token, activity)));
            }
        }
    }

    protected Task startWorker(Func0<Long> activity) {
        return TaskFactory.startNew(activity);
    }

    protected long runActivity(CancellationToken token, Action2<WorkItem<T>,
            CancellationToken> activity) {
        T state = this.stateCallback.call();
        if (state == null) {
            this.restart();
            return 0;
	}
	TaskWorker<T> worker = new TaskWorker<>(
                state, token, this.minWorkers, this.maxWorkers);
	activity.run(worker, token);
        state.dispose();
        return 0;
}

    @Override
    public void startQueue() {
        this.tryStartWorkers((worker, token) -> watchQueue(worker, token));
    }

    protected void watchQueue(WorkItem<T> worker, CancellationToken token) {
        while(!token.isCancellationRequested()) {
            try {
                this.workItems.dequeue(token).run(worker);
            } catch (OperationCanceledException ex) {
            } catch (IllegalStateException ex) {
                break;
            }
        }
    }

    @Override
    public void restart() {
        synchronized (this) {
            if (this.restarting)
                return;
            this.restarting = true;

            if (this.disposed)
                throw new ObjectDisposedException(this.getClass().getName());
            if (!this.initialized)
                throw new IllegalStateException(
                    "The work group has not been initialized.");
            if (!this.started)
                throw new IllegalStateException(
                    "The worker group has not yet been started.");
            this.tokenSource.cancel();
            this.tokenSource = new CancellationTokenSource();

            CancellationToken token = this.tokenSource.getToken();
            this.workers.clear();
            this.workers.add(this.startWorker(() -> this.restart(token)));
        }
    }

    protected long restart(CancellationToken token) {
        SpinWait.spinUntil(() -> token.isCancellationRequested() ||
                this.restartCallback.call());
        synchronized (this) {
            this.started = this.restarting = false;
            this.tryStartWorkers(this.activityCallback);
        }
        return 0;
    }

    @Override
    public boolean enqueue(Action1<WorkItem<T>> workItem) {
        if (workItem == null)
            throw new NullPointerException("workItem must not be null.");
        try {
            this.workItems.enqueue(workItem);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    @Override
    public void dispose() {
        synchronized (this) {
            if (this.disposed)
                return;
            this.disposed = true;
            this.workItems.CompleteAdding();
            if (this.tokenSource == null)
                return;
            this.tokenSource.cancel();
            this.workers.clear();
        }
    }
}
