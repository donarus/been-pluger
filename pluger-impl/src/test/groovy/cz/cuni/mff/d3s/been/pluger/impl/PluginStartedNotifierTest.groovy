package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IPluginActivator
import spock.lang.Specification

class PluginStartedNotifierTest extends Specification {
    def 'test plugin started method called'() {
        given:
            def pluginStartedNotifier = new PluginStartedNotifier()
            def activator1 = Mock(IPluginActivator)
            def activator2 = Mock(IPluginActivator)
            def activators = [
                    activator1,
                    activator2
            ]

        when:
            pluginStartedNotifier.notifyStarted(activators)

        then:
            1 * activator1.notifyStarted()
            1 * activator2.notifyStarted()
    }
}