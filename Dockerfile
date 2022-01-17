FROM wangray4j/dpp-flink-on-k8s:1.0

COPY ./build/libs/dpp-service-0.2.1-incubation.jar /app/dpp-service-0.2.1-dev.jar
COPY ./lib/dap-plug-k8s.jar /data/flink/jar/sql/dap-plug-k8s.jar
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
ENTRYPOINT java -jar /app/dpp-service-0.2.1-dev.jar
