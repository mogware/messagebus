package org.mogware.messagebus;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mogware.system.Guid;

public class DefaultChannelMessageHandlerTest {
    private MockHandlerContext handlerContext;
    private MockDispatchContext dispatchContext;
    private MockDeliveryContext delivery;
    private MockRoutingTable routes;
    private ChannelMessage sentMessage;
    private URI[] recipients;
    private List<URI> queuedRecipients;
    private ChannelMessage queuedMessage;

    private void establishContext() {
        this.sentMessage = null;
        this.recipients = null;
        this.queuedMessage = null;
        this.queuedRecipients = new ArrayList<>();
        this.handlerContext = new MockHandlerContext();
        this.dispatchContext = new MockDispatchContext();
        this.delivery = new MockDeliveryContext();
        this.routes = new MockRoutingTable();
        this.handlerContext.dispatchContext = this.dispatchContext;
        this.dispatchContext.withMessageCallback =
                (x) -> this.queuedMessage = (ChannelMessage) x;
        this.dispatchContext.withRecipientCallback =
                (x) -> this.queuedRecipients.add(x);
        this.dispatchContext.sendCallback = (x) -> {
            this.sentMessage = this.queuedMessage;
            this.recipients = this.queuedRecipients.toArray(new URI[]{});
        };
    }

    private DefaultChannelMessageHandler build() {
        return new DefaultChannelMessageHandler(
                this.handlerContext, this.routes);
    }

    private ChannelMessage buildMesssage(List<Object> messages) {
        return new ChannelMessage(
                Guid.newGuid(),
                Guid.newGuid(),
                URI.create("http://localhost"),
                new HashMap<>(),
                messages
        );
    }

    @Test(expected = NullPointerException.class)
    public void createMessageHandlerWithNullContext() {
        println("createMessageHandlerWithNullContext");
        establishContext();
        new DefaultChannelMessageHandler(null, this.routes);
    }

    @Test(expected = NullPointerException.class)
    public void createMessageHandlerWithNullRoutingTable() {
        println("createMessageHandlerWithNullRoutingTable");
        establishContext();
        new DefaultChannelMessageHandler(this.handlerContext, null);
    }

    @Test
    public void handlingMessage() {
        println("handlingMessage");
        establishContext();
        ChannelMessage deliveredMessage = this.buildMesssage(
                Arrays.asList(new Object[] { "1", 2, 3.0 })
        );
        DefaultChannelMessageHandler handler = this.build();
        handler.handle(deliveredMessage);
        assertEquals(3, this.routes.routeCallCount);
        assertEquals(-1, deliveredMessage.getActiveIndex());
    }

    @Test
    public void handlingMessageThrowsException() {
        println("handlingMessageThrowsException");
        establishContext();
        this.routes.routeThrowsException = true;
        ChannelMessage deliveredMessage = this.buildMesssage(
                Arrays.asList(new Object[] { "1", 2, 3.0 })
        );
        DefaultChannelMessageHandler handler = this.build();
        handler.handle(deliveredMessage);
        assertEquals(-1, deliveredMessage.getActiveIndex());
    }

    @Test
    public void handlingMessageWhenDiscontinued() {
        println("handlingMessageWhenDiscontinued");
        establishContext();
        this.routes.routeCallback = (ctx, msg) -> {
                this.handlerContext.continueHandling = false;
                return 0;
        };
        ChannelMessage deliveredMessage = this.buildMesssage(
                Arrays.asList(new Object[] { "1", 2 })
        );
        DefaultChannelMessageHandler handler = this.build();
        handler.handle(deliveredMessage);
        assertEquals(1, this.routes.routeCallCount);
        assertEquals(-1, deliveredMessage.getActiveIndex());
    }

    @Test
    public void noHandlersExistForAnyMessage() {
        println("noHandlersExistForAnyMessage");
        establishContext();
        ChannelMessage deliveredMessage = this.buildMesssage(
                Arrays.asList(new Object[] { 0 })
        );
        DefaultChannelMessageHandler handler = this.build();
        handler.handle(deliveredMessage);
        assertEquals(deliveredMessage, this.sentMessage);
        assertEquals(ChannelEnvelope.unhandledMessageAddress,
                this.recipients[0]);
        assertEquals(-1, deliveredMessage.getActiveIndex());
    }

    @Test
    public void instructedNotToContinueProcessing() {
        println("instructedNotToContinueProcessing");
        establishContext();
        this.handlerContext.continueHandling = false;
        ChannelMessage deliveredMessage = this.buildMesssage(
                Arrays.asList(new Object[] { 0 })
        );
        DefaultChannelMessageHandler handler = this.build();
        handler.handle(deliveredMessage);
        assertNull(this.sentMessage);
        assertEquals(-1, deliveredMessage.getActiveIndex());
    }

    @Test
    public void specificMessagesNotHandled() {
        println("specificMessagesNotHandled");
        establishContext();
        this.routes.routeCallback = (ctx, msg) -> {
                if (msg.equals(1))
                    return 1; // message is handled
                if (msg.equals(3))
                    return 1; // message is handled
                return 0;
        };
        ChannelMessage deliveredMessage = this.buildMesssage(
                Arrays.asList(new Object[] { 1, 2, 3, 4 })
        );
        DefaultChannelMessageHandler handler = this.build();
        handler.handle(deliveredMessage);
        assertTrue(Arrays.asList(new Object[] { 2, 4 }).containsAll(
                sentMessage.getMessages())
        );
        assertEquals(deliveredMessage.getCorrelationId(),
                sentMessage.getCorrelationId());
        assertEquals(deliveredMessage.getReturnAddress(),
                sentMessage.getReturnAddress());
        assertEquals(deliveredMessage.getHeaders(), sentMessage.getHeaders());
        assertEquals(ChannelEnvelope.unhandledMessageAddress,
                this.recipients[0]);
        assertEquals(-1, deliveredMessage.getActiveIndex());
    }

    private static void println(String test) {
        System.out.println("DefaultChannelMessageHandlerTest: " + test);
    }
}
