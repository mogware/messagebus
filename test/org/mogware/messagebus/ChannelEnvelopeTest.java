package org.mogware.messagebus;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mogware.system.Guid;

public class ChannelEnvelopeTest {
    private static final Guid state = Guid.newGuid();
    private static final ChannelMessage message = new ChannelMessage(
        Guid.newGuid(), Guid.newGuid(), URI.create("mq:/return"), null, null
    );
    private static final Set<URI> recipients = new HashSet<>();
    static {
        recipients.add(ChannelEnvelope.loopbackAddress);
        recipients.add(URI.create("mq:/testing"));
        recipients.add(null);
    }

    @Test
    public void createChannelEnvelope() {
        println("createChannelEnvelope");
        ChannelEnvelope envelope = new ChannelEnvelope(message,
                recipients, state);
        assertEquals(message, envelope.getMessage());
        assertTrue(envelope.getRecipients().size() == 2);
        assertEquals(state, envelope.getState());
    }

    @Test(expected = NullPointerException.class)
    public void createChannelEnvelopeWithoutMessage() {
        println("createChannelEnvelopeWithoutMessage");
        ChannelEnvelope envelope = new ChannelEnvelope(null, recipients, state);
    }

    @Test(expected = NullPointerException.class)
    public void createChannelEnvelopeWithoutRecipients() {
        println("createChannelEnvelopeWithoutRecipients");
        ChannelEnvelope envelope = new ChannelEnvelope(message, null, state);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createChannelEnvelopeWithEmptySetOfRecipients() {
        println("createChannelEnvelopeWithEmptySetOfRecipients");
        ChannelEnvelope envelope = new ChannelEnvelope(message,
                new HashSet<URI>(), state);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void attemptToModifyRecipientCollection() {
        println("attemptToModifyRecipientCollection");
        ChannelEnvelope envelope = new ChannelEnvelope(message,
                recipients, state);
        envelope.getRecipients().clear();
    }

    private static void println(String test) {
        System.out.println("ChannelEnvelopeTest: " + test);
    }
}
