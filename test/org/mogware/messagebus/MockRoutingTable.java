package org.mogware.messagebus;

import org.mogware.system.delegates.Func1;
import org.mogware.system.delegates.Func2;

public class MockRoutingTable implements RoutingTable {
    protected int routeCallCount = 0;   
    protected boolean routeThrowsException = false; 
    protected Func2<HandlerContext, Object, Integer> routeCallback;      
    
    @Override
    public <T> void add(Class<T> clazz, MessageHandler<T> handler, int sequence) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void add(Class<T> clazz, Func1<HandlerContext, MessageHandler<T>> callback, int sequence, Class handlerType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int route(HandlerContext context, Object message) {
        this.routeCallCount++;
        if (this.routeThrowsException)
            throw new RuntimeException();
        if (this.routeCallback != null)
            return this.routeCallback.call(context, message);
        return 0;
    }
}
