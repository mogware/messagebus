package org.mogware.messagebus;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mogware.system.Guid;
import org.mogware.system.threading.TimeSpan;

public class DefaultChannelMessageBuilderTest {
    private static final Guid correlationId = Guid.newGuid();
    private static final URI returnAddress = URI.create("http://google.com/");
    private static final Map<String, String> headers = new HashMap<>();
    private static final List<Object> messages = new ArrayList<>();
    static {
        headers.put("null key", null);
        headers.put("empty key", "");
        headers.put("populated key", "some value");
        messages.add(1);
        messages.add("2");
        messages.add(new BigDecimal(3.0));
        messages.add(4.0);
        messages.add(5L);
    };

    @Test
    public void buildChannelMessage() throws Exception {
        println("buildChannelMessage");
        ChannelMessage message = new DefaultChannelMessageBuilder().build(
                correlationId, returnAddress, headers, messages);
        assertEquals(correlationId, message.getCorrelationId());
        assertEquals(returnAddress, message.getReturnAddress());
        assertEquals(headers, message.getHeaders());
        assertEquals(headers.size(), message.getHeaders().size());
        assertTrue(headers.keySet().containsAll(message.getHeaders().keySet()));
        assertTrue(messages.containsAll(message.getMessages()));
        assertEquals(LocalDateTime.MAX, message.getExpiration());
        assertTrue(message.getPersistent());
    }

    @Test
    public void buildChannelMessageNoReturnAddress() throws Exception {
        println("buildChannelMessageNoReturnAddress");
        ChannelMessage message = new DefaultChannelMessageBuilder().build(
                Guid.empty, null, null, null);
        assertNull(message.getReturnAddress());
    }

    @Test
    public void buildChannelMessageWithNoHeader() throws Exception {
        println("buildChannelMessageWithNoHeader");
        ChannelMessage message = new DefaultChannelMessageBuilder().build(
                Guid.empty, null, null, null);
        assertEquals(0, message.getHeaders().size());
    }

    @Test
    public void buildChannelMessageWithNoMessages() throws Exception {
        println("buildChannelMessageWithNoMessages");
        ChannelMessage message = new DefaultChannelMessageBuilder().build(
                Guid.empty, null, null, null);
        assertEquals(0, message.getMessages().size());
    }

    @Test
    public void messageTypeMarkedAsTransient() throws Exception {
        println("messageTypeMarkedAsTransient");
        List<Object> messages = Arrays.asList(new Object[]{""});
        DefaultChannelMessageBuilder builder =
                new DefaultChannelMessageBuilder();
        builder.markAsTransient(messages.get(0).getClass());
        ChannelMessage message = builder.build(
                Guid.empty, null, null, messages);
        assertFalse(message.getPersistent());
    }

    @Test
    public void messageTypeMarkedAsExpiring() throws Exception {
        println("messageTypeMarkedAsExpiring");
        TimeSpan timeToLive = TimeSpan.fromDays(1);
        List<Object> messages = Arrays.asList(new Object[]{""});
        DefaultChannelMessageBuilder builder =
                new DefaultChannelMessageBuilder();
        builder.markAsExpiring(messages.get(0).getClass(), timeToLive);
        ChannelMessage message = builder.build(
                Guid.empty, null, null, messages);
        assertEquals(0, ChronoUnit.SECONDS.between(
                message.getExpiration(),
                LocalDateTime.now().plus(
                    Duration.ofNanos(timeToLive.getTicks() * 100)))
        );
    }

    @Test(expected = NullPointerException.class)
    public void nullTypeMarkedAsTransient() throws Exception {
        println("nullTypeMarkedAsTransient");
        new DefaultChannelMessageBuilder().markAsTransient(null);
    }

    @Test(expected = NullPointerException.class)
    public void nullTypeMarkedAsExpiring() throws Exception {
        println("nullTypeMarkedAsExpiring");
        new DefaultChannelMessageBuilder()
                .markAsExpiring(null, TimeSpan.maxValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonPositiveExpirationValue() throws Exception {
        println("nonPositiveExpirationValue");
        new DefaultChannelMessageBuilder()
                .markAsExpiring(Integer.class,TimeSpan.zero);
    }

    private static void println(String test) {
        System.out.println("DefaultChannelMessageBuilderTest: " + test);
    }
}
