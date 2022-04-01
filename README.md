Cobnet
============

[![CI](https://github.com/StorebeansDev/cobnet/actions/workflows/main.yml/badge.svg?branch=main)](https://github.com/StorebeansDev/cobnet/actions/workflows/main.yml)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![versionjava](https://img.shields.io/badge/dynamic/xml?color=brightgreen&url=https://raw.githubusercontent.com/StorebeansDev/cobnet/master/pom.xml&query=%2F%2F%2A%5Blocal-name%28%29%3D%27java.version%27%5D&label=java)](https://github.com/openjdk/jdk)
[![versionspringboot](https://img.shields.io/badge/dynamic/xml?color=brightgreen&url=https://raw.githubusercontent.com/StorebeansDev/cobnet/master/pom.xml&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27parent%27%5D%2F%2A%5Blocal-name%28%29%3D%27version%27%5D&label=spring-boot)](https://github.com/spring-projects/spring-boot)
[![versionspringcloud](https://img.shields.io/badge/dynamic/xml?color=brightgreen&url=https://raw.githubusercontent.com/StorebeansDev/cobnet/master/pom.xml&query=%2F%2F%2A%5Blocal-name%28%29%3D%27spring-cloud.version%27%5D&label=spring-cloud)](https://spring.io/projects/spring-cloud)
[![versiongraalvm](https://img.shields.io/badge/dynamic/xml?color=brightgreen&url=https://raw.githubusercontent.com/StorebeansDev/cobnet/master/pom.xml&query=%2F%2F%2A%5Blocal-name%28%29%3D%27graalvm.version%27%5D&label=graalvm)](https://github.com/oracle/graal/)
[![versionspring-graalvm](https://img.shields.io/badge/dynamic/xml?color=brightgreen&url=https://raw.githubusercontent.com/StorebeansDev/cobnet/master/pom.xml&query=%2F%2F%2A%5Blocal-name%28%29%3D%27spring-native.version%27%5D&label=spring-native)](https://github.com/spring-projects-experimental/spring-graalvm-native)
[![fork this repo](https://githubbadges.com/fork.svg?user=StorebeansDev&repo=cobnet&style=default)](https://github.com/StorebeansDev/cobnet/fork)
[![GitHub issues](https://img.shields.io/github/issues/StorebeansDev/cobnet.svg)](https://github.com/StorebeansDev/cobnet/issues)

<!--[![Docker](https://img.shields.io/docker/cloud/build/StorebeansDev/cobnet?label=Docker&style=flat)](https://hub.docker.com/r/StorebeansDev/cobnet/builds)-->

This is a spring-native polyglot server that can be quickly deployed.

# Getting Started

### JVM

This project is developed in GraalVM environment, so please configure the corresponding environment when you want to run this project as SVM, see 
[Get Started with GraalVM](https://www.graalvm.org/22.0/docs/getting-started/).

This project can be easily build with maven, run the following goal:

```
mvn clean package
```

Then run the followings to start the server:

```
sh ./start.sh
```
or 

```
java -jar target/cobnet.jar
```
## Spring Native

This project has been configured to let you generate either a lightweight container or a native executable.

### Configuration

Default source is already configured good to be build native or container. But if you have other modified we can use agent and some tool to help for configuration needs, to use these options the following needs to be setup:

* [MySQL](https://www.mysql.com) 

* [Redis](https://redis.io)

#### Native image agent

Native images are built ahead of runtime and their build relies on a static analysis of which code will be reachable.

First build a jvm mirror

```
mvn clean package
```

Then run with the agent

```
java -agentlib:native-image-agent=config-merge-dir=src/main/java/ -jar target/cobnet.jar agent
```

##### Auto proxy script

A script which written in [Python3](https://www.python.org/downloads/) to help you append Graal Native needed proxy into extra-proxy-config.json.

From project root directory run:
```
cd script
```
Then run the script:
```
python3 ProxyHelper.py
```

### Lightweight Container with Cloud Native Buildpacks
If you're already familiar with Spring Boot container images support, this is the easiest way to get started with Spring Native.
Docker should be installed and configured on your machine prior to creating the image, see [the Getting Started section of the reference guide](https://docs.spring.io/spring-native/docs/0.11.0/reference/htmlsingle/#getting-started-buildpacks).

* [Docker](https://docs.docker.com/get-docker/) atlease 10GB memory in docker preferences. see [Out of memory error when building the native image](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#_out_of_memory_error_when_building_the_native_image)

* [Docker-Compose](https://docs.docker.com/compose/install/) 18.06.0+ needs to be installed.

####To create the docker image and container by compose, there is two options.

#####JVM:

```
mvn -Dredis-host=redis -Ddb-host=db clean package -Pdocker -Ddocker-target=jvm
```

#####Native:

```
mvn -Dredis-host=redis -Ddb-host=db clean package -Pdocker -Ddocker-target=native
```

To start the app you can run as follows:

```
docker start -a cobnet
```

### Executable with Native Build Tools
Use this option if you want to explore more options such as running your tests in a native image.
The GraalVM native-image compiler should be installed and configured on your machine, see [the Getting Started section of the reference guide](https://docs.spring.io/spring-native/docs/0.11.0/reference/htmlsingle/#getting-started-native-build-tools).

* [Graalvm-Native](https://www.graalvm.org/docs/getting-started/) needs to be installed.
* [Reflection Use in Native Images](https://www.graalvm.org/reference-manual/native-image/Reflection/).
* To [add a language](https://www.graalvm.org/reference-manual/native-image/Options/#macro-options) to native image.

To create the executable, run the following goal:

```
mvn clean package -Pnative
```

Then, you can run the app as follows:

```
sh ./start.sh
```
or 

```
target/cobnet
```

### Reference Documentation
For further reference, please consider the following sections:
* [Spring Redis](https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#reference)
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.6.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.6.1/maven-plugin/reference/html/#build-image)
* [Spring Native Reference Guide](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/)
* [Spring Data JDBC](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.6.1/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.6.1/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Cloud Eureka Client](https://docs.spring.io/spring-cloud-netflix/docs/current/reference/html/#spring-cloud-eureka-client)
* [GraalVM](https://fossies.org/dox/graal-vm-21.3.0/index.html) and [Graalvm SDK](https://www.graalvm.org/sdk/javadoc/overview-summary.html)
### Guides
The following guides illustrate how to use some features concretely:

* [Using Spring Data JDBC](https://github.com/spring-projects/spring-data-examples/tree/master/jdbc/basics)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Spring Boot with Graalvm](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#support-spring-boot)
* [Spring Cloud with Graalvm](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#support-spring-cloud)
* [Graalvm Options](https://chriswhocodes.com/graalvm_native_image_ce_jdk17_options.html)

### Additional Links
These additional references should also help you:

* [Configure the Spring AOT Plugin](https://docs.spring.io/spring-native/docs/0.11.0/reference/htmlsingle/#spring-aot-maven)
