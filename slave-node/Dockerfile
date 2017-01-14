FROM java:8
VOLUME /tmp
ADD ./target/slave-node-0.1.0.jar slave.jar
RUN bash -c 'touch /slave.jar'
EXPOSE 8081
CMD ["/usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-jar", "slave.jar"]