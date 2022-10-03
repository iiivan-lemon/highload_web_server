FROM ubuntu:latest

RUN apt-get update && \
    apt-get install -y openjdk-11-jdk && \
    apt-get clean;

ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
ENV PATH="$JAVA_HOME/bin:${PATH}"
WORKDIR /server
COPY . .

RUN javac -sourcepath src -d bin -classpath bin/server.jar src/Main.java
CMD exec java $JAVA_OPTS -classpath bin:bin/server.jar Main 80
