package org.mogware.messagebus;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mogware.system.Guid;

public class DefaultChannelMessageDispatchContextTest {
    private MockMessagingChannel channel;
    private MockChannelTransaction transaction;
    private ChannelMessage message;
    private ChannelEnvelope envelope;

    private void establishContext() {
        this.channel = new MockMessagingChannel();
        this.transaction = new MockChannelTransaction();
        this.message = new MockChannelMessage();
        this.channel.channelTransaction = this.transaction;
        this.channel.sendCallback = (x) -> envelope = x;
    }

    private DefaultChannelMessageDispatchContext build() {
        return new DefaultChannelMessageDispatchContext(
                this.channel, this.message);
    }
    
    @Test(expected = NullPointerException.class)
    public void createDispatchContextWithNullChannel() {
        println("createDispatchContextWithNullChannel");
        establishContext();
        new DefaultChannelMessageDispatchContext(null, this.message);
    }

    @Test(expected = NullPointerException.class)
    public void createDispatchContextWithNullChannelMessage() {
        println("createDispatchContextWithNullChannelMessage");
        establishContext();
        new DefaultChannelMessageDispatchContext(this.channel, null);
    }

    @Test
    public void createDispatchContext() {
        println("createDispatchContext");
        establishContext();
        DefaultChannelMessageDispatchContext dispatchContext = build();
        assertEquals(1, dispatchContext.getMessageCount());
        assertEquals(0, dispatchContext.getHeaderCount());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void attemptingToAddMessage() {
        println("attemptingToAddMessage");
        establishContext();
        DefaultChannelMessageDispatchContext dispatchContext = build();
        dispatchContext.withMessage(0);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void attemptingToAddSetOfMessage() {
        println("attemptingToAddSetOfMessage");
        establishContext();
        DefaultChannelMessageDispatchContext dispatchContext = build();
        dispatchContext.withMessages(new Object[0]);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void attemptingToSetCorrelationIdentifier() {
        println("attemptingToSetCorrelationIdentifier");
        establishContext();
        DefaultChannelMessageDispatchContext dispatchContext = build();
        dispatchContext.withCorrelationId(Guid.empty);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void attemptingToSetHeader() {
        println("attemptingToSetHeader");
        establishContext();
        DefaultChannelMessageDispatchContext dispatchContext = build();
        dispatchContext.withHeader("", "");
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void attemptingToSetMultipleHeaders() {
        println("attemptingToSetMultipleHeaders");
        establishContext();
        DefaultChannelMessageDispatchContext dispatchContext = build();
        dispatchContext.withHeaders(new HashMap());
    }
    
    @Test(expected = NullPointerException.class)
    public void attemptingToAddNullRecipient() {
        println("attemptingToAddNullRecipient");
        establishContext();
        DefaultChannelMessageDispatchContext dispatchContext = build();
        dispatchContext.withRecipient(null);
    }
    
    @Test
    public void attemptingToAddRecipient() {
        println("attemptingToAddRecipient");
        establishContext();
        DefaultChannelMessageDispatchContext dispatchContext = build();
        DispatchContext returnedContext =
            dispatchContext.withRecipient(ChannelEnvelope.loopbackAddress);
        assertEquals(dispatchContext, returnedContext);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void attemptingToAddNullState() {
        println("attemptingToAddNullState");
        establishContext();
        DefaultChannelMessageDispatchContext dispatchContext = build();
        dispatchContext.withState(null);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void attemptingToAddState() {
        println("attemptingToAddState");
        establishContext();
        DefaultChannelMessageDispatchContext dispatchContext = build();
        dispatchContext.withState("");
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void publishingTheDispatch() {
        println("publishingTheDispatch");
        establishContext();
        DefaultChannelMessageDispatchContext dispatchContext = build();
        dispatchContext.publish(null);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void replyingTheDispatch() {
        println("replyingTheDispatch");
        establishContext();
        DefaultChannelMessageDispatchContext dispatchContext = build();
        dispatchContext.reply(null);
    }
    
    @Test
    public void sendingTheDispatch() {
        println("sendingTheDispatch");
        establishContext();
        List<URI> recipients = Arrays.asList(
                URI.create("http://first"), URI.create("http://second"));
        DefaultChannelMessageDispatchContext dispatchContext = build();
        recipients.stream().forEach((x) -> dispatchContext.withRecipient(x));        
        ChannelTransaction transaction = dispatchContext.send(null);
        assertEquals(this.message, envelope.getMessage());
        assertTrue(recipients.containsAll(envelope.getRecipients()));
        assertEquals(message, envelope.getState());
        assertEquals(transaction, this.transaction);
        assertEquals(0, dispatchContext.getMessageCount());
    }
    
    @Test(expected = IllegalStateException.class)
    public void sendingTheDispatchMultipleTimes() {
        println("sendingTheDispatch");
        establishContext();
        DefaultChannelMessageDispatchContext dispatchContext = build();
        dispatchContext.withRecipient(URI.create("http://first")).send(null);
        dispatchContext.send(null);
    }
    
    private static void println(String test) {
        System.out.println("DefaultChannelMessageDispatchContextTest: " + test);
    }
}
