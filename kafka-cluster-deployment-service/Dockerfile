FROM ubuntu:21.04
ARG JAR_FILE

# Install OpenJDK 15 & Git
RUN apt-get update && \
    apt-get install -y openjdk-15-jre git wget && \
    apt-get clean

# Create directories
RUN mkdir -p /home/workspace
RUN mkdir -p /home/workspace/config
RUN mkdir -p /home/workspace/repository
COPY target/${JAR_FILE} /home/workspace/kafka-cluster-deployment-service.jar
RUN wget -O /home/workspace/kafka-cluster-manager.jar https://github.com/Bluezdrive/kafka-cluster-manager/releases/download/v2.2/kafka-cluster-manager-2.2.jar

# Create .ssh directory
RUN mkdir -p /root/.ssh

# Add github.com to known hosts
RUN ssh-keyscan github.com >> ~/.ssh/known_hosts

# Add ssh keyfiles for access to github.com
#COPY docker/id_rsa /root/.ssh/id_rsa
#COPY docker/id_rsa.pub /root/.ssh/id_rsa.pub
#RUN chmod 600 /root/.ssh/id_rsa

# Add configuration file
#COPY docker/application.yml /home/workspace/config/application.yml

WORKDIR /home/workspace/repository

ENTRYPOINT ["java", "-Dspring.config.location=file:/home/workspace/config/application.yml", "-jar", "/home/workspace/kafka-cluster-deployment-service.jar"]
