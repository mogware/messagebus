package org.mogware.messagebus;

import org.mogware.system.Disposable;
import org.mogware.system.Guid;

public final class ExtensionMethods {
    private ExtensionMethods() {
    }

    public static void tryDispose(Disposable resource, boolean rethrow) {
        if (resource == null)
            return;
        try {
            resource.dispose();
        } catch (Exception ex) {
            if (rethrow)
                throw ex;
        }
    }
}
