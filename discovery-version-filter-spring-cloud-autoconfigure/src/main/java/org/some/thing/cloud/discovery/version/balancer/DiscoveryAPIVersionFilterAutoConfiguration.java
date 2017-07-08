package org.some.thing.cloud.discovery.version.balancer;

import feign.Client;
import org.some.thing.cloud.discovery.version.balancer.filter.ServiceAPIVersionServerListFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.some.thing.cloud.discovery.version.balancer.filter.ServiceAPIVersionFilterProperties;

@Configuration
@ConditionalOnBean({Client.class, DiscoveryClient.class})
@EnableConfigurationProperties(value = ServiceAPIVersionFilterProperties.class)
public class DiscoveryAPIVersionFilterAutoConfiguration {

  @Bean
  public ServiceAPIVersionServerListFilter versionedFilter(DiscoveryClient discoveryClient, ServiceAPIVersionFilterProperties properties) {
    return new ServiceAPIVersionServerListFilter(properties.getServiceVersions(), discoveryClient);
  }
}
