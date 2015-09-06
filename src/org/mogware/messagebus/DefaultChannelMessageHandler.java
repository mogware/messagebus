package org.mogware.messagebus;

import java.util.ArrayList;
import java.util.List;
import org.mogware.system.Guid;

public class DefaultChannelMessageHandler
        implements MessageHandler<ChannelMessage>{
    private final HandlerContext context;
    private final RoutingTable routes;

    public DefaultChannelMessageHandler(HandlerContext context,
            RoutingTable routes) {
        if (context == null)
            throw new NullPointerException("context must not be null");
        if (routes == null)
            throw new NullPointerException("routes must not be null");
        this.context = context;
        this.routes = routes;
    }

    @Override
    public void handle(ChannelMessage message) {
        List<Object> unhandled = new ArrayList<>(message.getMessages().size());
        try {
            int handled = 0;
            while (message.moveNext() && this.context.getContinueHandling())
                handled += this.route(message.getActiveMessage(), unhandled);
            if (handled == 0)
                unhandled.clear();
            if (this.context.getContinueHandling() &&
                    (handled == 0 || unhandled.size() > 0))
                this.forwardToUnhandledAddress(message, unhandled);
        } catch (Exception ex) {
        } finally {
            message.reset();
        }
    }

    private int route(Object message, List<Object> unhandled) {
        int count = this.routes.route(this.context, message);
        if (count == 0)
            unhandled.add(message);
        return count;
    }

    protected void forwardToUnhandledAddress(ChannelMessage message,
            List<Object> messages) {
        if (messages.size() != 0)
            message = new ChannelMessage(
                    Guid.newGuid(),
                    message.getCorrelationId(),
                    message.getReturnAddress(),
                    message.getHeaders(),
                    messages);
        this.context.prepareDispatch(null, null)
                .withMessage(message)
                .withRecipient(ChannelEnvelope.unhandledMessageAddress)
                .send(null);
    }
}
