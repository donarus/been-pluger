package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IPluginActivator
import cz.cuni.mff.d3s.been.pluger.IServiceRegistry
import spock.lang.Specification

class PluginServiceActivatorTest extends Specification {

    def 'test register plugins'() {
        given:
            def activator = new ServiceActivator()
            def pluginRegistry = Mock(IServiceRegistry)
            def activators = [
                    Mock(IPluginActivator),
                    Mock(IPluginActivator)
            ]

        when:
            activator.activateServices(pluginRegistry, activators)

        then:
            activators.each {
                1 * it.activate(pluginRegistry)
            }
    }

}
