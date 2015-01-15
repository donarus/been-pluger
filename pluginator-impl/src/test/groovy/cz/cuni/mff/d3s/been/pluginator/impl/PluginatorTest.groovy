package cz.cuni.mff.d3s.been.pluginator.impl

import cz.cuni.mff.d3s.been.pluginator.IPluginFilter
import cz.cuni.mff.d3s.been.pluginator.IDependencyResolver
import cz.cuni.mff.d3s.been.pluginator.IPluginActivator
import cz.cuni.mff.d3s.been.pluginator.IPluginActivatorLoader
import cz.cuni.mff.d3s.been.pluginator.IPluginInitializer
import cz.cuni.mff.d3s.been.pluginator.IPluginInjector
import cz.cuni.mff.d3s.been.pluginator.IPluginLoader
import cz.cuni.mff.d3s.been.pluginator.IServiceRegistry
import cz.cuni.mff.d3s.been.pluginator.IPluginStarter
import cz.cuni.mff.d3s.been.pluginator.IPluginUnpacker
import cz.cuni.mff.d3s.been.pluginator.IPluginServiceActivator
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class PluginatorTest extends Specification {


    @Rule
    private TemporaryFolder tmpFolder

    def 'test create pluginator'() {
        given:
            def workingDir = tmpFolder.newFolder().toPath()
            def config = [
                    (Pluginator.WORKING_DIRECTORY_KEY) : workingDir,
                    (Pluginator.DEPENDENCIES_FINAL_KEY): []
            ]

        when:
            def pluginator = Pluginator.create(config)

        then:
            assert pluginator instanceof Pluginator
            assert pluginator.config.workingDirectory == workingDir
            assert pluginator.config.configDirectory == workingDir.resolve('config')
            assert Files.isDirectory(pluginator.@config.configDirectory)
            assert pluginator.config.pluginsDirectory == workingDir.resolve('plugins')
            assert Files.isDirectory(pluginator.@config.pluginsDirectory)
            assert pluginator.config.temporaryDirectory == workingDir.resolve('tmp')
            assert Files.isDirectory(pluginator.@config.temporaryDirectory)
            assert pluginator.config.disabledPluginsConfigFile == workingDir.resolve('config').resolve('disabled-plugins.conf')
            assert !Files.exists(pluginator.@config.disabledPluginsConfigFile)
            assert pluginator.config.disabledPlugins == []

            assert pluginator.pluginRegistry instanceof PluginatorRegistry
            assert pluginator.pluginLoader instanceof PluginLoader
            assert pluginator.pluginFilter instanceof PluginFilter
            assert pluginator.pluginUnpacker instanceof PluginUnpacker
            assert pluginator.dependencyResolver instanceof DependencyResolver
            assert pluginator.jarLoader instanceof JarLoader
            assert pluginator.pluginActivatorLoader instanceof PluginActivatorLoader
            assert pluginator.serviceRegistrator instanceof ServiceRegistrator
            assert pluginator.pluginInjector instanceof PluginInjector
            assert pluginator.pluginInitializer instanceof PluginInitializer
            assert pluginator.pluginStarter instanceof PluginStarter
    }


    def 'phases are ordered correctly'() {
        given:
            def config = Mock(PluginatorConfig)
            def pluginRegistry = Mock(IServiceRegistry)
            def pluginLoader = Mock(IPluginLoader)
            def pluginFilter = Mock(IPluginFilter)
            def pluginUnpacker = Mock(IPluginUnpacker)
            def dependencyResolver = Mock(IDependencyResolver)
            def jarLoader = Mock(JarLoader)
            def pluginActivatorLoader = Mock(IPluginActivatorLoader)
            def serviceRegistrator = Mock(IPluginServiceActivator)
            def pluginInjector = Mock(IPluginInjector)
            def pluginInitializer = Mock(IPluginInitializer)
            def pluginStarter = Mock(IPluginStarter)

            def pluginator = new Pluginator(
                    config: config,
                    pluginRegistry: pluginRegistry,
                    pluginLoader: pluginLoader,
                    pluginFilter: pluginFilter,
                    pluginUnpacker : pluginUnpacker,
                    dependencyResolver: dependencyResolver,
                    jarLoader: jarLoader,
                    pluginActivatorLoader: pluginActivatorLoader,
                    serviceRegistrator: serviceRegistrator,
                    pluginInjector: pluginInjector,
                    pluginInitializer: pluginInitializer,
                    pluginStarter: pluginStarter
            )

            def allPlugins = [
                    new PluginDescriptor(name: 'descriptor1'),
                    new PluginDescriptor(name: 'descriptor2'),
                    new PluginDescriptor(name: 'descriptor3')
            ]

            def selectedPlugins = [
                    new PluginDescriptor(name: 'descriptor1'),
                    new PluginDescriptor(name: 'descriptor3')
            ]

            def resolvedDependencies = [
                    Paths.get('resolvedDependency1'),
                    Paths.get('resolvedDependency2')
            ]

            def jarLoaderClassLoader = Mock(ClassLoader)

            def activators = [
                    Mock(IPluginActivator)
            ]

        when:
            pluginator.start()

        then:
            1 * pluginLoader.loadPlugins(config) >> allPlugins

        then:
            1 * pluginFilter.filter(config, allPlugins) >> selectedPlugins

        then:
            1 * pluginUnpacker.unpack(config, selectedPlugins)

        then:
            1 * dependencyResolver.resolve(config, selectedPlugins) >> resolvedDependencies

        then:
            1 * jarLoader.loadJars(resolvedDependencies) >> jarLoaderClassLoader

        then:
            1 * pluginActivatorLoader.loadActivators(selectedPlugins, jarLoaderClassLoader) >> activators

        then:
            1 * serviceRegistrator.activateServices(pluginRegistry, activators)

        then:
            1 * pluginInjector.injectServices(pluginRegistry)

        then:
            1 * pluginInitializer.initialize(activators)

        then:
            1 * pluginStarter.start(activators)
    }

}
