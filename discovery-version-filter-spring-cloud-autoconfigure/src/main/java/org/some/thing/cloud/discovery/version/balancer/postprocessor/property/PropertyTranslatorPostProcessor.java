package org.some.thing.cloud.discovery.version.balancer.postprocessor.property;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

public class PropertyTranslatorPostProcessor implements EnvironmentPostProcessor {
  public static final String DEFAULT_VERSION_PROP = "eureka.instance.metadataMap.versions";
  public static final String CUSTOM_VERSION_PROP = "spring.discovery.api.versions";
  public static final String CLIENT_PROPERTY_SOURCE_NAME = "CUSTOM_DISCOVERY_PROPERTY_SOURCE";


  public static final String CUSTOM_SERVICE_VERSIONS_PROP = "spring.discovery.filter.versions";
  public static final String ZUUL_SERVICE_VERSIONS_ROOT = "zuul.routes";
  public static final String ZUUL_SERVICE_VERSIONS_PROP = "versions";
  public static final String ZUUL_SERVICE_ID_PROP = "service-id";
  public static final String ZUUL_PROPERTY_SOURCE_NAME = "CUSTOM_ZUUL_PROPERTY_SOURCE";

  private Map<String, Map<String, String>> map = new HashMap<>();

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
    translateClientVersionProperty(environment);
    translateZuulRoutes(environment);
  }

  @SuppressWarnings("unchecked")
  private void translateZuulRoutes(ConfigurableEnvironment environment) {

    //TODO should be fixed to multiple propertySource usage
    MapPropertySource zuulSource = findZuulPropertySource(environment);
    if (zuulSource == null) {
      return;
    }

    Map<String, String> customServiceVersions = new HashMap<>();
    for (String name : zuulSource.getPropertyNames()) {
      extractServiceVersion(zuulSource, name, customServiceVersions);
    }

    Map<String, Object> properties = new HashMap<>();
    properties.put(CUSTOM_SERVICE_VERSIONS_PROP, customServiceVersions);
    MapPropertySource target = new MapPropertySource(ZUUL_PROPERTY_SOURCE_NAME, properties);
    environment.getPropertySources().addFirst(target);
  }

  private MapPropertySource findZuulPropertySource(ConfigurableEnvironment environment) {
    for (PropertySource<?> propertySource : environment.getPropertySources()) {
      if (propertySource instanceof MapPropertySource) {
        for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
          if (key.toLowerCase().startsWith(ZUUL_SERVICE_VERSIONS_ROOT)) {
            return (MapPropertySource) propertySource;
          }
        }
      }
    }
    return null;
  }

  private void extractServiceVersion(MapPropertySource zuulSource, String name, Map<String,String> targetMap) {
    if (!name.toLowerCase().startsWith(ZUUL_SERVICE_VERSIONS_ROOT)) {
      return;
    }
    if (!name.toLowerCase().contains(ZUUL_SERVICE_ID_PROP)) {
      return;
    }
    String baseName = name.replaceAll("." + ZUUL_SERVICE_ID_PROP, "");
    if (!zuulSource.containsProperty(baseName + "." + ZUUL_SERVICE_VERSIONS_PROP))
      return;
    String versions = (String) zuulSource.getProperty(baseName + "." + ZUUL_SERVICE_VERSIONS_PROP);
    String serviceId = (String) zuulSource.getProperty(baseName + "." + ZUUL_SERVICE_ID_PROP);

    targetMap.put(serviceId, versions);
  }

  private void translateClientVersionProperty(ConfigurableEnvironment environment) {
    if(!environment.containsProperty(CUSTOM_VERSION_PROP)) {
      return;
    }
    String versions = environment.getProperty(CUSTOM_VERSION_PROP);
    Map<String, Object> properties = new HashMap<>();
    properties.put(DEFAULT_VERSION_PROP, versions);
    MapPropertySource target = new MapPropertySource(CLIENT_PROPERTY_SOURCE_NAME, properties);
    environment.getPropertySources().addFirst(target);
  }
}