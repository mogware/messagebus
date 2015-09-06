package org.mogware.messagebus;

public class MockDeliveryContext implements DeliveryContext {

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
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
