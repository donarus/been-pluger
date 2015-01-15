package cz.cuni.mff.d3s.been.pluginator.impl

import cz.cuni.mff.d3s.been.pluginator.IPluginActivator
import spock.lang.Specification

class PluginInitializerTest extends Specification {
    def 'test initialize method called'() {
        given:
            def pluginInitializer = new PluginInitializer()
            def activator1 = Mock(IPluginActivator)
            def activator2 = Mock(IPluginActivator)
            def activators = [
                    activator1,
                    activator2
            ]

        when:
            pluginInitializer.initialize(activators)

        then:
            1 * activator1.initialize()
            1 * activator2.initialize()
    }
}
