# 1. Aşama: Uygulamayı Oluşturma (Build Stage)
FROM eclipse-temurin:17-jdk-focal AS build
WORKDIR /app

# Maven'ı indirip kurma adımları
ARG MAVEN_VERSION=3.9.6
ARG USER_HOME_DIR="/root"
ARG BASE_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/
RUN set -eux; \
    mkdir -p /usr/share/maven /usr/share/maven/ref; \
    curl -fsSL ${BASE_URL}${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.tar.gz | tar -xzC /usr/share/maven --strip-components=1; \
    ln -s /usr/share/maven/bin/mvn /usr/bin/mvn;
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"


COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# 2. Aşama: Çalıştırma (Runtime Stage)
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]