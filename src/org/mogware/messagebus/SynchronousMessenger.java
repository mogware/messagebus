package org.mogware.messagebus;

import java.util.Map;

public class SynchronousMessenger implements Messenger {
    private final MessagingChannel channel;

    public SynchronousMessenger(MessagingChannel channel) {
        this.channel = channel;
    }

    @Override
    public void dispatch(Object message, Map<String, String> headers,
            Object state) {
        DispatchContext dispatch = this.channel.prepareDispatch(message, null);
        if (headers != null)
            dispatch.withHeaders(headers);
        if (state != null)
            dispatch.withState(state);
        dispatch.send(null);
    }

    @Override
    public void commit() {
        this.channel.getCurrentTransaction().commit();
    }

    @Override
    public void dispose() {
        this.channel.dispose();
    }
}
