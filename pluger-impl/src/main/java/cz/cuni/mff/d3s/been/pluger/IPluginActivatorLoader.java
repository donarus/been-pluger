package cz.cuni.mff.d3s.been.pluger;

import cz.cuni.mff.d3s.been.pluger.impl.PluginDescriptor;

import java.util.Collection;

public interface IPluginActivatorLoader {

    Collection<IPluginActivator> loadActivators(Collection<PluginDescriptor> pluginDescriptors, ClassLoader pluginClassLoader);

}
