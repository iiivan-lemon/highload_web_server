FROM openjdk:11
WORKDIR /server
COPY . .

RUN javac src/*.java
CMD exec java $JAVA_OPTS -cp src ServerPool 80
