package cz.cuni.mff.d3s.been.pluginator.impl

import cz.cuni.mff.d3s.been.pluginator.DescriptorException
import groovy.json.JsonSlurper
import spock.lang.Specification
import spock.lang.Unroll

class DepencencyDescriptorTest extends Specification {

    def 'create descriptor from given map when all required fields are filled'() {
        given:
            def json = getClass().getResourceAsStream('/plugin-descriptor.json').text

        when:
            Map pluginDescriptorMap = new JsonSlurper().parseText(json)
            def dependencyDescriptorMap = pluginDescriptorMap.dependencies[0]
            def dependency = DependencyDescriptor.create(dependencyDescriptorMap)

        then:
            assert dependency == new DependencyDescriptor(
                    groupId: "groupIdValueDependency1",
                    artifactId: "artifactIdValueDependency1",
                    version: "versionValueDependency1",
                    scope: DependencyScope.REQUIRED
            )
    }

    @Unroll
    def 'exc with message "#expectedMessage" is thrown when created with missing #missingFields'() {
        given:
            def json = getClass().getResourceAsStream('/plugin-descriptor.json').text

        when:
            Map pluginDescriptorMap = new JsonSlurper().parseText(json)
            def dependencyDescriptorMap = pluginDescriptorMap.dependencies[0]
            missingFields.each { (dependencyDescriptorMap as Map).remove(it) }
            DependencyDescriptor.create(dependencyDescriptorMap)

        then:
            DescriptorException e = thrown()
            e.message.contains(expectedMessage)

        where:
            missingFields                                 | expectedMessage
            ['groupId']                                   | "missing 'groupId'"
            ['artifactId']                                | "missing 'artifactId'"
            ['version']                                   | "missing 'version'"
            ['scope']                                     | "missing 'scope'"
            ['groupId', 'artifactId']                     | "missing 'groupId', missing 'artifactId'"
            ['groupId', 'artifactId', 'version', 'scope'] | "missing 'groupId', missing 'artifactId', missing 'version', missing 'scope'"

    }

    def 'Descriptor Exception is thrown when descriptor is created with invalid scope'() {
        given:
            def json = getClass().getResourceAsStream('/plugin-descriptor.json').text

        when:
            Map pluginDescriptorMap = new JsonSlurper().parseText(json)
            def dependencyDescriptorMap = pluginDescriptorMap.dependencies[0]
            (dependencyDescriptorMap as Map).replace('scope', 'invalid scope')
            DependencyDescriptor.create(dependencyDescriptorMap)

        then:
            DescriptorException e = thrown()
            e.message == "Cannot create dependency descriptor. Unknown scope: 'invalid scope'."
    }

}
