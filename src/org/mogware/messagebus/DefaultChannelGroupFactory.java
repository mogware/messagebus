package org.mogware.messagebus;

public class DefaultChannelGroupFactory {
    public ChannelGroup build(ChannelConnector connector,
            ChannelGroupConfiguration configuration) {
        if (connector == null)
            throw new NullPointerException("connector must not be null");
        if (configuration == null)
            throw new NullPointerException("configuration must not be null");
        if (configuration.getSynchronous())
            return new SynchronousChannelGroup(connector,configuration);
        TaskWorkerGroup<MessagingChannel> workers =
                new TaskWorkerGroup<MessagingChannel>(
                        configuration.getMinWorkers(),
                        configuration.getMaxWorkers(),
                        configuration.getMaxDispatchBuffer()
                );
        return new DefaultChannelGroup(connector,configuration,workers);
    }
}
