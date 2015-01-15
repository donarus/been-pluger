package cz.cuni.mff.d3s.been.pluginator.impl

import cz.cuni.mff.d3s.been.pluginator.IPluginLoader
import groovy.json.JsonSlurper

import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class PluginLoader implements IPluginLoader {

    public static final String PLUGIN_FILE_EXTENSION = '.plugin'

    public static final String PLUGIN_DESCRIPTOR_FILE_NAME = 'plugin-descriptor.json'

    @Override
    Collection<PluginDescriptor> loadPlugins(PluginatorConfig config) {
        findAvailablePlugins(config).collect { Path pluginFile ->
            pluginFile = pluginFile.toAbsolutePath()
            def pluginDescriptorJson = loadPluginDescriptor(pluginFile)
            def pluginDescriptorMap = new JsonSlurper().parseText(pluginDescriptorJson)
            PluginDescriptor.create(pluginDescriptorMap, pluginFile)
        }
    }

    private String loadPluginDescriptor(Path pluginFile) {
        def zipPluginFile = new ZipFile(pluginFile.toFile())
        def pluginDescriptorEntry = zipPluginFile.getEntry(PLUGIN_DESCRIPTOR_FILE_NAME)
        def pluginDescriptorJson = readZipEntryAsString(zipPluginFile, pluginDescriptorEntry)
        zipPluginFile.close()
        pluginDescriptorJson
    }

    private String readZipEntryAsString(ZipFile file, ZipEntry pluginDescriptorEntry) {
        file.getInputStream(pluginDescriptorEntry).text
    }

    private ArrayList findAvailablePlugins(PluginatorConfig config) {
        Files.list(config.pluginsDirectory).findAll {
            it.toString().endsWith(PLUGIN_FILE_EXTENSION)
        }
    }

}
