# Jenkins Best Practices

In the last few months the Software Logistics team has had visits from several Cloudbees heavyweights.
For those that do not know, [Cloudbees](https://www.cloudbees.com) is the company behind [Jenkins](http://jenkins.io). 

We have received visits from [Damien Duportal](http://www.devopsconnect.com/speaker/damien-duportal/), [Viktor Farcic](https://technologyconversations.com/about/) and the most notable, the inventor of Hudson himself: [Kohsuke Kawaguchi](http://kohsuke.org/about/).  
We've had Q&A's, code reviews, platform reviews and freeform discussions with them.
What will follow is recap of their input and our discoveries.


## Pipeline improvements

Summary of pipeline improvements to be made at ABN AMRO.
Aside from the feedback targeted at specific pipelines, there was a lot of generic feedback wich applies to all.


### Generic Pipelines

The current models created within ABN AMRO to share Jenkins Pipeline DSL code across applications and branches is a good starting point.
For further improvements, we should utilize the Global Shared Libraries.


### Fundamentals

* Except for the steps themselves, all of the Pipeline logic, the Groovy conditionals, loops, etc execute on the *master*. Whether simple or complex! Even inside a node block!
* Steps may use executors to do work where appropriate, but each step has a small on-master overhead too.
* Pipeline code is written as Groovy but the execution model is radically transformed at compile-time to Continuation Passing Style (CPS).
* This transformation provides valuable safety and durability guarantees for Pipelines, but it comes with trade-offs:
    * Steps can invoke Java and execute fast and efficiently, but Groovy is much slower to run than normal.
    * Groovy logic requires far more memory, because an object-based syntax/block tree is kept in memory.
* Pipelines persist the program and its state frequently to be able to survive failure of the master.


### Ideal generic pipeline

The ideal generic pipeline simply defines the stages and runs standard commands.
The tool used for building/packaging should have the information to execute what is required.

For example, you can utilize docker to run everything you need to.
Have a docker compose file for each stage and execute them in order (seen below).
It is up to the developers to make sure each stage is doing the correct thing.

```groovy
node {
    stage('checkout') {
        checkout scm
    }

    stage('unit') {
        sh 'execute unit test build'
    }

    stage('staging') {
        sh 'provision staging environment'
        sh 'run staging tests'
        sh 'spin down staging environment'
    }

    stage('publish') {
      sh 'publish artifact to repository'
    }

    stage('production'){
      sh 'deploy application to production'
    }
}
```

### Do's & Don'ts

| DO                                                                                            |  DONT |  
| --------------------------------------------------------------------------------------------- | ----- |
| Create shared libraries for reuse                                                             | Build everything yourself|
| Separate the pipeline Flow from the implementations                                           | Put everything into a single script|
| Use external scripts/tools for complex or CPU-expensive processing (utlize build tools!)      | Use Jenkins to automate: you can run the entire pipeline your machine before commit|
| Use Jenkins only for orchestration                                                            | Use the Pipeline for XML or JSON parsing using Groovy’s XmlSlurper and JsonSlurper! Strongly prefer command-line tools or scripts.|
| Make Pipelines platform agnostic                                                              | Force Linux or Windows commands unless absolutely necessary|
| Simplify the flow by all methods having parameters                                            | Rely on the env variable|
| Migrate to Git (BitBucket)                                                                    | Stay on legacy SCM's (Subversion) |
| Follow programming Best Practices (KISS, DRY, [SOLID](https://dzone.com/articles/the-solid-principles-in-real-life))| Think pipelines are exempt from normal programming rules, they're not|
| Use stages for steps that can fail due to mistakes in code                                    | Use stages for each pipeline command/step you execute|
| Build mainline code (Trunk or Master)                                                         | Build branches |
| [Use push mechanism's for detecting changes](https://social.connect.abnamro.com/wikis/home?lang=en-us#!/wiki/Wee2d246ac590_421b_b359_d00ab2e7a415/page/BitBucket%20%26%20Jenkins)                                                    | Use polling for detecting changes|
| Build as much as possible within one Node block (to avoid slave allocation latency)           | Use stash/unstash for large files or entire directories|
| Cache reusable build resources (e.g. npm's node_modules on disk of the slaves)                | Always rebuild everything from scratch|
| Redo all quality check for every commit                                                       | Run a pipeline when there is no change or no output to be used | 

For more information, read [Cloudbees' blog](https://jenkins.io/blog/2017/02/01/pipeline-scalability-best-practice/) or watch [need-speed-building-Pipelines-be-faster](https://www.cloudbees.com/need-speed-building-Pipelines-be-faster).

### Example: separate flow from implementation

#### Jenkinsfile

```java
node() {
  test()
  build(maven, jdk,..)
  deploy("integration")
}
```

#### Shared Library file

```java
build(maven, jdk,..) {
    // get maven
    // get jdk 
    sh 'mvn clean install'
}
```


## Visit of Kohsuke Kawaguchi

* Q: Shared Slaves and Pipelines aren't a nice mix, will these be improved or will there be another solution?
* A:
    * For Windows will remain THE solution for the time being
    * For Linux its moving to the temporary slaves (see Cloud and Container vision below)
    * In short term, work it out with Support.

* Q: I applaud Blue Ocean's new UI, but will it also take care of more application lifecycle meta data?
    * Related: will Jenkins ever move into the CD meta information space (like XL Release) or stay a "build engine" "task scheduler"?
* A:
    * "Badges" of checks passed, should be accumulated in Jenkins
    * Permanent report should be outside Jenkins
    * Maybe augmenting the Fingerprint with the badges

* Q: Future of Jenkins and building/hosting in the cloud?
* A: 
    * [Cloudbees' Private SaaS Edition](https://www.cloudbees.com/products/cloudbees-jenkins-platform/private-saas-edition)
    * BuildSlaves temporarily "created" via autoscaling VM's
    * Slaves connecting to the master (via [Swarm Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Swarm+Plugin))

* Q: Containers, cloud, infra as code, what do you see Jenkins will be in this space?
* A: 
    * See Cloud space answer above
    * See book from Viktor Farcic [DevOps Toolkit 2.1](ttps://leanpub.com/the-devops-2-1-toolkit)

* Q: Public market place for "Global Shared Libraries" like Fabric8's github repo and such?
* A: Efforts are undertaken from Cloudbees to have authoritative examples

* Q: DSL vs Declerative
* A: both are of same importance for now

* Q: support for IDE support
* A:
    * like the .gdsl file for intelli J
    * more probably to come, not a priority
    * See [IDE Support](https://st-g.de/2016/08/jenkins-pipeline-autocompletion-in-intellij)

* Q: DB vs Filesystem
* A: 
    * There are some PoCs being done to see how Jenkins can run via a Database instead of Filesystem
    * Finishing Jenkins 2.0 and Pipeline is priority

* Q: How to create temper proof audit?
* A: Export data from Jenkins to read-only store, will not be provided by Jenkins

* Q: How do we keep track & control over whole product ranges with inter-dependencies, is there any product in this space?
* A:
    * Model them in separated pipelines.
    * Model systems outside of Jenkins, like docker-compose or terraform.
    * Cloudbees is looking into visualizing the tracking of the changes.
    * Maybe we're missing a product level (zoom out of pipelines, to group them)