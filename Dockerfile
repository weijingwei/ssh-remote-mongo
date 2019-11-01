FROM adoptopenjdk/openjdk8
MAINTAINER biptwjw

ARG MAVEN_VERSION=3.6.2
ARG USER_HOME_DIR="/root"
ARG SHA=d941423d115cd021514bfd06c453658b1b3e39e6240969caf4315ab7119a77299713f14b620fb2571a264f8dff2473d8af3cb47b05acf0036fc2553199a5c1ee
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha512sum -c - \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

COPY mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
COPY settings-docker.xml /usr/share/maven/ref/

ENV JAVA_HEAP_OPTIONS="-Xms2048m -Xmx2048m"
ENV JAVA_GC_OPTIONS="-XX:+UseG1GC"
ENV JAVA_EXTRA_OPTIONS=""



RUN mkdir -p /opt/app /opt/app/conf /opt/app/logs /tmp/pubnub
COPY . /tmp/pubnub
RUN mvn install -f /tmp/pubnub
RUN ls /tmp/pubnub/target
RUN cp /tmp/pubnub/target/classes/application.properties /opt/app/conf/
RUN cp /tmp/pubnub/target/*.jar /opt/app/app.jar
RUN rm -rf /tmp/pubnub
EXPOSE 9999
WORKDIR /opt/app
CMD exec java ${JAVA_HEAP_OPTIONS} ${JAVA_GC_OPTIONS} ${JAVA_EXTRA_OPTIONS} \
        -Dlogging.path=/opt/app/logs \
        -Dlogging.file=/opt/app/logs/application.log \
      	-jar app.jar \
      	--spring.config.location=file:./conf/application.properties
