package com.abnamro.stpl

/**
 * Class to generate groovy docs
 */
class GroovyDocs implements Serializable {
    def steps

    /**
     * 
     * constructor
     * 
     * @param steps the pipeline dsl context
     */
    GroovyDocs(def steps) {
        this.steps = steps
    }

    /** 
     * Use this method to generate your documentation
     *
     * @param packagePath space separated java package names for which to generate groovy doc
     */
    void generate(String packagePath) {
        def linuxPrefix = ''
        if (steps.isUnix()) {
            linuxPrefix = 'Linux '
        }

        String groovyHome = steps.tool name: "${linuxPrefix}Groovy 2.4.5", type: 'hudson.plugins.groovy.GroovyInstallation'
        String javaHome = steps.tool name: "${linuxPrefix}SUN JDK 1.8", type: 'jdk'

        String command = "${groovyHome}/bin/groovydoc -sourcepath src -verbose -d output ${packagePath}"

        steps.withEnv(["GROOVY_HOME=${groovyHome}", "JAVA_HOME=${javaHome}"]) {
            if (steps.isUnix()) {
                steps.sh command
            } else {
                steps.bat command
            }
        }
    }
}


