# Spring starter for service communication filtered by api version
Starter allows ribbon client to filter services' list according to configured version list

## How to use

### Dependecy
```
dependecies {
    compile 'org.some.thing.cloud.discovery:spring-cloud-starter-discovery-version-balancer:0.1.0'
}
```

### Configuration example
```
spring:
  discovery:
    filter:
      versions:
        someServiceId: v2
    api:
      versions: v1,v2
```
`spring.discovery.filter.version` - map of supported services' versions
`someServiceId`  - identifier of some service that will be called from the current service
`api.versions` - list of own apis' versions supported by the service

## How to build
`./gradlew clean build`