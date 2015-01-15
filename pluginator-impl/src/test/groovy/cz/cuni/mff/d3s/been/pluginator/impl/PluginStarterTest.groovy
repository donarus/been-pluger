package cz.cuni.mff.d3s.been.pluginator.impl

import cz.cuni.mff.d3s.been.pluginator.IPluginActivator
import spock.lang.Specification

class PluginStarterTest extends Specification {
    def 'test start method called'() {
        given:
            def pluginStarter = new PluginStarter()
            def activator1 = Mock(IPluginActivator)
            def activator2 = Mock(IPluginActivator)
            def activators = [
                    activator1,
                    activator2
            ]

        when:
            pluginStarter.start(activators)

        then:
            1 * activator1.start()
            1 * activator2.start()
    }
}
