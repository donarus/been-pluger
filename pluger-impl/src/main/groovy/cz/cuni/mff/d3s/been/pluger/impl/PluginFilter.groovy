package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IPluginFilter

class PluginFilter implements IPluginFilter {

    @Override
    Collection<PluginDescriptor> filter(PlugerConfig plugerConfig, Collection<PluginDescriptor> allPlugins) {
        allPlugins.findAll {
            String pluginFullName = "${it.groupId}:${it.artifactId}:${it.version}"

            // 1. filter disabled plugins
            !(pluginFullName in plugerConfig.disabledPlugins)

            // 2. ...
        }
    }

}
