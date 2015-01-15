package cz.cuni.mff.d3s.been.pluginator.impl

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class DependencyResolverTest extends Specification {

    @Rule
    private TemporaryFolder tmpFolder

    def 'resolve dependencies'() {
        given:
            def pluginsDirectory = tmpFolder.newFolder().toPath()
            def unpackedLibsDirectory = tmpFolder.newFolder().toPath()
            def pluginPath = pluginsDirectory.resolve('test-plugin1.plugin')
            Files.copy(getClass().getResourceAsStream("/defaultDependencyResolverTest/plugin1.plugin"), pluginPath)
            def pluginDescriptors = [createDescriptor_1(pluginPath)]
            def config = new PluginatorConfig(
                    pluginsDirectory: pluginsDirectory,
                    unpackedLibsDirectory: unpackedLibsDirectory
            )

            def dependencyResolver = new DependencyResolver()

        when:
            def resolved = dependencyResolver.resolve(config, pluginDescriptors)

        then:
            assert resolved.sort() == [
                    getPath(unpackedLibsDirectory, 'dummy', 'group1', 'dummy.artifact1', '1.0.0', 'dummy.artifact1-1.0.0.jar'),
                    getPath(unpackedLibsDirectory, 'dummy', 'group1', 'dummy.artifact2', '1.2.3', 'dummy.artifact2-1.2.3.jar'),
                    getPath(unpackedLibsDirectory, 'dummy', 'group2', 'dummy.artifact3', '1.0-SNAPSHOT', 'dummy.artifact3-1.0-SNAPSHOT.jar'),
                    getPath(unpackedLibsDirectory, 'dummy', 'group2', 'dummy.artifact4', '2.0.0', 'dummy.artifact4-2.0.0.jar'),
                    getPath(unpackedLibsDirectory, 'my', 'dummy', 'plugin', 'myplugin.artifact', '1.2.3', 'myplugin.artifact-1.2.3.jar')
            ].sort()
    }

    def 'test two dependencies with mismatched versions'() {
        given:
            def pluginsDirectory = tmpFolder.newFolder().toPath()
            def unpackedLibsDirectory = tmpFolder.newFolder().toPath()
            def plugin1Path = pluginsDirectory.resolve('test-plugin1.plugin')
            def plugin2Path = pluginsDirectory.resolve('test-plugin2.plugin')
            Files.copy(getClass().getResourceAsStream("/defaultDependencyResolverTest/plugin1.plugin"), plugin1Path)
            Files.copy(getClass().getResourceAsStream("/defaultDependencyResolverTest/plugin2.plugin"), plugin2Path)
            def pluginDescriptors = [
                    createDescriptor_1(plugin1Path),
                    createDescriptor_2(plugin2Path)
            ]
            def config = new PluginatorConfig(
                    pluginsDirectory: pluginsDirectory,
                    unpackedLibsDirectory: unpackedLibsDirectory
            )

            def dependencyResolver = new DependencyResolver()

        when:
            def resolved = dependencyResolver.resolve(config, pluginDescriptors)

        then:
            assert resolved.sort() == [
                    getPath(unpackedLibsDirectory, 'dummy', 'group1', 'dummy.artifact1', '2.0.0', 'dummy.artifact1-2.0.0.jar'),
                    getPath(unpackedLibsDirectory, 'dummy', 'group1', 'dummy.artifact2', '1.2.3', 'dummy.artifact2-1.2.3.jar'),
                    getPath(unpackedLibsDirectory, 'dummy', 'group2', 'dummy.artifact3', '1.0-SNAPSHOT', 'dummy.artifact3-1.0-SNAPSHOT.jar'),
                    getPath(unpackedLibsDirectory, 'dummy', 'group2', 'dummy.artifact4', '2.0.0', 'dummy.artifact4-2.0.0.jar'),
                    getPath(unpackedLibsDirectory, 'dummy', 'group3', 'dummy.artifact5', '2.0.0', 'dummy.artifact5-2.0.0.jar'),
                    getPath(unpackedLibsDirectory, 'my', 'dummy', 'plugin', 'myplugin.artifact', '1.2.3', 'myplugin.artifact-1.2.3.jar'),
                    getPath(unpackedLibsDirectory, 'my', 'dummy', 'plugin', 'myplugin2.artifact', '1.2.3', 'myplugin2.artifact-1.2.3.jar')
            ].sort()
    }

    def 'test clash between final dependencies and plugin dependencies'() {
        given:
            def pluginsDirectory = tmpFolder.newFolder().toPath()
            def unpackedLibsDirectory = tmpFolder.newFolder().toPath()
            def plugin1Path = pluginsDirectory.resolve('test-plugin1.plugin')
            Files.copy(getClass().getResourceAsStream("/defaultDependencyResolverTest/plugin1.plugin"), plugin1Path)
            def pluginDescriptors = [
                    createDescriptor_1(plugin1Path)
            ]
            def config = new PluginatorConfig(
                    pluginsDirectory: pluginsDirectory,
                    unpackedLibsDirectory: unpackedLibsDirectory,
                    finalDependencies: [
                            "dummy.group1:dummy.artifact1:2.0.0",
                            "dummy.group2:dummy.artifact4:2.1.0",
                    ]
            )

            def dependencyResolver = new DependencyResolver()

        when:
            def resolved = dependencyResolver.resolve(config, pluginDescriptors)

        then:
            assert resolved.sort() == [
                    getPath(unpackedLibsDirectory, 'dummy', 'group1', 'dummy.artifact2', '1.2.3', 'dummy.artifact2-1.2.3.jar'),
                    getPath(unpackedLibsDirectory, 'dummy', 'group2', 'dummy.artifact3', '1.0-SNAPSHOT', 'dummy.artifact3-1.0-SNAPSHOT.jar'),
                    getPath(unpackedLibsDirectory, 'my', 'dummy', 'plugin', 'myplugin.artifact', '1.2.3', 'myplugin.artifact-1.2.3.jar')
            ].sort()
    }

    private def getPath(Path parent, String... parts) {
        def path = parent
        parts.each {
            path = path.resolve(it)
        }
        path
    }

    private PluginDescriptor createDescriptor_1(Path pluginPath) {
        new PluginDescriptor(
                pluginPath: pluginPath,

                groupId: "my.dummy.plugin",
                artifactId: "myplugin.artifact",
                version: "1.2.3",
                activator: "activatorValue",
                dependencies: [
                        new DependencyDescriptor(
                                groupId: "dummy.group1",
                                artifactId: "dummy.artifact1",
                                version: "1.0.0",
                        ),
                        new DependencyDescriptor(
                                groupId: "dummy.group1",
                                artifactId: "dummy.artifact2",
                                version: "1.2.3",
                        ),
                        new DependencyDescriptor(
                                groupId: "dummy.group2",
                                artifactId: "dummy.artifact3",
                                version: "1.0-SNAPSHOT",
                        ),
                        new DependencyDescriptor(
                                groupId: "dummy.group2",
                                artifactId: "dummy.artifact4",
                                version: "2.0.0",
                        )
                ]
        )
    }

    private PluginDescriptor createDescriptor_2(Path pluginPath) {
        new PluginDescriptor(
                pluginPath: pluginPath,

                groupId: "my.dummy.plugin",
                artifactId: "myplugin2.artifact",
                version: "1.2.3",
                activator: "activatorValue",
                dependencies: [
                        new DependencyDescriptor(
                                groupId: "dummy.group1",
                                artifactId: "dummy.artifact1",
                                version: "2.0.0",
                        ),
                        new DependencyDescriptor(
                                groupId: "dummy.group1",
                                artifactId: "dummy.artifact2",
                                version: "1.0.0",
                        ),
                        new DependencyDescriptor(
                                groupId: "dummy.group2",
                                artifactId: "dummy.artifact3",
                                version: "1.0-SNAPSHOT",
                        ),
                        new DependencyDescriptor(
                                groupId: "dummy.group3",
                                artifactId: "dummy.artifact5",
                                version: "2.0.0",
                        )
                ]
        )
    }

}
