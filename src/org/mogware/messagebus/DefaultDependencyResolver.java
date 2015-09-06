package org.mogware.messagebus;

import static org.mogware.messagebus.ExtensionMethods.tryDispose;
import org.mogware.system.Disposable;
import org.mogware.system.delegates.Func2;

public class DefaultDependencyResolver <T extends Disposable>
        implements DependencyResolver {
    private final T container;
    private final Func2<T, Integer, T> create;
    private final int depth;
    private final boolean disposable;

    public DefaultDependencyResolver(T container, Func2<T, Integer, T> create) {
        this(container, create, 0, true);
    }

    private DefaultDependencyResolver(T container, Func2<T, Integer, T> create,
            int depth, boolean disposable) {
        this.container = container;
        this.depth = depth;
        this.create = create;
        this.disposable = disposable;
    }

    @Override
    public <TActual> TActual as(Class<TActual> type) {
        try {
            return type.cast(this.container);
        } catch (ClassCastException ex) {
            return null;
        }
    }

    @Override
    public DependencyResolver createNestedResolver() {
        if (this.create == null)
            return new DefaultDependencyResolver<>(this.container,
                    this.create, this.depth + 1, false);
        T inner = this.create.call(this.container, this.depth + 1);
        if (inner == null)
            return new DefaultDependencyResolver<>(this.container,
                    this.create, this.depth + 1, false);
        return new DefaultDependencyResolver<>(inner, this.create,
                this.depth + 1, true);
    }


    @Override
    public void dispose() {
        if (this.disposable)
            tryDispose(this.container, false);
    }
}
