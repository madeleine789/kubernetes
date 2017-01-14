FROM java:8
VOLUME /tmp
ADD ./target/master-node-0.1.0.jar master.jar
RUN bash -c 'touch /master.jar'
EXPOSE 8080
CMD ["/usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-jar", "master.jar"]