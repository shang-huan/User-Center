# FROM: 基础镜像，基于jdk8镜像开始
FROM openjdk:8

# COPY: 将应用的配置文件也拷贝到镜像中。
COPY *.jar /app.jar

CMD ["--server.port=8090"]

# EXPOSE：声明端口
EXPOSE 8090

ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=prod"]
# ENTRYPOINT：docker启动时，运行的命令，这里容器启动时直接运行jar服务。
