
package org.mogware.messagebus;

import java.net.URI;
import java.util.Date;
import java.util.List;

/**
* Provides the ability to determine the set of recipients (by address) for a
* given type of message.
*
* Instances of this class must be designed to be multi-thread safe such that
* they can be shared between threads.
*/

public interface DispatchTable {
    /**
    * Gets the set of recipients of the message type specified.
    */
    List<URI> getUri(Class messageType);

    /**
    * Adds the subscriber to the set of subscribers for the specified message type.
    */
    void addSubscriber(URI subscriber, Class messageType, Date expiration);

    /**
    * Adds a recipient to the set of recipients for the specified message type.
    * Recipients are used when "sending" a message.
    */
    void addRecipient(URI recipient, Class messageType);

    /**
    * Removes the subscriber or recipient from the set of subscribers for the specified message type.
    */
    void remove(URI address, Class messageType);
}
