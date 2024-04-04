# Example of custom Java runtime using jlink in a multi-stage container build
FROM eclipse-temurin:21 as jre-build

# Call jdeps before jlink
# unpack jar into app-dependencies
# jdeps --ignore-missing-deps -q --recursive --multi-release 21 --print-module-deps --class-path 'app-dependencies/BOOT-INF/lib/*' app.jar

# Create a custom Java runtime
RUN $JAVA_HOME/bin/jlink \
         #base modules
         --add-modules java.base,java.compiler,java.desktop,java.instrument,java.management,java.net.http,java.prefs,java.rmi,java.scripting,java.security.jgss,java.security.sasl,java.sql.rowset,jdk.jfr,jdk.unsupported \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime

# Define your base image
FROM debian:buster-slim
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /javaruntime $JAVA_HOME

# Continue with your application deployment
RUN mkdir /opt/app
COPY /target/patient-domain-service-0.0.1-SNAPSHOT.jar /opt/app
CMD ["java", "-jar", "/opt/app/patient-domain-service-0.0.1-SNAPSHOT.jar"]