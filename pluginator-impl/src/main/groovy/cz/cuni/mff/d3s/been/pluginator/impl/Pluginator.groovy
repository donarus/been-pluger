package cz.cuni.mff.d3s.been.pluginator.impl

import cz.cuni.mff.d3s.been.pluginator.IPluginFilter
import cz.cuni.mff.d3s.been.pluginator.IDependencyResolver
import cz.cuni.mff.d3s.been.pluginator.IJarLoader
import cz.cuni.mff.d3s.been.pluginator.IPluginActivatorLoader
import cz.cuni.mff.d3s.been.pluginator.IPluginInitializer
import cz.cuni.mff.d3s.been.pluginator.IPluginInjector
import cz.cuni.mff.d3s.been.pluginator.IPluginLoader
import cz.cuni.mff.d3s.been.pluginator.IServiceRegistry
import cz.cuni.mff.d3s.been.pluginator.IPluginStarter
import cz.cuni.mff.d3s.been.pluginator.IPluginUnpacker
import cz.cuni.mff.d3s.been.pluginator.IPluginServiceActivator

import java.nio.file.Files
import java.nio.file.Path

class Pluginator {

    static final String WORKING_DIRECTORY_KEY = "working.directory"

    static final String DEPENDENCIES_FINAL_KEY = "dependencies.final"

    private PluginatorConfig config

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

    private Pluginator() {
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

    public static Pluginator create(Map<String, Object> config) {
        Path workingDirectory = config.get(WORKING_DIRECTORY_KEY)
        Collection<String> finalDependencies = config.get(DEPENDENCIES_FINAL_KEY)

        def unpackedLibsDirectory = Files.createDirectories(workingDirectory.resolve('libs'))
        def pluginsDirectory = Files.createDirectories(workingDirectory.resolve('plugins'))
        def configDirectory = Files.createDirectories(workingDirectory.resolve('config'))
        def temporaryDirectory = Files.createDirectories(workingDirectory.resolve('tmp'))

        def disabledPluginsConfigFile = configDirectory.resolve('disabled-plugins.conf')
        def disabledPlugins = Files.isReadable(disabledPluginsConfigFile) ? Files.readAllLines(disabledPluginsConfigFile) : []

        return new Pluginator(
                config: new PluginatorConfig(
                        workingDirectory: workingDirectory.toAbsolutePath(),
                        unpackedLibsDirectory: unpackedLibsDirectory,
                        pluginsDirectory: pluginsDirectory.toAbsolutePath(),
                        configDirectory: configDirectory.toAbsolutePath(),
                        temporaryDirectory: temporaryDirectory.toAbsolutePath(),
                        disabledPluginsConfigFile: disabledPluginsConfigFile.toAbsolutePath(),
                        disabledPlugins: disabledPlugins,
                        finalDependencies: finalDependencies
                ),
                pluginRegistry: new PluginatorRegistry(),
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
