package org.mogware.messagebus;

import java.net.URI;

public class MockHandlerContext implements HandlerContext {
    protected boolean continueHandling = true;
    protected DispatchContext dispatchContext;
    
    @Override
    public boolean getContinueHandling() {
        return this.continueHandling;
    }

    @Override
    public void dropMessage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deferMessage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void forwardMessage(Iterable<URI> recipients) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChannelGroupConfiguration getCurrentConfiguration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DispatchContext prepareDispatch(Object message, MessagingChannel channel) {
        return this.dispatchContext;
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
