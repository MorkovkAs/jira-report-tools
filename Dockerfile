# =====
# BUILD
# =====

FROM gradle:7.3.2-jdk17 AS build

WORKDIR /app

#RUN useradd --create-home --shell /bin/bash builduser
RUN useradd -s /bin/bash builduser && usermod --home /app builduser && chown -R builduser /app

USER builduser

ENV GRADLE_ARGS="-Dfile.encoding=UTF-8"

COPY --chown=builduser settings.gradle gradle.properties gradlew /app/
COPY --chown=builduser gradle/ /app/gradle

COPY --chown=builduser . .

RUN gradle bootJar
#RUN gradle allClean bootJar

# =========
# BUILD END
# =========

FROM bellsoft/liberica-openjdk-centos:17.0.4.1-1 AS runtime

COPY --from=build app/report-service/build/libs/report-service-*.jar /app/report-service.jar

WORKDIR /app

EXPOSE 8080/tcp

CMD java -jar report-service.jar