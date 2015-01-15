package cz.cuni.mff.d3s.been.pluginator;

import cz.cuni.mff.d3s.been.pluginator.impl.Service;

import java.util.Collection;

public interface IServiceRegistry extends IServiceRegistrator {

    <I> I getService(Class<I> serviceInterface);

    <I> I getService(String serviceName, Class<I> serviceInterface);

    <I> I getServices(Class<I> serviceInterface);

    <I> I getServices(String serviceName, Class<I> serviceInterface);

    Collection<Service> getAllRegisteredServices();

}
