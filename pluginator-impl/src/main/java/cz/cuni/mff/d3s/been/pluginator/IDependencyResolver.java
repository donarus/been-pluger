package cz.cuni.mff.d3s.been.pluginator;

import cz.cuni.mff.d3s.been.pluginator.impl.PluginDescriptor;
import cz.cuni.mff.d3s.been.pluginator.impl.PluginatorConfig;

import java.nio.file.Path;
import java.util.Collection;

public interface IDependencyResolver {

    Collection<Path> resolve(PluginatorConfig config, Collection<PluginDescriptor> pluginDescriptors);

}
