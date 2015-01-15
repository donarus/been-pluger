package cz.cuni.mff.d3s.been.pluginator.impl

import cz.cuni.mff.d3s.been.pluginator.IPluginActivator
import cz.cuni.mff.d3s.been.pluginator.IServiceRegistry
import spock.lang.Specification

class ServiceRegistratorTest extends Specification {

    def 'test register plugins'() {
        given:
            def registrator = new ServiceRegistrator()
            def pluginRegistry = Mock(IServiceRegistry)
            def activators = [
                    Mock(IPluginActivator),
                    Mock(IPluginActivator)
            ]

        when:
            registrator.activateServices(pluginRegistry, activators)

        then:
            activators.each {
                1 * it.activate(pluginRegistry)
            }
    }

}
