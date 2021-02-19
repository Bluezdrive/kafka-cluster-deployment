# Kafka Cluster Deployment

The Kafka Cluster Deployment Service supports you in executing the Kafka Cluster Manager triggered by a GitHub push event or by polling 
the repository on a regular basis.

# Configure Service

## Directory structure

```TEXT
/home
 +---/config
 |    +---application.yaml
 +---/workspace
 |    +---/repository
 |    +---kafka-cluster-manager.jar
 +---kafka-cluster-deployment-service.jar
 ```

## Configuration File application.yaml
The configuration file application.yaml must be stored in a subdirectory config 

```YAML
config:
  working-directory: /home/workspace/repository
  git:
    repository: Bluezdrive/kafka-cluster-topology
    branch: master
    secret: <GITHUB_WEBHOOK_SECRET>
    private-key: <GITHUB_SSH_PRIVATE_KEY>
    cron: 0 0/1 * * * *
  tasks:
    init-workspace:
      order: 1
      name: "Init Workspace ${config.working-directory}"
      type: COMMAND_LINE
      command: find ${config.working-directory} -mindepth 1 -delete
    init-repository:
      order: 2
      name: "Init Git Repository: ${config.git.repository}"
      type: GIT
      command: cloneOrPull
    deploy-to-cluster-sandbox:
      order: 3
      name: "Deploy to Apache Kafka® Cluster 'Sandbox'"
      type: COMMAND_LINE
      command: java -jar /home/workspace/kafka-cluster-manager.jar --cluster=sandbox
      environment:
        BOOTSTRAP_SERVER: <CLUSTER_BOOSTRAP_SERVER>
        CLUSTER_API_KEY: <CLUSTER_API_KEY>
        CLUSTER_API_SECRET: <CLUSTER_API_SECRET>
        SCHEMA_REGISTRY_URL: <SCHEMA_REGISTRY_URL>
        SCHEMA_REGISTRY_API_KEY: <SCHEMA_REGISTRY_API_KEY>
        SCHEMA_REGISTRY_API_SECRET: <SCHEMA_REGISTRY_API_SECRET>
    deploy-to-cluster-development:
      order: 4
      name: "Deploy to Apache Kafka® Cluster 'Development'"
      type: COMMAND_LINE
      command: java -jar /home/workspace/kafka-cluster-manager.jar --cluster=development
      environment:
        BOOTSTRAP_SERVER: <CLUSTER_BOOSTRAP_SERVER>
        CLUSTER_API_KEY: <CLUSTER_API_KEY>
        CLUSTER_API_SECRET: <CLUSTER_API_SECRET>
        SCHEMA_REGISTRY_URL: <SCHEMA_REGISTRY_URL>
        SCHEMA_REGISTRY_API_KEY: <SCHEMA_REGISTRY_API_KEY>
        SCHEMA_REGISTRY_API_SECRET: <SCHEMA_REGISTRY_API_SECRET>
    update-repository:
      order: 5
      name: "Update Git Repository: ${config.git.repository}"
      type: GIT
      command: commitAndPush
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            clientId: <GITHUB_CLIENT_ID>
            clientSecret: <GITHUB_CLIENT_SECRET>
          facebook:
            clientId: <FACEBOOK_CLIENT_ID>
            clientSecret: <FACEBOOK_CLIENT_SECRET>
          google:
            clientId: <GOOGLE_CLIENT_ID>
            clientSecret: <GOOGLE_CLIENT_SECRET>
  datasource:
    url: "jdbc:postgresql://<HOST>:<PORT>/<DATABASE_NAME>"
    username: <USER_NAME>
    password: <PASSWORD>
```

## Encrypt Values in Configuration File

If for some reason it becomes necessary to store the configuration file in a repository, then at least
credentials should be encrypted. The encryption is done with Jasypt. Individual values can be encrypted
by using the following command.

```shell
mvn jasypt:encrypt-value -Djasypt.encryptor.password="<PASSWORD_TO_DECRYPT_VALUES>" -Djasypt.plugin.value="<VALUE>"
```

## Profiles

The service supports different spring profiles. This allows different configuration files to be used for
multiple environments. By default the 'Default' profile is used. This profile expects the configuration 
file application.yaml in the subdirectory 'config'.
In case you want to use profiles, add the profile name to the configuration file name and specify an environment
variable "spring_profiles_active=<PROFILE>".

```shell
application-<PROFILE>.yaml
```

## Configure GitHub Repository

### Create GitHub Webhook

In order for a build to be triggered by GitHub, a webhook must first be set up for the corresponding repository.

1. Navigate to the menu item "Webhooks" in your GitHub repository and click on the button "Add webhook".
2. Now enter the URL of the web service in the "Payload URL" field: "https://HOST:PORT/api/jobs"
3. Select the value "application/json" as "Content type"
4. For security reasons, the "Secret" must be set. The secret selected there must also be specified in the 
   application.yaml under the property "config.git.secret".

```YAML
config:
  git:
    secret: <GITHUB_WEBHOOK_SECRET>
```
   
### GitHub Poll

If the environment does not allow a build to be actively triggered by GitHub, it is possible to poll the repository 
on a regular basis. For this purpose, the corresponding cronjob must be configured:

```YAML
config:
  git:
    cron: 0 0/1 * * * *
```

# Docker

Here's an example of how to run the service as a docker image.

## Command with default profile

```shell
docker run \
  -e password=<PASSWORD_TO_DECRYPT_VALUES> \
  -v /home/config:/home/workspace/config \
  -p 8080:8080 \
  bluezdrive/kafka-cluster-deployment-service:1.0
```

## Command with profile

```shell
docker run \
  -e spring_profiles_active=<PROFILE> \
  -e password=<PASSWORD_TO_DECRYPT_VALUES> \
  -v /home/config:/home/workspace/config \
  -p 8080:8080 \
  bluezdrive/kafka-cluster-deployment-service:1.0
```
