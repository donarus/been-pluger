package cz.cuni.mff.d3s.been.pluger.impl

import spock.lang.Specification

class PluginFilterTest extends Specification {

    def 'test select allowed plugins when no disabled plugin'() {
        given:
            def selector = new PluginFilter()
            def config = new PlugerConfig(
                    disabledPlugins: []
            )
            def allPlugins = [
                    new PluginDescriptor(groupId: 'groupId1', artifactId: 'artifactId1', version: 'version1'),
                    new PluginDescriptor(groupId: 'groupId2', artifactId: 'artifactId2', version: 'version2'),
                    new PluginDescriptor(groupId: 'groupId3', artifactId: 'artifactId3', version: 'version3')
            ]

        when:
            def selected = selector.filter(config, allPlugins)

        then:
            assert selected == allPlugins
    }

    def 'test select allowed plugins when some plugins are disabled'() {
        given:
            def selector = new PluginFilter()
            def config = new PlugerConfig(
                    disabledPlugins: [
                            'groupId1:artifactId1:version1',
                            'groupId3:artifactId3:version3'
                    ]
            )
            def allPlugins = [
                    new PluginDescriptor(groupId: 'groupId1', artifactId: 'artifactId1', version: 'version1'),
                    new PluginDescriptor(groupId: 'groupId2', artifactId: 'artifactId2', version: 'version2'),
                    new PluginDescriptor(groupId: 'groupId3', artifactId: 'artifactId3', version: 'version3')
            ]

        when:
            def selected = selector.filter(config, allPlugins)

        then:
            assert selected == [
                    new PluginDescriptor(groupId: 'groupId2', artifactId: 'artifactId2', version: 'version2')
            ]
    }

    def 'test select allowed plugins when non-existing plugin disabled'() {
        given:
            def selector = new PluginFilter()
            def config = new PlugerConfig(
                    disabledPlugins: [
                            'non:existing:plugin'
                    ]
            )
            def allPlugins = [
                    new PluginDescriptor(groupId: 'groupId1', artifactId: 'artifactId1', version: 'version1'),
                    new PluginDescriptor(groupId: 'groupId2', artifactId: 'artifactId2', version: 'version2'),
                    new PluginDescriptor(groupId: 'groupId3', artifactId: 'artifactId3', version: 'version3')
            ]

        when:
            def selected = selector.filter(config, allPlugins)

        then:
            assert selected == allPlugins
    }

}
