package cz.cuni.mff.d3s.been.pluger;

import cz.cuni.mff.d3s.been.pluger.impl.PluginDescriptor;
import cz.cuni.mff.d3s.been.pluger.impl.PlugerConfig;

import java.util.Collection;

public interface IPluginUnpacker {

    void unpack(PlugerConfig config, Collection<PluginDescriptor> pluginDescriptors);

}
