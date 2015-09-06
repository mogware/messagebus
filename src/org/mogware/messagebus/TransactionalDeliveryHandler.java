package org.mogware.messagebus;

public class TransactionalDeliveryHandler implements DeliveryHandler {
    private final DeliveryHandler inner;

    public TransactionalDeliveryHandler(DeliveryHandler inner) {
        if (inner == null)
            throw new NullPointerException("inner must not be null");
        this.inner = inner;
    }

    @Override
    public void handle(DeliveryContext delivery) {
        if (delivery == null)
            throw new NullPointerException("delivery must not be null");
        this.inner.handle(delivery);
        delivery.getCurrentTransaction().commit();
    }
}
