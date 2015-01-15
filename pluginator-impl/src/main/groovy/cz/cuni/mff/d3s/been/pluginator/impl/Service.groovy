package cz.cuni.mff.d3s.been.pluginator.impl

class Service<I, C extends I> {

    String serviceName

    Class<I> serviceInterface

    C serviceInstance

}
