FROM ubuntu:21.10
ARG JAR_FILE

# Install OpenJDK 15 & Git
RUN apt-get update -y && \
    apt-get install -y gpgv2 openjdk-15-jre git wget && \
    apt-get clean

# Create directories
RUN mkdir -p /home/workspace
RUN mkdir -p /home/workspace/config
RUN mkdir -p /home/workspace/repository
COPY target/${JAR_FILE} /home/workspace/kafka-cluster-deployment-service.jar
RUN wget -O /home/workspace/kafka-cluster-manager.jar https://github.com/Bluezdrive/kafka-cluster-manager/releases/download/v2.6.3/kafka-cluster-manager-2.6.3.jar

# Create .ssh directory
RUN mkdir -p /root/.ssh

# Add github.com to known hosts
RUN ssh-keyscan github.com >> ~/.ssh/known_hosts

WORKDIR /home/workspace

ENTRYPOINT ["java", "-jar", "/home/workspace/kafka-cluster-deployment-service.jar"]
