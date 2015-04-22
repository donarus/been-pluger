package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IPluginActivator
import cz.cuni.mff.d3s.been.pluger.IPluginConfigurer
import cz.cuni.mff.d3s.been.pluger.IPluginStarter

class PluginConfigurer implements IPluginConfigurer {

    @Override
    void configurePlugins(Map<String, String> pluginsConfiguration, Collection<IPluginActivator> activators) {
        activators*.configure(pluginsConfiguration)
    }

}
