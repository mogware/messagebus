package org.mogware.messagebus;

import org.mogware.system.delegates.Func1;

/**
* Provides the ability to route a given message to one or more registered
* handlers. In addition, multiple routing tables can be used by an IoC-managed
* application to have different routes depending upon which table is resolved
* for a given incoming message.
* 
* Instances of this class must be designed to be multi-thread safe such that
* they can be shared between threads. At the same time, routes should only be
* added to instances of this class during wireup rather than at runtime.
*/

public interface RoutingTable {
    /**
    * Adds a route to the handler provided using the optional sequence specified. Adding the same handler multiple times
    * will result in the most recent registration being used.
    */
    <T>void add(Class<T> clazz, MessageHandler<T> handler, int sequence);

    /**
    * Adds a route to the handler provided using the optional sequence specified. When the handler type is specified, adding
    * the same handler multiple times will result in the most recent registration being used.
    */
    <T>void add(Class<T> clazz, Func1<HandlerContext, 
            MessageHandler<T>> callback, int sequence, Class handlerType);

    /**
    * Routes the message provided to the associated message handlers.
    */
    int route(HandlerContext context, Object message);
}
