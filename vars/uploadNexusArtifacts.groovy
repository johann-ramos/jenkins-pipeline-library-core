import com.abnamro.stpl.util.Nexus
import com.abnamro.stpl.util.model.NexusArtifact

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
def call(final String nexusUrl,
      final String repositoryId,
      final String groupId,
      final String version,
      NexusArtifact[] artifacts,
      final String credentialsId,
      final String nexusVersion = 'nexus2',
      final String nexusProtocol = 'https') {

    Nexus nexus = new Nexus(this)
    nexus.uploadArtifacts(nexusUrl, repositoryId, groupId, version,artifacts, credentialsId, nexusVersion, nexusProtocol)
}