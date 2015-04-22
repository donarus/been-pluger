package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IPluginActivator
import spock.lang.Specification

class PluginConfigurerTest extends Specification {
    def 'test configure method called'() {
        given:
            def pluginConfigurer = new PluginConfigurer()
            def activator1 = Mock(IPluginActivator)
            def activator2 = Mock(IPluginActivator)
            def activators = [
                    activator1,
                    activator2
            ]
            def pluginsConfiguration = ["fake": "plugins config"]

        when:
            pluginConfigurer.configurePlugins(pluginsConfiguration, activators)

        then:
            1 * activator1.configure(pluginsConfiguration)
            1 * activator2.configure(pluginsConfiguration)
    }
}
