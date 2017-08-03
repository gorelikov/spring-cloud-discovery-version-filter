package org.some.thing.cloud.discovery.version.balancer.postprocessor.property;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

import java.util.HashMap;

import static org.some.thing.cloud.discovery.version.balancer.postprocessor.property.PropertyTranslatorPostProcessor.CUSTOM_SERVICE_VERSIONS_PROP;
import static org.some.thing.cloud.discovery.version.balancer.postprocessor.property.PropertyTranslatorPostProcessor.CUSTOM_VERSION_PROP;
import static org.some.thing.cloud.discovery.version.balancer.postprocessor.property.PropertyTranslatorPostProcessor.DEFAULT_VERSION_PROP;

public class PropertyTranslatorPostProcessorTest {
  private static final String VERSIONS = "1.0,2.0";

  @Test
  public void postProcessClientVersionFilterEnvironmentTest() {
    //given
    MockEnvironment environment = new MockEnvironment();
    environment.withProperty(CUSTOM_VERSION_PROP, VERSIONS);

    PropertyTranslatorPostProcessor processor = new PropertyTranslatorPostProcessor();

    //process
    processor.postProcessEnvironment(environment, null);

    //check
    Assert.assertEquals(VERSIONS, environment.getProperty(DEFAULT_VERSION_PROP));
  }

  @Test
  public void postProcessZuulFilterEnvironmentTest() {
    final String ZUUL_ROOT = "zuul.routes";
    final String SERVICE_ID = "serviceID";
    //given
    MockEnvironment environment = new MockEnvironment();
    environment.withProperty(ZUUL_ROOT + "." + SERVICE_ID + ".service-id", SERVICE_ID)
        .withProperty(ZUUL_ROOT + "." + SERVICE_ID + ".versions", VERSIONS);

    PropertyTranslatorPostProcessor processor = new PropertyTranslatorPostProcessor();

    //process
    processor.postProcessEnvironment(environment, null);

    //check
    Assert.assertEquals(VERSIONS, environment.getProperty(CUSTOM_SERVICE_VERSIONS_PROP, HashMap.class).get(SERVICE_ID));
  }

}