package cz.cuni.mff.d3s.been.pluginator;

import cz.cuni.mff.d3s.been.pluginator.impl.PluginatorConfig;
import cz.cuni.mff.d3s.been.pluginator.impl.PluginDescriptor;

import java.util.Collection;

public interface IPluginFilter {

    Collection<PluginDescriptor> filter(PluginatorConfig pluginatorConfig, Collection<PluginDescriptor> allPlugins);

}
