Storechain-Server
============

[![CI](https://github.com/SpamanDev/StoreChain-Server/actions/workflows/main.yml/badge.svg?branch=main)](https://github.com/SpamanDev/StoreChain-Server/actions/workflows/main.yml)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![versionjava](https://img.shields.io/badge/dynamic/xml?color=brightgreen&url=https://raw.githubusercontent.com/SpamanDev/StoreChain-Server/master/pom.xml&query=%2F%2F%2A%5Blocal-name%28%29%3D%27java.version%27%5D&label=java)](https://github.com/openjdk/jdk)
[![versionspringboot](https://img.shields.io/badge/dynamic/xml?color=brightgreen&url=https://raw.githubusercontent.com/SpamanDev/StoreChain-Server/master/pom.xml&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27parent%27%5D%2F%2A%5Blocal-name%28%29%3D%27version%27%5D&label=springboot)](https://github.com/spring-projects/spring-boot)
[![versionspring-graalvm-native](https://img.shields.io/badge/dynamic/xml?color=brightgreen&url=https://raw.githubusercontent.com/SpamanDev/StoreChain-Server/master/pom.xml&query=%2F%2F%2A%5Blocal-name%28%29%3D%27graalvm.version%27%5D&label=graalvm)](https://github.com/spring-projects-experimental/spring-graalvm-native)
[![fork this repo](https://githubbadges.com/fork.svg?user=SpamanDev&repo=StoreChain-Server&style=default)](https://github.com/SpamanDev/StoreChain-Server/fork)
[![GitHub issues](https://img.shields.io/github/issues/SpamanDev/StoreChain-Server.svg)](https://github.com/SpamanDev/StoreChain-Server/issues)

<!--[![Docker](https://img.shields.io/docker/cloud/build/SpamanDev/StoreChain-Server?label=Docker&style=flat)](https://hub.docker.com/r/SpamanDev/StoreChain-Server/builds)-->

This is a SpringCloud euraka client project that can be quickly deployed.

# Read Me First
The following was discovered as part of building this project:

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.6.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.6.1/maven-plugin/reference/html/#build-image)
* [Spring Native Reference Guide](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/)
* [Spring Data JDBC](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.6.1/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.6.1/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Cloud Eureka Client](https://docs.spring.io/spring-cloud-netflix/docs/current/reference/html/#spring-cloud-eureka-client)

### Guides
The following guides illustrate how to use some features concretely:

* [Using Spring Data JDBC](https://github.com/spring-projects/spring-data-examples/tree/master/jdbc/basics)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Spring Boot with Graalvm](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#support-spring-boot)
* [Spring Cloud with Graalvm](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#support-spring-cloud)

### Additional Links
These additional references should also help you:

* [Configure the Spring AOT Plugin](https://docs.spring.io/spring-native/docs/0.11.0/reference/htmlsingle/#spring-aot-maven)

### JVM

* [GraalVM-CE-JAVA](https://www.graalvm.org/downloads/) needs to be downloaded.

This project can be easily build with maven, run the following goal:

```
$ mvn clean package
```

Then run the followings to start the server:

```
$ java -jar target/storechain-server.jar
```

## Spring Native

This project has been configured to let you generate either a lightweight container or a native executable.

### Lightweight Container with Cloud Native Buildpacks
If you're already familiar with Spring Boot container images support, this is the easiest way to get started with Spring Native.
Docker should be installed and configured on your machine prior to creating the image, see [the Getting Started section of the reference guide](https://docs.spring.io/spring-native/docs/0.11.0/reference/htmlsingle/#getting-started-buildpacks).

* [Docker](https://docs.docker.com/get-docker/) atlease 10GB memory in docker preferences. see [Out of memory error when building the native image](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#_out_of_memory_error_when_building_the_native_image)

* [Docker-Compose](https://docs.docker.com/compose/install/) 18.06.0+ needs to be installed.

To create the docker image and container by compose, run the following goal:

```
$ mvn clean package -Pdocker
```
To start the app you can run as follows:

```
$ docker start storechain-server
```

### Executable with Native Build Tools
Use this option if you want to explore more options such as running your tests in a native image.
The GraalVM native-image compiler should be installed and configured on your machine, see [the Getting Started section of the reference guide](https://docs.spring.io/spring-native/docs/0.11.0/reference/htmlsingle/#getting-started-native-build-tools).

* [Graalvm-Native](https://www.graalvm.org/docs/getting-started/) needs to be installed.
* [Reflection Use in Native Images](https://www.graalvm.org/reference-manual/native-image/Reflection/).
* To [add a language](https://www.graalvm.org/reference-manual/native-image/Options/#macro-options) to native image.

To create the executable, run the following goal:

```
$ mvn clean package -Pnative
```

Then, you can run the app as follows:

```
$ target/storechain-server
```
