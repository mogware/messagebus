package org.mogware.messagebus;

import java.net.URI;
import java.util.Map;
import org.mogware.system.Guid;
import org.mogware.system.delegates.Action1;

public class MockDispatchContext implements DispatchContext {
    protected Action1<Object> withMessageCallback;
    protected Action1<URI> withRecipientCallback; 
    protected Action1<Object[]> sendCallback;    
    
    @Override
    public int getMessageCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getHeaderCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DispatchContext withMessage(Object message) {
        if (this.withMessageCallback != null)
            this.withMessageCallback.run(message);
        return this;
    }

    @Override
    public DispatchContext withMessages(Object[] messages) {
        return this;
    }

    @Override
    public DispatchContext withCorrelationId(Guid correlationId) {
        return this;
    }

    @Override
    public DispatchContext withHeader(String key, String value) {
        return this;
    }

    @Override
    public DispatchContext withHeaders(Map<String, String> headers) {
        return this;
    }

    @Override
    public DispatchContext withRecipient(URI recipient) {
        if (this.withRecipientCallback != null)
            this.withRecipientCallback.run(recipient);
        return this;
    }

    @Override
    public DispatchContext withState(Object state) {
        return this;
    }

    @Override
    public ChannelTransaction send(Object[] messages) {
        if (this.sendCallback != null)
            this.sendCallback.run(messages);
        return null;
    }

    @Override
    public ChannelTransaction publish(Object[] messages) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChannelTransaction reply(Object[] messages) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
