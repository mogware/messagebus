package org.mogware.messagebus;

import org.junit.Test;
import static org.junit.Assert.*;
import org.mogware.system.ObjectDisposedException;
import org.mogware.system.delegates.Func0;

public class DefaultChannelGroupTest {
    private MockChannelGroupConfiguration configuration;
    private MockChannelConnector connector;
    private MockWorkItem<MessagingChannel> worker;
    private MockWorkerGroup<MessagingChannel> workers;
    private MockMessagingChannel channel;
    private Func0<MessagingChannel> stateCallback;
    private Func0<Boolean> restartCallback;

    private void establishContext() {
        this.configuration = new MockChannelGroupConfiguration();
        this.connector = new MockChannelConnector();
        this.worker = new MockWorkItem<>();
        this.workers = new MockWorkerGroup<>();
        this.channel = new MockMessagingChannel();
        this.connector.channel = this.channel;
        this.worker.state = this.channel;
        this.workers.initializeCallback = (state, restart) -> {
            this.stateCallback = state;
            this.restartCallback = restart;
        };
        this.workers.startActivityCallback = (x) -> x.run(this.worker);
    }

    private ChannelGroup build() {
        return new DefaultChannelGroup(
                this.connector,
                this.configuration,
                this.workers);
    }

    @Test
    public void createChannelGroup() {
        println("createChannelGroup");
        this.establishContext();
        this.configuration.dispatchOnly = true;
        ChannelGroup group = build();
        assertTrue(group.getDispatchOnly());
    }

    @Test
    public void initializeChannelGroup() {
        println("initializeChannelGroup");
        this.establishContext();
        ChannelGroup group = build();
        group.initialize();
        assertEquals(1, this.workers.initializeCallCount);
        assertEquals(this.channel, this.stateCallback.call());
    }

    @Test
    public void initializeGroupMoreThanOnce() {
        println("initializeGroupMoreThanOnce");
        this.establishContext();
        ChannelGroup group = build();
        group.initialize();
        group.initialize();
        assertEquals(1, this.workers.initializeCallCount);
    }

    @Test(expected = ObjectDisposedException.class)
    public void initializeDisposedGroup() {
        println("initializeDisposedGroup");
        this.establishContext();
        ChannelGroup group = build();
        group.dispose();
        group.initialize();
    }

    @Test
    public void initializeDispatchOnlyGroup() {
        println("initializeDispatchOnlyGroup");
        this.establishContext();
        this.configuration.dispatchOnly = true;
        ChannelGroup group = build();
        group.initialize();
        assertEquals(1, this.workers.startQueueCallCount);
        assertEquals(1, this.connector.connectCallCount);
    }

    @Test
    public void initializeDispatchOnlyGroupThrowsConnectionException() {
        println("initializeDispatchOnlyGroupThrowsConnectionException");
        this.establishContext();
        this.configuration.dispatchOnly = true;
        this.connector.connectThrowsException = true;
        ChannelGroup group = build();
        group.initialize();
        assertEquals(1, this.connector.connectCallCount);
    }

    @Test
    public void reestablishingConnection() {
        println("reestablishingConnection");
        this.establishContext();
        ChannelGroup group = build();
        group.initialize();
        assertTrue(this.restartCallback.call());
        assertEquals(1, this.connector.connectCallCount);
    }

    @Test
    public void reestablishingConnectionThrowsConnectionException() {
        println("reestablishingConnectionThrowsConnectionException");
        this.establishContext();
        this.connector.connectThrowsException = true;
        ChannelGroup group = build();
        group.initialize();
        assertFalse(this.restartCallback.call());
        assertEquals(1, this.connector.connectCallCount);
    }

    @Test(expected = IllegalStateException.class)
    public void openingCallerOwnedChannelOnUninitializedGroup() {
        println("openingCallerOwnedChannelOnUninitializedGroup");
        this.establishContext();
        ChannelGroup group = build();
        group.openChannel();
    }

    @Test(expected = ObjectDisposedException.class)
    public void openingCallerOwnedChannelOnDisposedGroup() {
        println("openingCallerOwnedChannelOnDisposedGroup");
        this.establishContext();
        ChannelGroup group = build();
        group.initialize();
        group.dispose();
        group.openChannel();
    }

    @Test
    public void openingCallerOwnedChannel() {
        println("openingCallerOwnedChannel");
        this.establishContext();
        ChannelGroup group = build();
        group.initialize();
        assertEquals(channel, group.openChannel());
        assertEquals(1, this.connector.connectCallCount);
    }

    @Test(expected = ChannelConnectionException.class)
    public void openingCallerOwnedChannelFailsToConnect() {
        println("openingCallerOwnedChannelFailsToConnect");
        this.establishContext();
        this.connector.connectThrowsException = true;
        ChannelGroup group = build();
        group.initialize();
        group.openChannel();
    }

    @Test
    public void dispatchMessageWithDispatchOnlyGroup() {
        println("dispatchMessageWithDispatchOnlyGroup");
        this.establishContext();
        this.configuration.dispatchOnly = true;
        this.workers.enqueueCallback = (x) -> x.run(this.worker);
        ChannelGroup group = build();
        group.initialize();
        final int invocations[] = { 0 };
        assertTrue(group.beginDispatch((x) -> invocations[0]++));
        assertEquals(1, this.channel.prepareDispatchCallCount);
        assertEquals(1, invocations[0]);
    }

    @Test
    public void dispatchMessageWhenNotEnqueuing() {
        println("dispatchMessageWhenNotEnqueuing");
        this.establishContext();
        this.configuration.dispatchOnly = true;
        this.workers.enqueueReturnValue = false;
        ChannelGroup group = build();
        group.initialize();
        assertFalse(group.beginDispatch((x) -> {}));
    }

    @Test
    public void dispatchMessageThrowsAnException() {
        println("dispatchMessageThrowsAnException");
        this.establishContext();
        this.configuration.dispatchOnly = true;
        this.channel.prepareDispatchThrowsException = true;
        final int count[] = { 0 };
        this.workers.enqueueCallback = (x) -> {
            if (count[0]++ < 1)
                x.run(this.worker);
        };
        ChannelGroup group = build();
        group.initialize();
        assertTrue(group.beginDispatch((x) -> {}));
        assertEquals(1, this.channel.prepareDispatchCallCount);
        assertEquals(1, this.workers.restartCallCount);
        assertEquals(2, this.workers.enqueueCallCount);
    }

    @Test(expected = IllegalStateException.class)
    public void dispatchMessageToFullDuplexGroup() {
        println("dispatchMessageToFullDuplexGroup");
        this.establishContext();
        ChannelGroup group = build();
        group.initialize();
        group.beginDispatch((x) -> {});
    }

    @Test(expected = NullPointerException.class)
    public void dispatchMessageWithNoCallback() {
        println("dispatchMessageWithNoCallback");
        this.establishContext();
        ChannelGroup group = build();
        group.initialize();
        group.beginDispatch(null);
    }

    @Test(expected = IllegalStateException.class)
    public void dispatchMessageWithoutInitializeFirst() {
        println("dispatchMessageWithoutInitializeFirst");
        this.establishContext();
        ChannelGroup group = build();
        group.beginDispatch((x) -> {});
    }

    @Test
    public void dispatchMessageToDisposedGroup() {
        println("dispatchMessageToDisposedGroup");
        this.establishContext();
        this.configuration.dispatchOnly = true;
        ChannelGroup group = build();
        group.initialize();
        group.dispose();
        assertTrue(group.beginDispatch((x) -> {}));
    }

    @Test
    public void receiveMessageOnFullDuplexGroup() {
        println("receiveMessageOnFullDuplexGroup");
        this.establishContext();
        ChannelGroup group = build();
        group.initialize();
        group.beginReceive((x) -> {});
        assertEquals(1, this.workers.startActivityCallCount);
        assertEquals(1, this.channel.receiveCallCount);
    }

    @Test
    public void receiveMessageThrowsAnException() {
        println("receiveMessageThrowsAnException");
        this.establishContext();
        this.channel.receiveCallback = (c) -> c.run(null);
        this.worker.performOperationCallback = (x) -> x.run();
        ChannelGroup group = build();
        group.initialize();
        group.beginReceive((x) -> { throw new ChannelConnectionException(); });
        assertEquals(1, this.workers.restartCallCount);
    }

    @Test(expected = NullPointerException.class)
    public void receiveMessageWithNoCallback() {
        println("receiveMessageWithNoCallback");
        this.establishContext();
        ChannelGroup group = build();
        group.initialize();
        group.beginReceive(null);
    }

    @Test(expected = IllegalStateException.class)
    public void receiveMessageWithoutInitializeFirst() {
        println("receiveMessageWithoutInitializeFirst");
        this.establishContext();
        ChannelGroup group = build();
        group.beginReceive((x) -> {});
    }

    @Test(expected = ObjectDisposedException.class)
    public void receiveMessageToDisposedGroup() {
        println("receiveMessageToDisposedGroup");
        this.establishContext();
        this.configuration.dispatchOnly = true;
        ChannelGroup group = build();
        group.initialize();
        group.dispose();
        group.beginReceive((x) -> {});
    }

    @Test(expected = IllegalStateException.class)
    public void receiveMessageWithMoreThanOneCallback() {
        println("receiveMessageWithMoreThanOneCallback");
        this.establishContext();
        ChannelGroup group = build();
        group.initialize();
        group.beginReceive((x) -> {});
        group.beginReceive((x) -> {});
    }

    @Test(expected = IllegalStateException.class)
    public void receiveMessageFromDispatchOnlyGroup() {
        println("receiveMessageFromDispatchOnlyGroup");
        this.establishContext();
        this.configuration.dispatchOnly = true;
        ChannelGroup group = build();
        group.initialize();
        group.beginReceive((x) -> {});
    }

    @Test
    public void disposeChannelGroup() {
        println("disposeChannelGroup");
        this.establishContext();
        ChannelGroup group = build();
        group.dispose();
        assertEquals(1, this.workers.disposeCallCount);
    }

    @Test
    public void disposeChannelGroupMoreThanOnce() {
        println("receiveMessageThrowsAnException");
        this.establishContext();
        ChannelGroup group = build();
        group.dispose();
        group.dispose();
        assertEquals(1, this.workers.disposeCallCount);
    }

    private static void println(String test) {
        System.out.println("DefaultChannelGroupTest: " + test);
    }
}
