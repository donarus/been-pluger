package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IPluginInjector
import cz.cuni.mff.d3s.been.pluger.IServiceRegistry
import cz.cuni.mff.d3s.been.pluger.MissingConstructorException

class PlugerRegistry implements IServiceRegistry {

    private Collection<Service> registeredServices = []

    @Override
    def <I, C extends I> C registerService(Class<I> serviceInterface, C serviceImplementationInstance) {
        registerService(serviceInterface.name, serviceInterface, serviceImplementationInstance)
    }

    @Override
    def <I, C extends I> C registerService(Class<I> serviceInterface, Class<C> serviceImplementation) {
        registerService(serviceInterface.name, serviceInterface, serviceImplementation)
    }

    @Override
    def <I, C extends I> C registerService(String serviceName, Class<I> serviceInterface, C serviceImplementationInstance) {
        registeredServices << new Service(
                serviceName: serviceName,
                serviceInterface: serviceInterface,
                serviceInstance: serviceImplementationInstance
        )

        serviceImplementationInstance
    }

    @Override
    def <I, C extends I> C registerService(String serviceName, Class<I> serviceInterface, Class<C> serviceImplementation) {
        def serviceImplementationInstance
        try {
            serviceImplementationInstance = serviceImplementation.newInstance()
        } catch (IllegalAccessException e) {
            throw new MissingConstructorException("PluginRegistry can't instantiate service '${serviceImplementation.name}'. Parameterless constructor is not accessible.", e)
        } catch (InstantiationException e) {
            throw new MissingConstructorException("PluginRegistry can't instantiate service '${serviceImplementation.name}'. Parameterless constructor was not found.", e)
        }

        registerService(serviceName, serviceInterface, serviceImplementationInstance)
    }

    @Override
    def <C> C registerService(Class<C> serviceImplementation) {
        registerService(serviceImplementation.name, serviceImplementation, serviceImplementation)
    }

    @Override
    def <C> C registerService(C serviceImplementationInstance) {
        registerService(serviceImplementationInstance.getClass().name, serviceImplementationInstance.getClass(), serviceImplementationInstance)
    }

    @Override
    def <C> C registerService(String serviceName, Class<C> serviceImplementation) {
        registerService(serviceName, serviceImplementation, serviceImplementation)
    }

    @Override
    def <C> C registerService(String serviceName, C serviceImplementationInstance) {
        registerService(serviceName, serviceImplementationInstance.getClass(), serviceImplementationInstance)
    }

    @Override
    def <I> I getService(Class<I> serviceInterface) {
        registeredServices.find {
            it.serviceInterface == serviceInterface
        }?.serviceInstance
    }

    @Override
    def <I> I getService(String serviceName, Class<I> serviceInterface) {
        registeredServices.find {
            it.serviceName == serviceName &&
            it.serviceInterface == serviceInterface
        }?.serviceInstance
    }

    @Override
    def <I> I getServices(Class<I> serviceInterface) {
        registeredServices.findAll {
            it.serviceInterface == serviceInterface
        }?.serviceInstance
    }

    @Override
    def <I> I getServices(String serviceName, Class<I> serviceInterface) {
        registeredServices.findAll {
            it.serviceName == serviceName &&
            it.serviceInterface == serviceInterface
        }?.serviceInstance
    }

    public void setInjector(IPluginInjector injector) {
        this.injector = injector;
    }

    public Collection<Service> getAllRegisteredServices() {
        return registeredServices
    }

}
