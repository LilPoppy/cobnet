# Graal-native image
# https://container-registry.oracle.com
FROM container-registry.oracle.com/os/oraclelinux:8-slim

ARG TARGET_BUILD
ARG ARTIFACTID
ARG VERSION
ARG AUTHORS
ARG AUTHORS_EMAIL
ARG SOURCE_REPO

ARG JAVA_VERSION
ARG GRAALVM_VERSION
ARG GRAALVM_PKG=https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-$GRAALVM_VERSION/graalvm-ce-$JAVA_VERSION-GRAALVM_ARCH-$GRAALVM_VERSION.tar.gz

ARG PORT
ARG WEBSOCKET_PORT

ARG DB_DATABASE
ARG DB_AUTO_RECONNECT
ARG DB_USE_SSL
ARG DB_HOST
ARG DB_PORT
ARG DB_USER
ARG DB_PASSWORD
ARG DB_ROOT_PASSWORD
ARG REDIS_HOST
ARG REDIS_PORT
ARG REDIS_PASSWORD

LABEL \
    com.storechain.server.image.title=${ARTIFACTID} \
    com.storechain.server.image.version=${VERSION} \
    com.storechain.server.image.authors='$AUTHORS<$AUTHORS_EMAIL>' \
    com.storechain.server.image.url='https://github.com/lilpoppy' \
    com.storechain.server.image.source='$SOURCE_REPO'

ARG RPM_REPO
ARG RPM_CODEREADY_BUILDER
ARG INSTALLATIONS

RUN microdnf update -y $RPM_REPO \
    && microdnf --enablerepo $RPM_CODEREADY_BUILDER install $INSTALLATIONS \
    vi which xz-devel zlib-devel findutils glibc-static libstdc++ libstdc++-devel libstdc++-static zlib-static maven \
    && microdnf clean all \
    && fc-cache -f -v

ARG WORK_DIR

ENV LANG=en_US.UTF-8 \
    JAVA_HOME=/opt/graalvm-ce-${JAVA_VERSION}-${GRAALVM_VERSION} \
    TARGET="./bin/start.sh ${TARGET_BUILD} ${ARTIFACTID}"

ADD bin/gu-wrapper.sh /usr/local/bin/gu

ARG TARGETPLATFORM

RUN set -eux \
    && if [ "$TARGETPLATFORM" == "linux/amd64" ]; then GRAALVM_PKG=${GRAALVM_PKG/GRAALVM_ARCH/linux-amd64}; fi \
    && if [ "$TARGETPLATFORM" == "linux/arm64" ]; then GRAALVM_PKG=${GRAALVM_PKG/GRAALVM_ARCH/linux-aarch64}; fi \
    && curl --fail --silent --location --retry 3 ${GRAALVM_PKG} \
    | gunzip | tar x -C /opt/ \
    && mkdir -p "/usr/java" \
    && ln -sfT "$JAVA_HOME" /usr/java/default \
    && ln -sfT "$JAVA_HOME" /usr/java/latest \
    && for bin in "$JAVA_HOME/bin/"*; do \
    base="$(basename "$bin")"; \
    [ ! -e "/usr/bin/$base" ]; \
    alternatives --install "/usr/bin/$base" "$base" "$bin" 20000; \
    done \
    && chmod +x /usr/local/bin/gu

RUN gu install native-image && gu install python && native-image --version

ADD . $WORK_DIR

WORKDIR $WORK_DIR

RUN \
    mvn versions:set-property -Dproperty=docker-target -DnewVersion=${TARGET_BUILD} -DgenerateBackupPoms=false; \
    mvn versions:set-property -Dproperty=db-database -DnewVersion=${DB_DATABASE} -DgenerateBackupPoms=false; \
    mvn versions:set-property -Dproperty=db-auto-reconnect -DnewVersion=${DB_AUTO_RECONNECT} -DgenerateBackupPoms=false; \
    mvn versions:set-property -Dproperty=db-use-ssl -DnewVersion=${DB_USE_SSL} -DgenerateBackupPoms=false; \
    mvn versions:set-property -Dproperty=db-host -DnewVersion=${DB_HOST} -DgenerateBackupPoms=false; \
    mvn versions:set-property -Dproperty=db-port -DnewVersion=${DB_PORT} -DgenerateBackupPoms=false; \
    mvn versions:set-property -Dproperty=db-user -DnewVersion=${DB_USER} -DgenerateBackupPoms=false; \
    mvn versions:set-property -Dproperty=db-password -DnewVersion=${DB_PASSWORD} -DgenerateBackupPoms=false; \
    mvn versions:set-property -Dproperty=db-root-password -DnewVersion=${DB_ROOT_PASSWORD} -DgenerateBackupPoms=false; \
    mvn versions:set-property -Dproperty=redis-host -DnewVersion=${REDIS_HOST} -DgenerateBackupPoms=false; \
    mvn versions:set-property -Dproperty=redis-port -DnewVersion=${REDIS_PORT} -DgenerateBackupPoms=false; \
    mvn versions:set-property -Dproperty=redis-password -DnewVersion=${REDIS_PASSWORD} -DgenerateBackupPoms=false; \
    if [ "${TARGET_BUILD}" == "native" ]; then \
    mvn package -Pnative; fi \
    && if [ "${TARGET_BUILD}" == "jvm" ]; then \
    mvn package; fi

RUN ls -1 | grep -E -iwv 'target|bin|docker-compose.yml' | xargs rm -f -r \
    && chmod +x ./bin/start.sh

RUN ls && pwd

EXPOSE $PORT
EXPOSE $WEBSOCKET_PORT

MAINTAINER $AUTHORS

ENTRYPOINT [ "/bin/bash", "-c", "${TARGET}" ]

RUN echo "All done!"
