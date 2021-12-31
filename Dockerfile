ARG ORACLE_VERSION

# https://container-registry.oracle.com
FROM container-registry.oracle.com/os/oraclelinux:${ORACLE_VERSION}

ARG ORACLE_VERSION

ARG ARTIFACTID
ARG VERSION
ARG AUTHORS
ARG AUTHORS_EMAIL
ARG SOURCE_REPO

ARG JAVA_VERSION
ARG GRAALVM_VERSION
ARG GRAALVM_PKG=https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-$GRAALVM_VERSION/graalvm-ce-$JAVA_VERSION-GRAALVM_ARCH-$GRAALVM_VERSION.tar.gz

ARG WORK_DIR

ARG PORT

LABEL \
    com.storechain.server.image.title=${ARTIFACTID} \
    com.storechain.server.image.version=${VERSION} \
    com.storechain.server.image.authors='$AUTHORS<$AUTHORS_EMAIL>' \
    com.storechain.server.image.url='https://github.com/lilpoppy' \
    com.storechain.server.image.source='$SOURCE_REPO'

ARG RPM=yum

RUN if [[ "$ORACLE_VERSION" == *"slim"* ]]; then RPM=microdnf; fi \
		&& $RPM update -y oraclelinux-release-el8 \
    && $RPM --enablerepo ol8_codeready_builder install bzip2-devel zip unzip ed gcc gcc-c++ gcc-gfortran gzip file fontconfig less libcurl-devel make openssl apr-devel openssl-devel readline-devel tar glibc-langpack-en \
    vi which xz-devel zlib-devel findutils glibc-static libstdc++ libstdc++-devel libstdc++-static zlib-static \
    && $RPM clean all \
    && $RPM install wget \
    && fc-cache -f -v
    

ENV LANG=en_US.UTF-8 \
    JAVA_HOME=/opt/graalvm-ce-$JAVA_VERSION-$GRAALVM_VERSION/

ADD gu-wrapper.sh /usr/local/bin/gu

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
   
	
ADD . $WORK_DIR

WORKDIR $WORK_DIR

RUN \
    curl -s "https://get.sdkman.io" | bash; \
    source "$HOME/.sdkman/bin/sdkman-init.sh"; \
    sdk install maven;
    
RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && mvn --version

RUN gu install native-image && native-image --version
    
CMD java -version

RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && mvn -Pnative -e -X clean package

MAINTAINER $AUTHORS

COPY --from=0 "/build/target/$ARTIFACTID" $ARTIFACTID

# docker run -p 8089:8089 storechain-server
CMD [ "sh", "-c", "./$ARTIFACTID -Dserver.port=$PORT" ]

RUN ifconfig


