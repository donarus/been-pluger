package cz.cuni.mff.d3s.been.pluginator.impl

import cz.cuni.mff.d3s.been.pluginator.IPluginFilter

class PluginFilter implements IPluginFilter {

    @Override
    Collection<PluginDescriptor> filter(PluginatorConfig pluginatorConfig, Collection<PluginDescriptor> allPlugins) {
        allPlugins.findAll {
            String pluginFullName = "${it.groupId}:${it.artifactId}:${it.version}"

            // 1. filter disabled plugins
            !(pluginFullName in pluginatorConfig.disabledPlugins)

            // 2. ...
        }
    }

}
