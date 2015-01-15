package cz.cuni.mff.d3s.been.pluginator;

import cz.cuni.mff.d3s.been.pluginator.impl.PluginDescriptor;
import cz.cuni.mff.d3s.been.pluginator.impl.PluginatorConfig;

import java.util.Collection;

public interface IPluginLoader {

    Collection<PluginDescriptor> loadPlugins(PluginatorConfig config);

}
