package cz.cuni.mff.d3s.been.pluger;

import cz.cuni.mff.d3s.been.pluger.impl.PluginDescriptor;
import cz.cuni.mff.d3s.been.pluger.impl.PlugerConfig;

import java.nio.file.Path;
import java.util.Collection;

public interface IDependencyResolver {

    Collection<Path> resolve(PlugerConfig config, Collection<PluginDescriptor> pluginDescriptors);

}
