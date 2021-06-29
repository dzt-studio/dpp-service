FROM openjdk:11.0.10-slim

COPY ./build/libs/dpp-service-0.0.1-incubation.jar /app/dpp-service-0.1.0-dev.jar
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
ENTRYPOINT java -jar /app/dpp-service-0.1.0-dev.jar
