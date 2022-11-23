RUN sed -i "s/archive.ubuntu./mirrors.aliyun./g" /etc/apt/sources.list
RUN sed -i "s/deb.debian.org/mirrors.aliyun.com/g" /etc/apt/sources.list

FROM openjdk:8

#作者信息
MAINTAINER huan.gao

#指定环境变量 工作目录
ENV WORKDIR "/app"

#暴露端口
EXPOSE 9001

ADD msf-boot.jar ${WORKDIR}

COPY ./config ${WORKDIR}/config
COPY ./db ${WORKDIR}/db
#启动 如果启动时想要添加额外的配置可以通过docker run -e PARAM="-Dserver.port=8000 -Dspring.profiles.active-prod"
ENTRYPOINT ["/bin/bash","-c","nohup java -Dfile.encoding=utf8 ${PARAM} -jar ${WORKDIR}/msf-boot.jar"]