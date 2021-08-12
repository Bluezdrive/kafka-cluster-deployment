FROM ubuntu:21.10
ARG JAR_FILE

# Install OpenJDK 15 & Git
RUN apt-get update -y && \
    apt-get install -y gpgv2 openjdk-15-jre git wget && \
    apt-get clean

# Create .ssh directory
RUN mkdir -p /root/.ssh

# Add github.com to known hosts
RUN ssh-keyscan github.com >> ~/.ssh/known_hosts

