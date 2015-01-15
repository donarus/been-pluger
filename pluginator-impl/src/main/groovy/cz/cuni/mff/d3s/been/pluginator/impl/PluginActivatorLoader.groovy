package cz.cuni.mff.d3s.been.pluginator.impl

import cz.cuni.mff.d3s.been.pluginator.IPluginActivator
import cz.cuni.mff.d3s.been.pluginator.IPluginActivatorLoader

class PluginActivatorLoader implements IPluginActivatorLoader {

    @Override
    Collection<IPluginActivator> loadActivators(Collection<PluginDescriptor> pluginDescriptors, ClassLoader pluginClassLoader) {
        pluginDescriptors*.activator.collect {
            pluginClassLoader.loadClass(it, true).newInstance() as IPluginActivator
        }
    }

}
