# 基础镜像
FROM openjdk:8

# 设定时区
ENV TZ=Asia/Shanghai
# RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
#设置系统编码
RUN yum install kde-l10n-Chinese -y
RUN yum install glibc-common -y
RUN localedef -c -f UTF-8 -i zh_CN zh_CN.utf8
#RUN export LANG=zh_CN.UTF-8
#RUN echo "export LANG=zh_CN.UTF-8" >> /etc/locale.conf
#ENV LANG zh_CN.UTF-8
ENV LC_ALL zh_CN.UTF-8

# 拷贝 jar 包
COPY websocket_manager-0.0.1-SNAPSHOT.jar /websocket_manager.jar

# 入口
ENTRYPOINT ["java","-jar","/websocket_manager.jar"]