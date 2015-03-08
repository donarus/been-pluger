package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IServiceRegistry
import spock.lang.Shared
import spock.lang.Specification

class ServiceRegistryTest extends Specification {

    @Shared
    IServiceRegistry registry

    def setup() {
        registry = new ServiceRegistry()
    }

    def 'get service'() {
        given:
            def service = new DummyServiceImpl()
            def serviceReference = new Service(serviceName: "serviceName", serviceInterface: DummyService, serviceInstance: service)
            (registry as ServiceRegistry).@registeredServices = [serviceReference]

        when:
            def foundService = getServiceMethod.call()

        then:
            assert foundService == service

        where:
            getServiceMethod << [
                    { registry.getService(DummyService) },
                    { registry.getService("serviceName", DummyService) },
            ]
    }

    def 'get all services by interface and name'() {
        given:
            def service1 = new DummyServiceImpl()
            def service2 = new DummyServiceImpl()
            def service3 = new DummyServiceImpl()
            def serviceReference1 = new Service(serviceName: "serviceName", serviceInterface: DummyService, serviceInstance: service1)
            def serviceReference2 = new Service(serviceName: "serviceName", serviceInterface: DummyService, serviceInstance: service2)
            def serviceReference3 = new Service(serviceName: "differendServiceName", serviceInterface: DummyService, serviceInstance: service3)

            (registry as ServiceRegistry).@registeredServices = [
                    serviceReference1,
                    serviceReference2,
                    serviceReference3
            ]

        when:
            def foundServices = registry.getServices("serviceName", DummyService)

        then:
            assert foundServices == [service1, service2]
    }

    def 'get all services by interface'() {
        given:
            def service1 = new DummyServiceImpl()
            def service2 = new DummyServiceImpl()
            def service3 = new DummyServiceImpl()
            def serviceReference1 = new Service(serviceName: "serviceName", serviceInterface: DummyService, serviceInstance: service1)
            def serviceReference2 = new Service(serviceName: "serviceName", serviceInterface: DummyService, serviceInstance: service2)
            def serviceReference3 = new Service(serviceName: "differendServiceName", serviceInterface: DummyService, serviceInstance: service3)

            (registry as ServiceRegistry).@registeredServices = [
                    serviceReference1,
                    serviceReference2,
                    serviceReference3
            ]

        when:
            def foundServices = registry.getServices(DummyService)

        then:
            assert foundServices == [service1, service2, service3]
    }

    def 'get non existing service'() {
        when:
            def registeredServiceByInterface = registry.getService(DummyService)
            def registeredServiceByNameAndInterface = registry.getService("serviceName", DummyService)

        then:
            assert registeredServiceByInterface == null
            assert registeredServiceByNameAndInterface == null
    }

    def 'register service'() {
        when:
            def installedService = method.call()
            def registeredServiceByInterface = registry.getService(interfaze)
            def registeredServiceByNameAndInterface = registry.getService(name, interfaze)

        then:
            (registry as ServiceRegistry).@registeredServices.size() == 1
            (registry as ServiceRegistry).@registeredServices*.serviceInstance == [installedService]
            (registry as ServiceRegistry).@registeredServices*.serviceInterface == [interfaze]
            (registry as ServiceRegistry).@registeredServices*.serviceName == [name]
            assert installedService == registeredServiceByNameAndInterface
            assert installedService == registeredServiceByInterface
            assert installedService instanceof DummyService

        where:
            [method, interfaze, name] << [
                    [{
                         registry.registerService(DummyService, new DummyServiceImpl())
                     }, DummyService, DummyService.class.name],
                    [{
                         registry.registerService(DummyService, DummyServiceImpl)
                     }, DummyService, DummyService.class.name],
                    [{
                         registry.registerService("serviceName", DummyService, new DummyServiceImpl())
                     }, DummyService, "serviceName"],
                    [{
                         registry.registerService("serviceName", DummyService, DummyServiceImpl)
                     }, DummyService, "serviceName"],
                    [{
                         registry.registerService(new DummyServiceImpl())
                     }, DummyServiceImpl, DummyServiceImpl.class.name],
                    [{
                         registry.registerService(DummyServiceImpl)
                     }, DummyServiceImpl, DummyServiceImpl.class.name],
                    [{
                         registry.registerService("serviceName", new DummyServiceImpl())
                     }, DummyServiceImpl, "serviceName"],
                    [{
                         registry.registerService("serviceName", DummyServiceImpl)
                     }, DummyServiceImpl, "serviceName"],
            ]
    }

    private interface DummyService {}

    private class DummyServiceImpl implements DummyService {}

}
