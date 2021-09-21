FROM bluezdrive/ubuntu-git-java15:1.0
ARG JAR_FILE

# Create directories
RUN mkdir -p /home/workspace
RUN mkdir -p /home/workspace/config
RUN mkdir -p /home/workspace/repository
COPY target/${JAR_FILE} /home/workspace/kafka-cluster-deployment-service.jar
RUN wget -O /home/workspace/kafka-cluster-manager.jar https://github.com/Bluezdrive/kafka-cluster-manager/releases/download/v2.6.4/kafka-cluster-manager-2.6.4.jar

WORKDIR /home/workspace

ENTRYPOINT ["java", "-jar", "/home/workspace/kafka-cluster-deployment-service.jar"]
