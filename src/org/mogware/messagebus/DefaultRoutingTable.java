package org.mogware.messagebus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.mogware.system.delegates.Func1;

public class DefaultRoutingTable implements RoutingTable {
    private final Set<Class> registeredHandlers = new HashSet<>();
    private final Map<Class, List<SequencedHandler>> registeredRoutes =
            new HashMap<>();

    @Override
    public <T> void add(Class<T> routeType, MessageHandler<T> handler,
            int sequence) {
        if (handler == null)
            throw new NullPointerException("handler must not be null");
        this.<T>add(
            routeType,
            new SimpleHandler<>(handler, sequence),
            handler.getClass()
        );
    }

    @Override
    public <T> void add(Class<T> routeType,
            Func1<HandlerContext, MessageHandler<T>> callback,
            int sequence, Class handlerType) {
        if (callback == null)
            throw new NullPointerException("callback must not be null");
        this.<T>add(
            routeType,
            new CallbackHandler<>(callback, sequence, handlerType),
            handlerType
        );
    }

    private <T>void add(Class<T> routeType, SequencedHandler handler,
            Class handlerType) {
        List<SequencedHandler> routes =
            this.registeredRoutes.containsKey(routeType) ?
                this.registeredRoutes.get(routeType) : new ArrayList<>();
        if (this.registeredHandlers.contains(handlerType)) {
            for (int i = 0; i < routes.size(); i++) {
                if (routes.get(i).getHandlerType() == handlerType)
                    routes.set(i, handler);
            }
        }
        else
            routes.add(handler);
        if (handlerType != null)
            this.registeredHandlers.add(handlerType);
        this.registeredRoutes.put(routeType, routes.stream()
                .sorted((c1, c2) -> Integer.compare(
                        c1.getSequence(), c2.getSequence()))                
                .collect(Collectors.toList())               
        );
    }

    @Override
    public int route(HandlerContext context, Object message) {
        if (context == null)
            throw new NullPointerException("context must not be null");
        if (context == null)
            throw new NullPointerException("context must not be null");
        Class routeType = message.getClass();
        if (!this.registeredRoutes.containsKey(routeType))
            return 0;
        int count = 0;
        for (SequencedHandler route: this.registeredRoutes.get(routeType)) {
            if (! context.getContinueHandling())
                break;
            if (tryRoute(route, context, message))
                count++;
        }
        return count;
    }

    // FUTURE: route to handlers for message base classes and interfaces all
    // the way back to Object
    private static boolean tryRoute(SequencedHandler route,
            HandlerContext context, Object message) {
        try {
            return route.handle(context, message);
        } catch (AbortCurrentHandlerException ex) {
            return true;
        }
    }

    private interface SequencedHandler {
        int getSequence();
        Class getHandlerType();
        boolean handle(HandlerContext context, Object message);
    }

    private static class SimpleHandler<T> implements SequencedHandler {
        private int sequence;
        private Class handlerType;
        private final MessageHandler<T> handler;

        @Override
        public int getSequence() {
            return this.sequence;
        }

        public void setSequence(int value) {
            this.sequence = value;
        }

        @Override
        public Class getHandlerType() {
            return this.handlerType;
        }

        public void setHandlerType(Class value) {
            this.handlerType = value;
        }

        @Override
        public boolean handle(HandlerContext context, Object message) {
            this.handler.handle((T)message);
            return true;
        }

        public SimpleHandler(MessageHandler<T> handler, int sequence) {
            this.handler = handler;
            this.setHandlerType(handler.getClass());
            this.setSequence(sequence);
        }
    }

    private static class CallbackHandler<T> implements SequencedHandler {
        private int sequence;
        private Class handlerType;
        private final Func1<HandlerContext, MessageHandler<T>> callback;

        @Override
        public int getSequence() {
            return this.sequence;
        }

        public void setSequence(int value) {
            this.sequence = value;
        }

        @Override
        public Class getHandlerType() {
            return this.handlerType;
        }

        public void setHandlerType(Class value) {
            this.handlerType = value;
        }

        @Override
        public boolean handle(HandlerContext context, Object message) {
            MessageHandler<T> handler = this.callback.call(context);
            if (handler == null)
                return false;
            handler.handle((T)message);
            return true;
        }

        public CallbackHandler(
                Func1<HandlerContext, MessageHandler<T>> callback,
                int sequence, Class handlerType) {
            this.callback = callback;
            this.setHandlerType(handlerType);
            this.setSequence(sequence);
        }
    }
}
