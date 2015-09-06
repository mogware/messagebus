package org.mogware.messagebus;

public class MockChannelConnector implements ChannelConnector {
    protected MessagingChannel channel = null;
    protected boolean connectThrowsException = false;
    protected int connectCallCount = 0;
    protected int disposeCallCount = 0;
    
    @Override
    public ConnectionState getCurrentState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterable<ChannelGroupConfiguration> getChannelGroups() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MessagingChannel connect(String channelGroup) {
        this.connectCallCount++;
        if (this.connectThrowsException)
            throw new ChannelConnectionException();
        return this.channel;
    }

    @Override
    public void dispose() {
        this.disposeCallCount++;
    }
}
