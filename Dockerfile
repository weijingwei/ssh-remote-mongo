FROM biptwjw/mymaven
MAINTAINER biptwjw

RUN mkdir -p /opt/app /opt/app/conf /opt/app/logs /tmp/project
COPY . /tmp/project
RUN mvn install -f /tmp/project
RUN ls -l /tmp/project/target
RUN cp /tmp/project/target/classes/application.properties /opt/app/conf/
RUN cp /tmp/project/target/classes/id_rsa_atlas_aws /opt/app/conf/
RUN cp /tmp/project/target/*.jar /opt/app/app.jar
RUN rm -rf /tmp/project
RUN rm -rf /usr/share/maven/ref/settings-docker.xml
RUN rm -rf /usr/local/bin/mvn-entrypoint.sh
RUN rm -rf /usr/bin/mvn
RUN rm -rf /usr/share/maven
RUN rm -rf ~/.m2

EXPOSE 9999
WORKDIR /opt/app
CMD exec java ${JAVA_HEAP_OPTIONS} ${JAVA_GC_OPTIONS} ${JAVA_EXTRA_OPTIONS} \
        -Dlogging.path=/opt/app/logs \
        -Dlogging.file=/opt/app/logs/application.log \
      	-jar app.jar \
      	--spring.config.location=file:./conf/application.properties
