package org.mogware.messagebus;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mogware.system.Guid;

public class ChannelMessageTest {
    private static final Guid messageId = Guid.newGuid();
    private static final Guid correlationId = Guid.newGuid();
    private static final URI returnAddress = URI.create("http://google.com/");
    private static final Map<String, String> headers = new HashMap<>();
    private static final List<Object> messages = new ArrayList<>();
    static {
        messages.add(1);
        messages.add("2");
        messages.add(new BigDecimal(3.0));
        messages.add(4.0);
        messages.add(5L);
    };

    private static ChannelMessage newChannelMessage() {
        return new ChannelMessage(
                messageId,
                correlationId,
                returnAddress,
                headers,
                messages
        );
    }

    @Test
    public void createChannelMessage() {
        println("createChannelMessage");
        ChannelMessage message = newChannelMessage();
        assertEquals(messageId, message.getMessageId());
        assertEquals(correlationId, message.getCorrelationId());
        assertEquals(returnAddress, message.getReturnAddress());
        assertEquals(headers, message.getHeaders());
        assertTrue(message.getMessages().containsAll(messages));
        assertNull(message.getActiveMessage());
        assertEquals(-1, message.getActiveIndex());
        assertEquals(LocalDateTime.MIN, message.getExpiration());
        assertFalse(message.getPersistent());
        assertEquals(LocalDateTime.MIN, message.getDispatched());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void attemptToModifyMessagesCollection() {
        println("attemptToModifyMessagesCollection");
        ChannelMessage message = newChannelMessage();
        message.getMessages().clear();
    }

    @Test
    public void requestingNextMessage() {
        println("requestingNextMessage");
        ChannelMessage message = newChannelMessage();
        assertTrue(message.moveNext());
        assertEquals(messages.get(0), message.getActiveMessage());
        assertEquals(0, message.getActiveIndex());
    }

    @Test
    public void noMoreMessagesAreAvailable() {
        println("noMoreMessagesAreAvailable");
        ChannelMessage message = newChannelMessage();
        while (message.moveNext()) {}
        assertNull(message.getActiveMessage());
        assertEquals(-1, message.getActiveIndex());
    }

    private static void println(String test) {
        System.out.println("ChannelMessageTest: " + test);
    }
}
