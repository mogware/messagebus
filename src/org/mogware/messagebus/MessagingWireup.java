package org.mogware.messagebus;

import java.util.ArrayList;
import java.util.List;
import org.mogware.messagebus.channels.DependencyResolverConnector;
import org.mogware.messagebus.channels.PooledDispatchConnector;
import org.mogware.system.delegates.Func1;

public class MessagingWireup {
    private final List<ChannelConnector> connectors = new ArrayList<>();
    private Func1<DeliveryHandler, DeliveryHandler> handlerCallback;

    public MessagingWireup addConnector(ChannelConnector connector) {
        if (connector == null)
            throw new NullPointerException("inner must not be null");
        if (!(connector instanceof PooledDispatchConnector))
            connector = new PooledDispatchConnector(connector);
        if (!(connector instanceof DependencyResolverConnector))
            connector = new DependencyResolverConnector(connector);
        this.connectors.add(connector);
        return this;
    }

    public MessagingWireup addConnectors(
            Iterable<ChannelConnector> connectors) {
        for (ChannelConnector connector: connectors)
            this.addConnector(connector);
        return this;
    }

    public MessagingWireup withDeliveryHandler(
            Func1<DeliveryHandler, DeliveryHandler> callback) {
        this.handlerCallback = callback;
        return this;
    }

    public MessagingHost start() {
        return this.startHost();
    }

    public MessagingHost startWithReceive(RoutingTable table,
            Func1<HandlerContext, MessageHandler<ChannelMessage>> handler) {
        if (handler == null)
            handler = (ctx) -> new DefaultChannelMessageHandler(ctx, table);
        table.add(ChannelMessage.class, handler, 0,
                DefaultChannelMessageHandler.class);
        MessagingHost host = this.startHost();
        host.beginReceive((ctx) -> this.buildDeliveryChain(table).handle(ctx));
        return host;
    }

    protected DeliveryHandler buildDeliveryChain(RoutingTable table) {
        DeliveryHandler handler = new DefaultDeliveryHandler(table);
        if (this.handlerCallback != null) {
            DeliveryHandler handler2 = this.handlerCallback.call(handler);
            if (handler2 != null)
                handler = handler2;
        }
        return new TransactionalDeliveryHandler(handler);
    }

    protected MessagingHost startHost() {
        this.auditConnection();
        MessagingHost host = new DefaultMessagingHost(this.connectors,
            (ctor, cfg) -> new DefaultChannelGroupFactory().build(ctor, cfg));
        host.initialize();
        return host;
    }

    protected void auditConnection() {
    }
}
