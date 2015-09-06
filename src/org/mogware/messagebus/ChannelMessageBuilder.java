package org.mogware.messagebus;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.mogware.system.Guid;

/**
* Provides the ability to build a channel message.
*
* Instances of this class must be designed to be multi-thread safe such that
* they can be shared between threads.
*/

public interface ChannelMessageBuilder {
    /**
    * Builds a new instance of the ChannelMessage class.
    */
    ChannelMessage build(Guid correlationId, URI returnAddress,
            Map<String, String> headers, List<Object> messages);
}
