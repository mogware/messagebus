package org.mogware.messagebus.channels;

import org.mogware.messagebus.ChannelEnvelope;
import org.mogware.messagebus.ChannelGroupConfiguration;
import org.mogware.messagebus.ChannelMessage;
import org.mogware.messagebus.ChannelTransaction;
import org.mogware.messagebus.DeliveryContext;
import org.mogware.messagebus.DependencyResolver;
import org.mogware.messagebus.DispatchContext;
import org.mogware.messagebus.ExtensionMethods;
import static org.mogware.messagebus.ExtensionMethods.tryDispose;
import org.mogware.messagebus.MessagingChannel;
import org.mogware.system.delegates.Action1;

public class DependencyResolverChannel implements MessagingChannel {
    private final MessagingChannel channel;
    private final DependencyResolver resolver;
    private DependencyResolver currentResolver;
    private DeliveryContext currentContext;

    public DependencyResolverChannel(MessagingChannel channel, DependencyResolver resolver) {
        if (channel == null)
            throw new NullPointerException("channel must not be null");
        if (resolver == null)
            throw new NullPointerException("resolver must not be null");
        this.channel = channel;
        this.resolver = resolver;
    }

    @Override
    public void beginShutdown() {
        this.channel.beginShutdown();
    }

    @Override
    public void receive(Action1<DeliveryContext> callback) {
        this.channel.receive((context) -> this.receive(context, callback));
    }

    protected void receive(DeliveryContext context,
            Action1<DeliveryContext> callback) {
        try {
            this.currentContext = context;
            this.currentResolver = this.resolver.createNestedResolver();
            callback.run(this);
        } catch (Exception ex) {
        } finally {
            tryDispose(this.currentResolver, false);
            this.currentResolver = null;
            this.currentContext = null;
        }
    }

    @Override
    public void send(ChannelEnvelope envelope) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getActive() {
        return this.getCurrentContext().getActive();
    }

    @Override
    public ChannelMessage getCurrentMessage() {
        return this.getCurrentContext().getCurrentMessage();
    }

    @Override
    public DependencyResolver getCurrentResolver() {
        return this.currentResolver != null ?
                this.currentResolver : this.resolver;
    }

    @Override
    public ChannelTransaction getCurrentTransaction() {
        return this.getCurrentContext().getCurrentTransaction();
    }

    @Override
    public ChannelGroupConfiguration getCurrentConfiguration() {
        return this.getCurrentContext().getCurrentConfiguration();
    }

    protected DeliveryContext getCurrentContext() {
        return this.currentContext != null ? this.currentContext : this.channel;
    }

    @Override
    public DispatchContext prepareDispatch(Object message,
            MessagingChannel actual) {
        return this.getCurrentContext().prepareDispatch(message,
                actual != null ? actual : this);
    }

    @Override
    public void dispose() {
        tryDispose(this.channel, false);
        tryDispose(this.resolver, false);
    }
}
