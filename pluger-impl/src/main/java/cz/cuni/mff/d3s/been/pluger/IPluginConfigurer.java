package cz.cuni.mff.d3s.been.pluger;

import cz.cuni.mff.d3s.been.pluger.impl.PluginDescriptor;

import java.util.Collection;
import java.util.Map;

public interface IPluginConfigurer {

    void configurePlugins(Map<String, String> pluginsConfiguration, Collection<IPluginActivator> activators);

}
