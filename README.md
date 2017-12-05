Jenkins-Pipeline-Library-Core is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Jenkins-Pipeline-Library-Core is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Jenkins-Pipeline.  If not, see <http://www.gnu.org/licenses/>.


# Standard Pipeline Core (STPL-Core)

This is the ABN AMRO Standard Pipeline with building blocks to be used for all other Standard Pipelines.

This is based on the [Shared Library](https://jenkins.io/doc/book/pipeline/shared-libraries/) concept of [Jenkins](http://jenkins.io).

For more information, read the [Docs](docs/) or [Contrubution Guide](docs/innersourcing/CONTRIBUTING.md).


## Standards & Guidelines

This pipeline attempts to implement and honor the [best practices from Cloudbees](JENKINS_PIPELINE_BEST_PRACTICES,md) regarding Jenkins Pipelines.

For more information on how Jenkins Pipeline's and Shared Libraries work, [visit this excellent site](https://joostvdg.github.io/).


## The pipeline-pipeline

___This pipeline should only be used on other pipelines___

Currently has the following stages:

- _GroovyDocs_: generate documentation
- _Git tagging_: Each build will generate a tag in Git

### Using pipelinePipeline

Create a `Jenkinsfile` which contains the same as [this example](Jenkinsfile).

### Configuration

Create `jenkins.yaml` and configure your properties. [Example](jenkins.yaml).

These are the properties to configure:

|Property|Required|Default|Description|
|---|---|---|---|
|packagePath|no||(comma-separated) java-styled package names for which to generate groovy doc|
|version|yes|||
|builderCredentialsId|yes|||
|maintainersEmailAddresses|yes||The (comma-seperated) emailaddresses of people who should be informed when the pipeline breaks|