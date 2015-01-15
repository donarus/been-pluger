package cz.cuni.mff.d3s.been.pluginator.impl

import cz.cuni.mff.d3s.been.pluginator.InjectService
import cz.cuni.mff.d3s.been.pluginator.IServiceRegistry
import spock.lang.Specification

class PluginInjectorTest extends Specification {

    def 'test injection'() {
        given:
            def injector = new PluginInjector()
            def registry = Mock(IServiceRegistry)

            def producer = new Producer()
            def consumer1 = new Consumer()
            def consumer2 = new Consumer()
            def consumer3 = new Consumer()
            def consumer4 = new Consumer()

            def producerService = new Service<IProducer, Producer>(serviceInterface: IProducer, serviceInstance: producer)
            def consumerService1 = new Service<IConsumer, Consumer>(serviceInterface: IConsumer, serviceInstance: consumer1)
            def consumerService2 = new Service<IConsumer, Consumer>(serviceInterface: IConsumer, serviceInstance: consumer2, serviceName: 'consumerNameSingle')
            def consumerService3 = new Service<IConsumer, Consumer>(serviceInterface: IConsumer, serviceInstance: consumer3, serviceName: 'consumerNameDouble')
            def consumerService4 = new Service<IConsumer, Consumer>(serviceInterface: IConsumer, serviceInstance: consumer4, serviceName: 'consumerNameDouble')

        when:
            injector.injectServices(registry)

        then:
            1 * registry.getAllRegisteredServices() >> [
                    producerService,
                    consumerService1,
                    consumerService2,
                    consumerService3,
                    consumerService4
            ]

            1 * registry.getService(IConsumer) >> consumer1
            assert producer.@consumerByInterface == consumer1

            1 * registry.getService('consumerNameSingle', IConsumer) >> consumer2
            assert producer.@consumerByName1 == consumer2

            1 * registry.getService('consumerNameDouble', IConsumer) >> consumer3
            assert producer.@consumerByName2 == consumer3

            1 * registry.getServices(IConsumer) >> [
                    consumer1,
                    consumer2,
                    consumer3,
                    consumer4
            ]
            assert producer.@allConsumersByInterface == [
                    consumer1,
                    consumer2,
                    consumer3,
                    consumer4
            ]

            1 * registry.getServices('consumerNameDouble', IConsumer) >> [
                    consumer3,
                    consumer4
            ]
            assert producer.@allConsumersByName == [
                    consumer3,
                    consumer4
            ]

            1 * registry.getService(NonExistingService) >> null
            1 * registry.getService('noExistingService', NonExistingService) >> null
            1 * registry.getServices(NonExistingService) >> []
            1 * registry.getServices('noExistingService', NonExistingService) >> []
    }

    static interface IProducer {}

    static interface IConsumer {}

    static interface INonExistingService {}

    static final class Producer implements IProducer {
        @InjectService
        private IConsumer consumerByInterface

        @InjectService(serviceName = "consumerNameSingle")
        private IConsumer consumerByName1

        @InjectService(serviceName = "consumerNameDouble")
        private IConsumer consumerByName2

        @InjectService
        private IConsumer[] allConsumersByInterface

        @InjectService(serviceName = "consumerNameDouble")
        private IConsumer[] allConsumersByName

        @InjectService(serviceName = "noExistingService")
        private NonExistingService noExistingServiceByName

        @InjectService
        private NonExistingService noExistingServiceByInterface


        @InjectService(serviceName = "noExistingService")
        private NonExistingService[] noExistingServicesByName

        @InjectService
        private NonExistingService[] noExistingServicesByInterface
    }

    static final class Consumer implements IConsumer {}

    static final class NonExistingService implements INonExistingService {}

}
