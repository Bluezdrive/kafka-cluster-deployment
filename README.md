# Kafka Cluster Deployment

The Kafka Cluster Deployment Service supports you in executing the Kafka Cluster Manager triggered by a GitHub push event.

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
    secret: <GITHUB_WEBOOK_SECRET>
  tasks:
    clone-repository:
      order: 1
      type: COMMAND_LINE
      command: git clone git@github.com:${config.git.repository}.git .
    checkout-master-branch:
      order: 2
      type: COMMAND_LINE
      command: git checkout ${config.git.branch}
    deploy-to-cluster-sandbox:
      order: 3
      type: COMMAND_LINE
      command: java -jar /home/workspace/kafka-cluster-manager.jar --dry-run --cluster=sandbox
      environment:
        BOOTSTRAP_SERVER: <BOOTSTRAP_SERVER_OF_CLUSTER_SANDBOX>
        CLUSTER_API_KEY: <CLUSTER_API_KEY_OF_CLUSTER_SANDBOX>
        CLUSTER_API_SECRET: <CLUSTER_API_SECRET_OF_CLUSTER_SANDBOX>
        SCHEMA_REGISTRY_URL: <SCHEMA_REGISTRY_URL_OF_CLUSTER_SANDBOX>
        SCHEMA_REGISTRY_API_KEY: <SCHEMA_REGISTRY_API_KEY_OF_CLUSTER_SANDBOX>
        SCHEMA_REGISTRY_API_SECRET: <SCHEMA_REGISTRY_API_SECRET_OF_CLUSTER_SANDBOX>
    deploy-to-cluster-development:
      order: 4
      type: COMMAND_LINE
      command: java -jar /home/workspace/kafka-cluster-manager.jar --dry-run --cluster=development
      environment:
        BOOTSTRAP_SERVER: <BOOTSTRAP_SERVER_OF_CLUSTER_DEVELOPMENT>
        CLUSTER_API_KEY: <CLUSTER_API_KEY_OF_CLUSTER_DEVELOPMENT>
        CLUSTER_API_SECRET: <CLUSTER_API_SECRET_OF_CLUSTER_DEVELOPMENT>
        SCHEMA_REGISTRY_URL: <SCHEMA_REGISTRY_URL_OF_CLUSTER_DEVELOPMENT>
        SCHEMA_REGISTRY_API_KEY: <SCHEMA_REGISTRY_API_KEY_OF_CLUSTER_DEVELOPMENT>
        SCHEMA_REGISTRY_API_SECRET: <SCHEMA_REGISTRY_API_SECRET_OF_CLUSTER_DEVELOPMENT>
    clean-up-workspace:
      order: 5
      type: COMMAND_LINE
      command: find . -mindepth 1 -delete
```

## Configure GitHub Repository

### Create GitHub Webhook

In order for a build to be triggered by GutHub, a webhook must first be set up for the corresponding repository.

1. Navigate to the menu item "Webhooks" in your GitHub repository and click on the button "Add webhook".
2. Now enter the URL of the web service in the "Payload URL" field.
3. Select the value "application/json" as "Content type"
4. For security reasons, the "Secret" must be set. The secret selected there must also be specified in the application.yaml under the property "config.git.secret".

# Development