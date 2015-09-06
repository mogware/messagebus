package org.mogware.messagebus;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mogware.system.Guid;

public class MockChannelMessage extends ChannelMessage {
    private static final Guid messageId = Guid.newGuid();
    private static final Guid correlationId = Guid.newGuid();
    private static final URI returnAddress = URI.create("http://google.com/");
    private static final Map<String, String> headers = new HashMap<>();
    private static final List<Object> messages = new ArrayList<>();

    public MockChannelMessage() {
        super(messageId, correlationId, returnAddress, headers, messages);
    }
}
