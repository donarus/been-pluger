package cz.cuni.mff.d3s.been.pluginator.impl

import java.nio.file.Path

class PluginatorConfig {

    Path workingDirectory

    Path pluginsDirectory

    Path configDirectory

    Path temporaryDirectory

    Path unpackedLibsDirectory

    Path disabledPluginsConfigFile

    List<String> disabledPlugins

    List<String> finalDependencies

}
