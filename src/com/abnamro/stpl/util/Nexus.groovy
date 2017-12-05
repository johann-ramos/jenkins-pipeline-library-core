package com.abnamro.stpl.util

import com.abnamro.stpl.util.model.NexusArtifact

class Nexus implements Serializable {
    private final def steps
    private final Maven maven

    /**
     * Creates a new Nexus utility class
     *
     * @param steps the pipeline dsl context
     * @param maven an instance of the com.abnamro.stpl.util.Maven-class; please make sure you configure it correctly
     */
    @Deprecated
    Nexus(final def steps, 
            final Maven maven) {
        this.steps = steps
        this.maven = maven
    }

    /**
     * Creates a new Nexus utility class
     *
     * @param steps the pipeline dsl context
     */
    Nexus(final def steps) {
        this.steps = steps
    }

     
     /**
     * Deploys a list of files to the Nexus repository of your choice
     *
     * @param nexusUrl the url of your nexus instance
     * @param repositoryId the three- or four-letter code of your repository
     * @param groupId the full classname of your artifact (e.g. com.abnamro.mydepartment)
     * @param artificatId the name of your application
     * @param version the version of your application
     * @param files list of files your want to upload
     * @param types must contain as many items as 'files'; for each uploaded file you have to indicate a type (usually the extension: .exe, .zip, etcetera)
     * @param classifiers must contain as many items as 'files'; for each uploaded you must indicate a classifier (e.g. DEBUG-version, sources, specific platform, etc.)
     */
    @Deprecated
    void deploy(final String nexusUrl, final String repositoryId, final String groupId, final String artifactId, 
            final String version,

            final ArrayList<String> files, final ArrayList<String> types, final ArrayList<String> classifiers) {

        assert nexusUrl : 'nexusUrl needs to be valid'
        assert repositoryId : 'repositoryId needs to be valid'
        assert groupId : 'groupId needs to be valid'
        assert artifactId : 'artifactId needs to be valid'
        assert version : 'version needs to be valid'
        assert files : 'filesParam needs to be valid'                

        String url="${nexusUrl}/content/repositories/${repositoryId}/"

        String filesParam = files[(1..-1)].join(',')
        String typesParam = types[(1..-1)].join(',')
        String classifiersParam = classifiers[(1..-1)].join(',')

        String mavenDeployCommand = 
            """deploy:deploy-file -Durl=$url \\
            -DgroupId=$groupId \\
            -DartifactId=$artifactId \\
            -Dversion=$version \\
            -Dfile=${files[0]} \\
            -Dpackaging=${types[0]} \\
            -Dclassifier=${classifiers[0]} \\
            -DrepositoryId=$repositoryId \\
            -Dfiles=$filesParam \\
            -Dtypes=$typesParam \\
            -Dclassifiers=$classifiersParam"""

        echo '==========================================================='
        echo '==========================================================='
        echo '== W A R N I N G ::: Nexus.deploy is deprecated. Please use Nexus.uploadArtifact or Nexus.uploadArtifacts'
        echo '==========================================================='
        echo '==========================================================='
        maven.mvnWithSettings(mavenDeployCommand)
    }

     /**
     * Deploys a single file to the Nexus repository of your choice
     *
     * @param nexusUrl the url of your nexus instance
     * @param repositoryId the three- or four-letter code of your repository
     * @param groupId the full classname of your artifact (e.g. com.abnamro.mydepartment)
     * @param artificatId the name of your application
     * @param version the version of your application
     * @param file file you want to upload
     */
    @Deprecated
    void deploy(final String nexusUrl, final String repositoryId, final String groupId, final String artifactId, 
            final String version,

            final String file) {
        echo '==========================================================='
        echo '==========================================================='
        echo '== W A R N I N G ::: Nexus.deploy is deprecated. Please use Nexus.uploadArtifact or Nexus.uploadArtifacts'
        echo '==========================================================='
        echo '==========================================================='
        deploy(nexusUrl, repositoryId, groupId, artifactId, version, file, null, null)
    }

     /**
     * Deploys a single file to the Nexus repository of your choice
     *
     * @param nexusUrl the url of your nexus instance
     * @param repositoryId the three- or four-letter code of your repository
     * @param groupId the full classname of your artifact (e.g. com.abnamro.mydepartment)
     * @param artificatId the name of your application
     * @param version the version of your application
     * @param file file you want to upload
     * @param packaging you have to indicate a file-type (usually the extension: .exe, .zip, etcetera)
     * @param classifier you must indicate a classifier (e.g. DEBUG-version, sources, specific platform, etc.)
     */
    @Deprecated
    void deploy(final String nexusUrl, final String repositoryId, final String groupId, final String artifactId, 
            final String version,
            final String file, final String packaging, final String classifier) {
        assert nexusUrl : 'nexusUrl needs to be valid'
        assert repositoryId : 'repositoryId needs to be valid'
        assert groupId : 'groupId needs to be valid'
        assert artifactId : 'artifactId needs to be valid'
        assert version : 'version needs to be valid'

        String url="${nexusUrl}/content/repositories/${repositoryId}/"

        String mavenDeployCommand = 
            """deploy:deploy-file -Durl=$url \\
            -DrepositoryId=$repositoryId \\
            -DgroupId=$groupId \\
            -DartifactId=$artifactId \\
            -Dversion=$version \\
            -Dfile=$file"""

        if(packaging) {
            mavenDeployCommand += " -Dpackaging=$packaging"
        }
        if(classifier) {
            mavenDeployCommand += " -Dclassifier=$classifier"
        }

        echo '==========================================================='
        echo '==========================================================='
        echo '== W A R N I N G ::: Nexus.deploy is deprecated. Please use Nexus.uploadArtifact or Nexus.uploadArtifacts'
        echo '==========================================================='
        echo '==========================================================='
        maven.mvnWithSettings(mavenDeployCommand)
    }

    /**
     *
     * @param nexusUrl the url of your nexus instance (If Nexus Url is http://mynexusrepo:8081/nexus the value for this field is mynexusrepo:8081/nexus)
     * @param repositoryId repositoryId the three- or four-letter code of your repository + the type (-releases, -shared), e.g. SOLO-releases
     * @param groupId the fully qualified namespace of your artifact, in general this is a reverse dns name (e.g. com.abnamro.solo)
     * @param version the version of your artifact
     * @param artifacts the main artifact to upload
     * @param credentialsId the credentialsId for uploading to Nexus
     * @param nexusVersion the nexus version to upload to (default is nexus2) [nexus2, nexus3]
     * @param nexusProtocol the protocol of the nexusUrl (default is https [https, http]
     */
    void uploadArtifact(final String nexusUrl,
                        final String repositoryId,
                        final String groupId,
                        final String version,
                        NexusArtifact artifact,
                        final String credentialsId,
                        final String nexusVersion = 'nexus2',
                        final String nexusProtocol = 'https'){

        NexusArtifact[] artifacts = [artifact]
        uploadArtifacts(nexusUrl, repositoryId, groupId, version, artifacts, credentialsId, nexusVersion, nexusProtocol)
    }

    /**
     *
     * @param nexusUrl the url of your nexus instance (If Nexus Url is http://mynexusrepo:8081/nexus the value for this field is mynexusrepo:8081/nexus)
     * @param repositoryId repositoryId the three- or four-letter code of your repository + the type (-releases, -shared), e.g. SOLO-releases
     * @param groupId the fully qualified namespace of your artifact, in general this is a reverse dns name (e.g. com.abnamro.solo)
     * @param version the version of your artifact
     * @param artifacts the array of artifacts to upload, Main artifacts must be first
     * @param credentialsId the credentialsId for uploading to Nexus
     * @param nexusVersion the nexus version to upload to (default is nexus2) [nexus2, nexus3]
     * @param nexusProtocol the protocol of the nexusUrl (default is https [https, http]
     */
    void uploadArtifacts(final String nexusUrl,
                        final String repositoryId,
                        final String groupId,
                        final String version,
                        NexusArtifact[] artifacts,
                        final String credentialsId,
                        final String nexusVersion = 'nexus2',
                        final String nexusProtocol = 'https') {

        assert nexusUrl: 'Param nexusUrl missing!'
        assert nexusVersion: 'Param nexusVersion missing!'
        assert nexusProtocol: 'Param nexusProtocol missing!'
        assert repositoryId: 'Param repositoryId missing!'
        assert credentialsId: 'Param credentialsId missing!'
        assert artifacts: 'Param artifacts missing!'

        def nexusArtifacts = [
                [
                        artifactId: "${artifacts[0].artifactId()}",
                        classifier: "${artifacts[0].classifier()}",
                        file: "${artifacts[0].file()}",
                        type: "${artifacts[0].type()}"
                ]
        ]

        for (int i =1; i < artifacts.length; i++) {
            nexusArtifacts.add([
                    artifactId: "${artifacts[i].artifactId}",
                    classifier: "${artifacts[i].classifier}",
                    file: "${artifacts[i].file}",
                    type: "${artifacts[i].type}"
            ])
        }

        steps.nexusArtifactUploader artifacts: nexusArtifacts,
                credentialsId: credentialsId,
                groupId: groupId,
                nexusUrl: nexusUrl,
                nexusVersion: nexusVersion,
                protocol: nexusProtocol,
                repository: repositoryId,
                version: version

    }

}
