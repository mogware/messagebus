package org.mogware.messagebus;

import org.junit.Test;
import static org.junit.Assert.*;

public class DefaultChannelGroupFactoryTest {
    private MockChannelGroupConfiguration configuration;
    private MockChannelConnector connector;
    private DefaultChannelGroupFactory factory;

    private void establishContext() {
        this.configuration = new MockChannelGroupConfiguration();
        this.connector = new MockChannelConnector();
        this.factory = new DefaultChannelGroupFactory();
        this.configuration.minWorkers = 1;
        this.configuration.maxWorkers = 1;
        this.configuration.maxDispatchBuffer = 5;
    }

    @Test(expected = NullPointerException.class)
    public void buildingChannelWithNullConnector() {
        println("buildingChannelWithNullConnector");
        this.establishContext();
        ChannelGroup group = this.factory.build(null, this.configuration);
    }

    @Test(expected = NullPointerException.class)
    public void buildingChannelWithNullConfiguration() {
        println("buildingChannelWithNullConfiguration");
        this.establishContext();
        ChannelGroup group = this.factory.build(this.connector, null);
    }

    @Test
    public void buildingAsynchronousChannel() {
        println("buildingAsynchronousChannel");
        this.establishContext();
        this.configuration.synchronous = false;
        ChannelGroup group = factory.build(this.connector, this.configuration);
        assertTrue(group instanceof DefaultChannelGroup);
    }

    @Test
    public void buildingSynchronousChannel() {
        println("buildingSynchronousChannel");
        this.establishContext();
        this.configuration.synchronous = true;
        ChannelGroup group = factory.build(this.connector, this.configuration);
        assertTrue(group instanceof SynchronousChannelGroup);
    }

    private static void println(String test) {
        System.out.println("DefaultChannelGroupFactoryTest: " + test);
    }
}
