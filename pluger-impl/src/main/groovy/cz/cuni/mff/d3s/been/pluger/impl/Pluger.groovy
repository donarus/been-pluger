package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IPluginFilter
import cz.cuni.mff.d3s.been.pluger.IDependencyResolver
import cz.cuni.mff.d3s.been.pluger.IJarLoader
import cz.cuni.mff.d3s.been.pluger.IPluginActivatorLoader
import cz.cuni.mff.d3s.been.pluger.IPluginInitializer
import cz.cuni.mff.d3s.been.pluger.IPluginInjector
import cz.cuni.mff.d3s.been.pluger.IPluginLoader
import cz.cuni.mff.d3s.been.pluger.IServiceRegistry
import cz.cuni.mff.d3s.been.pluger.IPluginStarter
import cz.cuni.mff.d3s.been.pluger.IPluginUnpacker
import cz.cuni.mff.d3s.been.pluger.IPluginServiceActivator

import java.nio.file.Files
import java.nio.file.Path

class Pluger {

    static final String WORKING_DIRECTORY_KEY = "working.directory"

    static final String DEPENDENCIES_FINAL_KEY = "dependencies.final"

    private PlugerConfig config

    private IServiceRegistry pluginRegistry

    private IPluginLoader pluginLoader

    private IPluginFilter pluginFilter

    private IPluginUnpacker pluginUnpacker

    private IDependencyResolver dependencyResolver

    private IJarLoader jarLoader

    private IPluginActivatorLoader pluginActivatorLoader

    private IPluginServiceActivator serviceRegistrator

    private IPluginInjector pluginInjector

    private IPluginInitializer pluginInitializer

    private IPluginStarter pluginStarter

    private Pluger() {
        // prevents outer instantiation .. use create(..) method
    }

    public void start() {
        def allPlugins = pluginLoader.loadPlugins(config)
        def allowedPlugins = pluginFilter.filter(config, allPlugins)
        pluginUnpacker.unpack(config, allowedPlugins)
        def resolvedDependencies = dependencyResolver.resolve(config, allowedPlugins)
        def pluginClassloader = jarLoader.loadJars(resolvedDependencies)
        def pluginActivators = pluginActivatorLoader.loadActivators(allowedPlugins, pluginClassloader)
        serviceRegistrator.activateServices(pluginRegistry, pluginActivators)
        pluginInjector.injectServices(pluginRegistry)
        pluginInitializer.initialize(pluginActivators)
        pluginStarter.start(pluginActivators)
    }

    public static Pluger create(Map<String, Object> config) {
        Path workingDirectory = config.get(WORKING_DIRECTORY_KEY)
        Collection<String> finalDependencies = config.get(DEPENDENCIES_FINAL_KEY)

        def unpackedLibsDirectory = Files.createDirectories(workingDirectory.resolve('libs'))
        def pluginsDirectory = Files.createDirectories(workingDirectory.resolve('plugins'))
        def configDirectory = Files.createDirectories(workingDirectory.resolve('config'))
        def temporaryDirectory = Files.createDirectories(workingDirectory.resolve('tmp'))

        def disabledPluginsConfigFile = configDirectory.resolve('disabled-plugins.conf')
        def disabledPlugins = Files.isReadable(disabledPluginsConfigFile) ? Files.readAllLines(disabledPluginsConfigFile) : []

        return new Pluger(
                config: new PlugerConfig(
                        workingDirectory: workingDirectory.toAbsolutePath(),
                        unpackedLibsDirectory: unpackedLibsDirectory,
                        pluginsDirectory: pluginsDirectory.toAbsolutePath(),
                        configDirectory: configDirectory.toAbsolutePath(),
                        temporaryDirectory: temporaryDirectory.toAbsolutePath(),
                        disabledPluginsConfigFile: disabledPluginsConfigFile.toAbsolutePath(),
                        disabledPlugins: disabledPlugins,
                        finalDependencies: finalDependencies
                ),
                pluginRegistry: new PlugerRegistry(),
                pluginLoader: new PluginLoader(),
                pluginFilter: new PluginFilter(),
                pluginUnpacker: new PluginUnpacker(),
                dependencyResolver: new DependencyResolver(),
                jarLoader: new JarLoader(),
                pluginActivatorLoader: new PluginActivatorLoader(),
                serviceRegistrator: new ServiceRegistrator(),
                pluginInjector: new PluginInjector(),
                pluginInitializer: new PluginInitializer(),
                pluginStarter: new PluginStarter()
        )
    }

}
