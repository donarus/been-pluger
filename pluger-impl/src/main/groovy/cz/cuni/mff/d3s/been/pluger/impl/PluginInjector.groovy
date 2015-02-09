package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.InjectService
import cz.cuni.mff.d3s.been.pluger.IPluginInjector
import cz.cuni.mff.d3s.been.pluger.IServiceRegistry

class PluginInjector implements IPluginInjector {

    @Override
    void injectServices(IServiceRegistry pluginRegistry) {
        pluginRegistry.allRegisteredServices.each {
            injectService(it, pluginRegistry)
        }
    }

    private void injectService(Service subjectService, IServiceRegistry pluginRegistry) {
        def subject = subjectService.serviceInstance
        def currentClass = subject.class
        while(currentClass.superclass) { // we don't want to process Object.class
            currentClass.declaredFields.each { def field ->
                def injectAnnotation = field.getAnnotation(InjectService.class)
                if (injectAnnotation) {
                    def serviceInterface = field.type.array ? field.type.componentType : field.type
                    def serviceName = injectAnnotation.serviceName()

                    def injectedValue
                    if (field.type.array && serviceName) {
                        injectedValue = pluginRegistry.getServices(serviceName, serviceInterface)
                    } else if (field.type.array) {
                        injectedValue = pluginRegistry.getServices(serviceInterface)
                    } else if (serviceName) {
                        injectedValue = pluginRegistry.getService(serviceName, serviceInterface)
                    } else {
                        injectedValue = pluginRegistry.getService(serviceInterface)
                    }

                    subject."${field.name}" = injectedValue
                }
            }
            currentClass = currentClass.superclass
        }
    }

}
