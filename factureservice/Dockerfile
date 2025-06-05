FROM openjdk:17-slim
WORKDIR /app
COPY target/*.jar app.jar
RUN apt-get update && apt-get install -y wget && \
    groupadd -r spring && useradd -r -g spring spring && \
    chown -R spring:spring /app
USER spring
EXPOSE 8083
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --spider -q http://localhost:8083/actuator/health || exit 1


