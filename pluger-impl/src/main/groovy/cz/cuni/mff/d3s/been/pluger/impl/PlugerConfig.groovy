package cz.cuni.mff.d3s.been.pluger.impl

import java.nio.file.Path

class PlugerConfig {

    Path workingDirectory

    Path pluginsDirectory

    Path configDirectory

    Path temporaryDirectory

    Path unpackedLibsDirectory

    Path disabledPluginsConfigFile

    Path pluginsWorkingDirectory

    List<String> disabledPlugins

    List<String> finalDependencies

    String[] programArgs

}
