package org.mogware.messagebus;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mogware.system.Guid;
import org.mogware.system.threading.TimeSpan;

public class DefaultChannelMessageBuilder implements ChannelMessageBuilder {
    private final Map<Class, TimeSpan> expirations = new HashMap<>();
    private final Set<Class> transients = new HashSet<>();

    public void markAsTransient(Class messageType) throws Exception {
        if (messageType == null)
            throw new NullPointerException("messageType must not be null");
        this.transients.add(messageType);
    }

    public void markAsExpiring(Class messageType, TimeSpan timeToLive) {
        if (messageType == null)
            throw new NullPointerException("messageType must not be null");
        if (TimeSpan.operatorLessEqual(timeToLive, TimeSpan.zero))
            throw new IllegalArgumentException("timeToLive must be positive");
        this.expirations.put(messageType, timeToLive);
    }

    @Override
    public ChannelMessage build(Guid correlationId, URI returnAddress,
            Map<String,String> headers, List<Object> messages) {
        ChannelMessage message = new ChannelMessage(Guid.newGuid(),
                correlationId, returnAddress, headers, messages);
        message.setExpiration(LocalDateTime.MAX);
        message.setPersistent(true);
        Class primaryType = message.getMessages().size() > 0 ?
                message.getMessages().get(0).getClass() : null;
        if (primaryType == null)
            return message;
        message.setPersistent(!this.transients.contains(primaryType));
        if (this.expirations.containsKey(primaryType)) {
            TimeSpan timeToLive = this.expirations.get(primaryType);
            message.setExpiration(LocalDateTime.now().plus(
                    Duration.ofNanos(timeToLive.getTicks() * 100)));
        }
        return message;
    }
}
