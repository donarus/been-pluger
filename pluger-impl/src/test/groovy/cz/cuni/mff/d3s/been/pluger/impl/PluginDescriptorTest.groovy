package cz.cuni.mff.d3s.been.pluger.impl

import groovy.json.JsonSlurper
import spock.lang.Specification

import java.nio.file.Paths

class PluginDescriptorTest extends Specification {

    def 'test create from map'() {
        given:
            def json = getClass().getResourceAsStream('/plugin-descriptor.json').text
            def pluginPath = Paths.get('pluginPath')

        when:
            def pluginDescriptorMap = new JsonSlurper().parseText(json)
            def plugin = PluginDescriptor.create(pluginDescriptorMap, pluginPath)

        then:
            assert plugin == new PluginDescriptor(
                    pluginPath: pluginPath,
                    name: "nameValue",
                    description: "descriptionValue",
                    groupId: "groupIdValue",
                    artifactId: "artifactIdValue",
                    version: "versionValue",
                    activator: "activatorValue",
                    dependencies: [
                            new DependencyDescriptor(
                                    groupId: "groupIdValueDependency1",
                                    artifactId: "artifactIdValueDependency1",
                                    version: "versionValueDependency1",
                                    scope: DependencyScope.REQUIRED
                            ),
                            new DependencyDescriptor(
                                    groupId: "groupIdValueDependency2",
                                    artifactId: "artifactIdValueDependency2",
                                    version: "versionValueDependency2",
                                    scope: DependencyScope.OPTIONAL
                            )
                    ],
                    pluginDependencies: [
                            new DependencyDescriptor(
                                    groupId: "groupIdValuePluginDependency1",
                                    artifactId: "artifactIdValuePluginDependency1",
                                    version: "versionValuePluginDependency1",
                                    scope: DependencyScope.REQUIRED
                            ),
                            new DependencyDescriptor(
                                    groupId: "groupIdValuePluginDependency2",
                                    artifactId: "artifactIdValuePluginDependency2",
                                    version: "versionValuePluginDependency2",
                                    scope: DependencyScope.OPTIONAL
                            )
                    ]
            )
    }

    def 'test create json object from existing descriptor'() {
        given:
            def descriptor = new PluginDescriptor(
                    pluginPath: null,
                    name: "nameValue",
                    description: "descriptionValue",
                    groupId: "groupIdValue",
                    artifactId: "artifactIdValue",
                    version: "versionValue",
                    activator: "activatorValue",
                    dependencies: [
                            new DependencyDescriptor(
                                    groupId: "groupIdValueDependency1",
                                    artifactId: "artifactIdValueDependency1",
                                    version: "versionValueDependency1",
                                    scope: DependencyScope.REQUIRED
                            ),
                            new DependencyDescriptor(
                                    groupId: "groupIdValueDependency2",
                                    artifactId: "artifactIdValueDependency2",
                                    version: "versionValueDependency2",
                                    scope: DependencyScope.OPTIONAL
                            )
                    ],
                    pluginDependencies: [
                            new DependencyDescriptor(
                                    groupId: "groupIdValuePluginDependency1",
                                    artifactId: "artifactIdValuePluginDependency1",
                                    version: "versionValuePluginDependency1",
                                    scope: DependencyScope.REQUIRED
                            ),
                            new DependencyDescriptor(
                                    groupId: "groupIdValuePluginDependency2",
                                    artifactId: "artifactIdValuePluginDependency2",
                                    version: "versionValuePluginDependency2",
                                    scope: DependencyScope.OPTIONAL
                            )
                    ]
            )

        when:
            def json = descriptor.createJsonDescriptor()

        then:
            def expected = getClass().getResourceAsStream('/plugin-descriptor.json').text
            new JsonSlurper().parseText(json) == new JsonSlurper().parseText(expected)
    }

}
