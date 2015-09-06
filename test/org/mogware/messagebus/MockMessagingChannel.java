package org.mogware.messagebus;

import org.mogware.system.delegates.Action1;

public class MockMessagingChannel implements MessagingChannel {
    protected int prepareDispatchCallCount = 0;    
    protected boolean prepareDispatchThrowsException = false;
    protected int receiveCallCount = 0;        
    protected Action1<Action1<DeliveryContext>> receiveCallback;
    protected int disposeCallCount = 0;
    protected ChannelTransaction channelTransaction;
    protected Action1<ChannelEnvelope> sendCallback;
    
    @Override
    public void beginShutdown() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void receive(Action1<DeliveryContext> callback) {
        if (this.receiveCallback != null)
            this.receiveCallback.run(callback);
        this.receiveCallCount++;
    }

    @Override
    public void send(ChannelEnvelope envelope) {
        if (this.sendCallback != null)
            this.sendCallback.run(envelope);
    }

    @Override
    public boolean getActive() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChannelMessage getCurrentMessage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DependencyResolver getCurrentResolver() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChannelTransaction getCurrentTransaction() {
        return this.channelTransaction;
    }

    @Override
    public ChannelGroupConfiguration getCurrentConfiguration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DispatchContext prepareDispatch(Object message, MessagingChannel channel) {
        prepareDispatchCallCount++;
        if (this.prepareDispatchThrowsException)
            throw new ChannelConnectionException();        
        return null;
    }

    @Override
    public void dispose() {
        this.disposeCallCount++;
    }
}
