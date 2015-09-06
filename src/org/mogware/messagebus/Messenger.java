package org.mogware.messagebus;

import java.util.Map;
import org.mogware.system.Disposable;

public interface Messenger extends Disposable {
    void dispatch(Object message, Map<String,String> headers, Object state);
    void commit();
}
