package cz.cuni.mff.d3s.been.pluger.impl

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import javax.tools.StandardLocation
import javax.tools.ToolProvider
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class JarLoaderTest extends Specification {

    @Rule
    private TemporaryFolder temporaryFolder

    def 'test load jars'() {
        given:
            def helloWorldJar_1 = compileSourceFileAndCreateJar('/defaultJarLoader/src/', 'dummy.helloworld', 'HelloWorld_1')
            def helloWorldJar_2 = compileSourceFileAndCreateJar('/defaultJarLoader/src/', 'dummy.helloworld', 'HelloWorld_2')
            def helloWorldJar_3 = compileSourceFileAndCreateJar('/defaultJarLoader/src/', 'dummy.helloworld', 'HelloWorld_3')

            def jarLoader = new JarLoader()

        when:
            def jarLoaderClassLoader = jarLoader.loadJars([
                    helloWorldJar_1,
                    helloWorldJar_2,
                    helloWorldJar_3
            ])

        then:
            def helloWorld_1 = jarLoaderClassLoader.loadClass('dummy.helloworld.HelloWorld_1')
            def helloWorld_2 = jarLoaderClassLoader.loadClass('dummy.helloworld.HelloWorld_2')
            def helloWorld_3 = jarLoaderClassLoader.loadClass('dummy.helloworld.HelloWorld_3')

            assert (helloWorld_1.newInstance().invokeMethod('getHelloWorld', null) == "greetings from HelloWorld_1!!!")
            assert (helloWorld_2.newInstance().invokeMethod('getHelloWorld', null) == "greetings from HelloWorld_2!!!")
            assert (helloWorld_3.newInstance().invokeMethod('getHelloWorld', null) == "greetings from HelloWorld_3!!!")
    }

    private Path compileSourceFileAndCreateJar(String srcRoot, String packageName, String className) {
        def compileOutputFolder = temporaryFolder.newFolder()
        def jarFile = temporaryFolder.newFile("${className}.jar")
        def sourceFileResource = getClass().getResource("${srcRoot}/${packageName.replace('.', '/')}/${className}.java")
        def sourceFile = new File(sourceFileResource.getFile())

        def compiler = ToolProvider.getSystemJavaCompiler()
        def fileManager = compiler.getStandardFileManager(null, null, null)
        fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(compileOutputFolder))
        def compilationUnits = fileManager.getJavaFileObjectsFromFiles([sourceFile])
        def compilerTask = compiler.getTask(null, fileManager, null, null, null, compilationUnits)
        compilerTask.call()
        fileManager.close()

        FileOutputStream fout = new FileOutputStream(jarFile)
        JarOutputStream jarOut = new JarOutputStream(fout)
        jarOut.putNextEntry(new ZipEntry("${packageName.replace('.', '/')}/"))
        jarOut.putNextEntry(new ZipEntry("${packageName.replace('.', '/')}/${className}.class"))

        def classFilePath = compileOutputFolder.toPath()
        packageName.split('\\.').each {
            classFilePath = classFilePath.resolve(it)
        }
        classFilePath = classFilePath.resolve("${className}.class")
        def classFileBytes = Files.readAllBytes(classFilePath)
        jarOut.write(classFileBytes)
        jarOut.closeEntry()
        jarOut.close()
        fout.close()

        jarFile.toPath()
    }


}
