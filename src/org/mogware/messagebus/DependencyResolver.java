package org.mogware.messagebus;

import org.mogware.system.Disposable;

/**
* Provides the ability to resolve dependencies from within user code.
*/

public interface DependencyResolver extends Disposable {
    /**
    * Gets a reference to the actual IoC container used to resolve dependencies.
    * The type of IoC container.
    */
    <T>T as(Class<T> type);

    /**
    * Instructs the container to create a nested or child instance.
    */
    DependencyResolver createNestedResolver();
}
