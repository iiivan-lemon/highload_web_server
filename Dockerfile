FROM openjdk:11
WORKDIR /server
COPY . .

RUN javac -sourcepath src -d bin -classpath bin/server.jar src/Main.java
CMD exec java $JAVA_OPTS -classpath bin:bin/server.jar Main 80
