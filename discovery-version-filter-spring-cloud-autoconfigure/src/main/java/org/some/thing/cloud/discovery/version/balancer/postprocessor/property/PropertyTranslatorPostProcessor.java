package org.some.thing.cloud.discovery.version.balancer.postprocessor.property;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class PropertyTranslatorPostProcessor implements EnvironmentPostProcessor {
  private static final String DEFAULT_VERSION_PROP = "eureka.instance.metadataMap.versions";
  private static final String CUSTOM_VERSION_PROP = "spring.discovery.api.versions";
  private static final String PROPERTY_SOURCE_NAME = "CUSTOM_DISCOVERY_PROPERTY_SOURCE";

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
    String versions = environment.getProperty(CUSTOM_VERSION_PROP);
    Map<String,Object> properties = new HashMap<>();
    properties.put(DEFAULT_VERSION_PROP, versions);
    MapPropertySource target = new MapPropertySource(PROPERTY_SOURCE_NAME, properties);
    environment.getPropertySources().addFirst(target);
  }
}
