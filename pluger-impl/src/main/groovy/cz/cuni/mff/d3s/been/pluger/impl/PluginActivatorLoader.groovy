package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IPluginActivator
import cz.cuni.mff.d3s.been.pluger.IPluginActivatorLoader

class PluginActivatorLoader implements IPluginActivatorLoader {

    @Override
    Collection<IPluginActivator> loadActivators(Collection<PluginDescriptor> pluginDescriptors, ClassLoader pluginClassLoader) {
        pluginDescriptors*.activator.collect {
            pluginClassLoader.loadClass(it, true).newInstance() as IPluginActivator
        }
    }

}
