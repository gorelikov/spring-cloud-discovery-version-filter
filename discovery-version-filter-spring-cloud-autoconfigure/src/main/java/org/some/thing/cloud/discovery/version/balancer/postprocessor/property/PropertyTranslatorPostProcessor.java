package org.some.thing.cloud.discovery.version.balancer.postprocessor.property;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PropertyTranslatorPostProcessor implements EnvironmentPostProcessor {
  private static final String DEFAULT_VERSION_PROP = "eureka.instance.metadataMap.versions";
  private static final String CUSTOM_VERSION_PROP = "spring.discovery.api.versions";
  private static final String CLIENT_PROPERTY_SOURCE_NAME = "CUSTOM_DISCOVERY_PROPERTY_SOURCE";


  private static final String CUSTOM_SERVICE_VERSIONS_PROP = "spring.discovery.filter.versions";
  private static final String ZUUL_SERVICE_VERSIONS_ROOT = "zuul.routes";
  private static final String ZUUL_SERVICE_VERSIONS_PROP = "versions";
  private static final String ZUUL_SERVICE_ID_PROP = "service-id";

  private Map<String,Map<String,String>> map = new HashMap<>();

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
    translateClientVersionProperty(environment);
    translateZuulRoutes(environment);
  }

  @SuppressWarnings("unchecked")
  private void translateZuulRoutes(ConfigurableEnvironment environment) {
    if(!environment.containsProperty(ZUUL_SERVICE_VERSIONS_ROOT)) {
      return;
    }
    Map<String,Map<String,String>> routes = environment.getProperty(ZUUL_SERVICE_VERSIONS_ROOT, map.getClass());
    Map<String,String> customServiceVersions = routes.entrySet().stream()
        .filter(entry -> entry.getValue().containsKey(ZUUL_SERVICE_VERSIONS_PROP))
        .map(Map.Entry::getValue)
        .collect(Collectors.toMap(entry -> entry.get(ZUUL_SERVICE_ID_PROP), entry -> entry.get(ZUUL_SERVICE_VERSIONS_PROP)));

    Map<String,Object> properties = new HashMap<>();
    properties.put(CUSTOM_SERVICE_VERSIONS_PROP, customServiceVersions);
  }

  private void translateClientVersionProperty(ConfigurableEnvironment environment) {
    if(!environment.containsProperty(CUSTOM_VERSION_PROP)) {
      return;
    }
    String versions = environment.getProperty(CUSTOM_VERSION_PROP);
    Map<String,Object> properties = new HashMap<>();
    properties.put(DEFAULT_VERSION_PROP, versions);
    MapPropertySource target = new MapPropertySource(CLIENT_PROPERTY_SOURCE_NAME, properties);
    environment.getPropertySources().addFirst(target);
  }
}