package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IPluginActivator
import cz.cuni.mff.d3s.been.pluger.IServiceRegistrator
import spock.lang.Specification

class PluginActivatorLoaderTest extends Specification {

    def 'test load activators'() {
        given:
            def loader = new PluginActivatorLoader()
            def descriptors = [
                    new PluginDescriptor(activator: DummyActivator1.name),
                    new PluginDescriptor(activator: DummyActivator2.name),
                    new PluginDescriptor(activator: DummyActivator3.name),
            ]
            def pluginClassloader = getClass().classLoader

        when:
            def activators = loader.loadActivators(descriptors, pluginClassloader)

        then:
            assert activators*.class.name.sort() == [
                    DummyActivator1.name,
                    DummyActivator2.name,
                    DummyActivator3.name,
            ].sort()
    }

    private abstract static class AbstractDummyTestActivator implements IPluginActivator {
        @Override
        void activate(IServiceRegistrator registry) {}
        @Override
        void initialize() {}
        @Override
        void start() {}
    }

    private static final class DummyActivator1 extends AbstractDummyTestActivator {}

    private static final class DummyActivator2 extends AbstractDummyTestActivator {}

    private static final class DummyActivator3 extends AbstractDummyTestActivator {}

}
