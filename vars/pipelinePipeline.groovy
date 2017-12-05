#!groovy

import com.abnamro.stpl.GroovyDocs
import com.abnamro.stpl.util.Git
import com.abnamro.stpl.util.Utilities

def properties = null
def utilities = null
def git = null

def call() {
    pipeline {
        agent { label 'linux' }

        options {
            buildDiscarder(logRotator(numToKeepStr: '5'))
        }

        stages {
            stage('Initialize') {
                // read configuration 
                steps {
                    checkout scm

                    script {
                        properties = readYaml file: 'jenkins.yaml'
                        properties.builderCredentialsId = properties.builderCredentialsId? properties.builderCredentialsId : 'BUILDER'
                        properties.maintainersEmailAddresses = properties.maintainersEmailAddresses? properties.maintainersEmailAddresses : '-------'

                        assert properties.version : 'Please add "version" to jenkins.yaml'
                        utilities = new Utilities(this)
                        git = new Git(this)
                        git.git = steps.tool name: 'GIT', type: 'hudson.plugins.git.GitTool'
                    }
                }
            }          

            // stage('Validation') {
            //     // linting
            // }

            // stage('Unittests') {
            //     // run unit tests

            // }
            
            // generate documentation
            stage('Generate docs') {
                steps {
                    script {
                        GroovyDocs groovyDocs = new GroovyDocs(this)
                        groovyDocs.generate(properties.packagePath.replace(',', ' '))
                    }

                    publishHTML target: [
                            allowMissing         : false,
                            alwaysLinkToLastBuild: true,
                            keepAll              : true,
                            reportDir            : 'output',
                            reportFiles          : 'index.html',
                            reportName           : 'GroovyDocs'
                    ]
                }
            }
            stage('Create Tag') {
                steps {
                    script {
                        if (env.BRANCH_NAME == 'master') { // cannot use when due to https://issues.jenkins-ci.org/browse/JENKINS-43576
                            echo "branchName: ${env.BRANCH_NAME}"
                            git.createNextTag(String.valueOf(properties.version), properties.builderCredentialsId, utilities)
                        }  else {
                            echo 'We only do this for the Master Branch so skipping'
                            echo "We cannot skip this due to https://issues.jenkins-ci.org/browse/JENKINS-43576"
                        }
                    }
                }
            }
        }      
        post {
            failure {
                script {
                    if(env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'develop') {
                            mail to: properties.maintainersEmailAddresses, 
                                subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                                body: """FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':
                                \nCheck console output at ${env.BUILD_URL}
                                
                                """
                            // wait for fix of https://issues.jenkins-ci.org/browse/JENKINS-9016 before we can use this:
                            // (it will send the email to the committers)
                            //  emailext (
                            //     subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                            //     body: """<p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
                            //         <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>""",
                            //     recipientProviders: [[$class: 'DevelopersRecipientProvider']]
                            //     )
                    }
                }
            }
            changed {
                script {
                    if (currentBuild && currentBuild.previousBuild) {
                        echo "Status Changed: [From: $currentBuild.previousBuild.result, To: $currentBuild.currentResult]"
                        if (currentBuild.currentResult == 'success') {
                            mail to: properties.maintainersEmailAddresses,
                                    subject: "Back to normal: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                                    body: """Back to normal: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':
                             \nCheck console output at ${env.BUILD_URL}
                        """
                        }
                    }
                }
            }
            always {
                step([$class: 'WsCleanup'])
            }
        }
    }   
}