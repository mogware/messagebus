package org.mogware.messagebus;

public class DefaultDeliveryHandler implements DeliveryHandler {
    private final RoutingTable routingTable;

    public DefaultDeliveryHandler(RoutingTable routingTable) {
        if (routingTable == null)
            throw new NullPointerException("routingTable must not be null");
        this.routingTable = routingTable;
    }

    @Override
    public void handle(DeliveryContext delivery) {
        DefaultHandlerContext context = new DefaultHandlerContext(delivery);
        this.routingTable.route(context, delivery.getCurrentMessage());
        context.dispose();
    }
}
