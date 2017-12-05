package com.abnamro.stpl.util.model

/**
 * Simple representation of the Nexus Artifact Upload Artifact class.
 */
class NexusArtifact implements Serializable {
    String artifactId
    String classifier
    String file
    String type

    def artifactId(){
        return artifactId
    }

    def classifier(){
        return classifier
    }

    def file(){
        return file
    }

    def type() {
        return type
    }
}