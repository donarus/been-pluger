package cz.cuni.mff.d3s.been.pluger;

import cz.cuni.mff.d3s.been.pluger.impl.PlugerConfig;
import cz.cuni.mff.d3s.been.pluger.impl.PluginDescriptor;

import java.util.Collection;

public interface IPluginFilter {

    Collection<PluginDescriptor> filter(PlugerConfig plugerConfig, Collection<PluginDescriptor> allPlugins);

}
