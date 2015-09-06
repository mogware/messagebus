package org.mogware.messagebus;

import org.mogware.system.delegates.Action1;

public class MockChannelTransaction implements ChannelTransaction {

    @Override
    public boolean getFinished() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void register(Action1 callback) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void commit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rollback() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
