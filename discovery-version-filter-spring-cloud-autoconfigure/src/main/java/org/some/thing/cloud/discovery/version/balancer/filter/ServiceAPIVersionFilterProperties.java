package org.some.thing.cloud.discovery.version.balancer.filter;


import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "spring.discovery.filter")
public class ServiceAPIVersionFilterProperties {
  @Setter(AccessLevel.PRIVATE)
  private Map<String, List<String>> serviceVersions;
  private Map<String, String> versions;

  @PostConstruct
  public void init() {
    this.serviceVersions = versions.entrySet().stream()
        .collect(Collectors.toMap(ver -> ver.getKey().toLowerCase(),
            ver -> Arrays.asList(ver.getValue().toLowerCase().split(","))));
  }
}
