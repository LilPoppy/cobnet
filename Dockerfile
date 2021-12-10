# docker pull ghcr.io/graalvm/graalvm-ce:latest
FROM ghcr.io/graalvm/graalvm-ce:latest

ADD . /build
WORKDIR /build

RUN yum install -y unzip zip

RUN \
    # Install SDKMAN
    curl -s "https://get.sdkman.io" | bash; \
    source "$HOME/.sdkman/bin/sdkman-init.sh"; \
    sdk install maven; \
    # Install GraalVM Native Image
    gu install native-image;

RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && mvn --version

RUN native-image --version

RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && mvn -B clean package -P native --no-transfer-progress

FROM oraclelinux:7-slim

MAINTAINER LilPoppy

COPY --from=0 "/build/target/spring-boot-graal" spring-boot-graal

# docker run -p 8080:8080 spring-boot-graal
CMD [ "sh", "-c", "./spring-boot-graal -Dserver.port=$PORT" ]