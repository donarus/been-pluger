package cz.cuni.mff.d3s.been.pluginator.impl

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Files

class PluginLoaderTest extends Specification {

    @Rule
    private TemporaryFolder tmpFolder

    def 'test load plugins'() {
        given:
            def pluginsDirectory = tmpFolder.newFolder().toPath()
            Files.copy(getClass().getResourceAsStream("/test-plugins/test-plugin1.plugin"), pluginsDirectory.resolve('test-plugin1.plugin'))
            Files.copy(getClass().getResourceAsStream("/test-plugins/test-plugin2.plugin"), pluginsDirectory.resolve('test-plugin2.plugin'))
            def config = new PluginatorConfig(
                    pluginsDirectory: pluginsDirectory
            )
            def pluginLoader = new PluginLoader()

        when:
            def pluginDescriptors = pluginLoader.loadPlugins(config)

        then:
            assert pluginDescriptors.size() == 2
            assert pluginDescriptors.find { it.name  == 'test plugin 1' } instanceof PluginDescriptor
            assert pluginDescriptors.find { it.name  == 'test plugin 2' } instanceof PluginDescriptor
    }

}
