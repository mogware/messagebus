package org.mogware.messagebus;

import java.net.URI;
import org.mogware.system.threading.TimeSpan;

public class MockChannelGroupConfiguration 
        implements ChannelGroupConfiguration {
    protected String groupName = "Test Channel Group";
    protected boolean synchronous = false;
    protected boolean dispatchOnly = false;
    protected int maxDispatchBuffer = 0;
    protected int minWorkers = 0;
    protected int maxWorkers = 0;
    
    @Override
    public String getGroupName() {
        return this.groupName;
    }

    @Override
    public boolean getSynchronous() {
        return this.synchronous;
    }

    @Override
    public boolean getDispatchOnly() {
        return this.dispatchOnly;
    }

    @Override
    public int getMaxDispatchBuffer() {
        return this.maxDispatchBuffer;
    }

    @Override
    public int getMinWorkers() {
        return this.minWorkers;
    }

    @Override
    public int getMaxWorkers() {
        return this.maxWorkers;
    }

    @Override
    public URI getReturnAddress() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChannelMessageBuilder getMessageBuilder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TimeSpan getReceiveTimeout() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DependencyResolver getDependencyResolver() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DispatchTable getDispatchTable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
