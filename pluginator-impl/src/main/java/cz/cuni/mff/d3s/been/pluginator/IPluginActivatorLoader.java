package cz.cuni.mff.d3s.been.pluginator;

import cz.cuni.mff.d3s.been.pluginator.impl.PluginDescriptor;

import java.util.Collection;

public interface IPluginActivatorLoader {

    Collection<IPluginActivator> loadActivators(Collection<PluginDescriptor> pluginDescriptors, ClassLoader pluginClassLoader);

}
