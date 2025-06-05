FROM openjdk:17-slim
WORKDIR /app
RUN apt-get update && apt-get install -y wget && \
    groupadd -r spring && useradd -r -g spring spring && \
    chown -R spring:spring /app
USER spring
COPY target/*.jar app.jar
EXPOSE 8087
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --spider -q http://localhost:8087/actuator/health || exit 1


