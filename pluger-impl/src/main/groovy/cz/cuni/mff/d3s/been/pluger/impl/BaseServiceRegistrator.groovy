package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IBaseServiceRegistrator
import cz.cuni.mff.d3s.been.pluger.IServiceRegistry
import cz.cuni.mff.d3s.been.pluger.PlugerServiceConstants

class BaseServiceRegistrator implements IBaseServiceRegistrator{

    @Override
    void register(PlugerConfig plugerConfig, IServiceRegistry registry, ClassLoader pluginClassLoader) {
        registry.registerService(PlugerServiceConstants.PLUGIN_CLASSLOADER, ClassLoader, pluginClassLoader)
        registry.registerService(PlugerServiceConstants.PLUGINS_WORKING_DIRECTORY, File, plugerConfig.pluginsWorkingDirectory.toFile())
        registry.registerService(PlugerServiceConstants.TMP_DIRECTORY, File, plugerConfig.temporaryDirectory.toFile())
        registry.registerService(PlugerServiceConstants.PLUGER_STARTUP_ARGUMENTS, String[], plugerConfig.plugerStartupArgs)
    }

}
