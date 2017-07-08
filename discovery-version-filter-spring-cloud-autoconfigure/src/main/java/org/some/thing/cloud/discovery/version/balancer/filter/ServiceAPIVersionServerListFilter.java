package org.some.thing.cloud.discovery.version.balancer.filter;

import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAffinityServerListFilter;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServiceAPIVersionServerListFilter extends ZoneAffinityServerListFilter<Server> {
  private final Map<String, List<String>> versions;
  private final DiscoveryClient discoveryClient;

  public static final String VERSION_FIELD = "versions";

  public ServiceAPIVersionServerListFilter(Map<String, List<String>> versions, DiscoveryClient discoveryClient) {
    this.versions = versions;
    this.discoveryClient = discoveryClient;
  }

  @Override
  public List<Server> getFilteredListOfServers(List<Server> listOfServers) {
    if (listOfServers == null || listOfServers.isEmpty())
      return listOfServers;
    List<ServiceInstance> instanceInfos = this.discoveryClient.getInstances(listOfServers.get(0).getMetaInfo().getServiceIdForDiscovery());
    final List<ServiceInstance> versionedInstance = instanceInfos.stream()
        .filter(instanceInfo -> !versions.containsKey(instanceInfo.getServiceId().toLowerCase())
            || checkVersion(versions.get(instanceInfo.getServiceId().toLowerCase()), instanceInfo.getMetadata().get(VERSION_FIELD)))
        .collect(Collectors.toList());
    return listOfServers.stream()
        .filter(server -> checkServiceInstance(server, versionedInstance))
        .collect(Collectors.toList());

  }

  private static boolean checkServiceInstance(Server server, List<ServiceInstance> serviceInstance) {
    return serviceInstance.stream()
        .anyMatch(instance -> instance.getPort() == server.getPort()
            && instance.getHost().equalsIgnoreCase(server.getHost()));
  }

  private static boolean checkVersion(List<String> expected, String actual) {
    if(StringUtils.isEmpty(actual))
      return true;
    return Stream.of(actual.toLowerCase().split(",")).anyMatch(act -> expected.contains(act));
  }
}
