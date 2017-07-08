package org.some.thing.cloud.discovery.version.balancer.filter;

import com.netflix.loadbalancer.Server;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ServiceAPIVersionServerListFilterTest {

  private final DiscoveryClient clientMock  = Mockito.mock(DiscoveryClient.class);

  @Test
  public void getFilteredListOfServers() throws Exception {
    final String hostname = "hostname";
    final String version = "1.0";
    final int port = 9999;
    final String serviceId = "serviceid";

    List<Server> servers = createServers(hostname, port, serviceId);

    Mockito.when(clientMock.getInstances(serviceId)).thenReturn(Arrays.asList(
        createServiceInstance(serviceId, servers.get(0).getHost(),servers.get(0).getPort(), version),
        createServiceInstance(serviceId, servers.get(1).getHost(), servers.get(1).getPort(), version),
        createServiceInstance(serviceId, servers.get(2).getHost(), servers.get(2).getPort(), "2.0"),
        createServiceInstance(serviceId, servers.get(3).getHost(), servers.get(3).getPort(), "3.0")
    ));

    ServiceAPIVersionServerListFilter filter = new ServiceAPIVersionServerListFilter(createVersionMap(serviceId, version), clientMock);
    List<Server> result = filter.getFilteredListOfServers(servers);

    Assert.assertTrue(result.size() == 2 );
    Assert.assertTrue(result.contains(servers.get(0)));
    Assert.assertTrue(result.contains(servers.get(1)));
  }


  private List<Server> createServers(String hostname, int port, String serviceId) {
    return  IntStream.range(0,4)
        .mapToObj(i -> new TestServer(hostname, port+i, serviceId))
        .collect(Collectors.toList());
  }

  private Map<String, List<String>> createVersionMap(String serviceId, String version) {
    Map<String,List<String>> versions = new HashMap<>();
    versions.put(serviceId, Collections.singletonList(version));
    return versions;
  }

  private SimpleTestServiceInstance createServiceInstance(String serviceId, String hostname, int port, String version) {
    Map<String,String> metaDataMap = new HashMap<>();
    metaDataMap.put(ServiceAPIVersionServerListFilter.VERSION_FIELD, version);
    return SimpleTestServiceInstance.builder()
        .host(hostname)
        .port(port)
        .serviceId(serviceId)
        .metadata(metaDataMap).build();
  }

  @Data
  @Builder
  @AllArgsConstructor
  public static class SimpleTestServiceInstance implements ServiceInstance {
    private String host;
    private int port;
    private String serviceId;
    private Map<String,String> metadata;
    private URI uri;
    private boolean secure;
  }

  @Data
  public static class TestServer extends Server {
    private MetaInfo metaInfo;
    public TestServer(String host, int port, String id) {
      super(host, port);
      this.setMetaInfo(new MetaInfo() {
        @Override
        public String getAppName() {
          return null;
        }

        @Override
        public String getServerGroup() {
          return null;
        }

        @Override
        public String getServiceIdForDiscovery() {
          return id;
        }

        @Override
        public String getInstanceId() {
          return null;
        }
      });
    }
  }
}