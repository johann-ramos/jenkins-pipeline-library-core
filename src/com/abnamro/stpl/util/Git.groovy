package com.abnamro.stpl.util

/**
 * Class for Git steps for the STPL Pipelines.
 */
class Git implements Serializable {

    def steps
    def git

    /**
     * Create a new Git instance.
     * @param steps the groovy dsl context
     */
    Git(steps) {
        this.steps = steps
    }

    /**
     * Execute a shell command, while taking care of the current platform.
     * @param command the command to execute
     */
    private void shell(String command) {
        if(steps.isUnix()) {
            steps.sh command
        } else {
            steps.bat command
        }
    }

    private String shellWithResponse(String command) {
        String  tempFileName = "shellWithResponse.txt"
        String commandWithExport = "${command} > ${tempFileName}"
        shell(commandWithExport)
        def response = steps.readFile tempFileName
        if(steps.isUnix()) {
            steps.sh "rm $tempFileName"
        } else {
            steps.bat "del $tempFileName"
        }
        return response.trim()
    }

    /**
     * retrieve git commit.
     */
    String getCommit() {
        String gitCommit
        if(steps.isUnix()) {
            steps.sh 'git rev-parse HEAD > GIT_COMMIT'
        } else {
            steps.bat 'git rev-parse HEAD > GIT_COMMIT'
        }
        gitCommit = steps.readFile('GIT_COMMIT').trim()
        return gitCommit
    }

    /**
     * retrieve git repo URL.
     */
    String getUrl() {
        String gitUrl
        if(steps.isUnix()) {
            steps.sh 'git ls-remote --get-url origin > GIT_URL'
        } else {
            steps.bat 'git ls-remote --get-url origin > GIT_URL'
        }
        gitUrl  = steps.readFile('GIT_URL').trim()
        return gitUrl
    }
    
    /**
     * Create a tag with the provided tagName.
     * @param tagName the name for the tag
     */
    void createTag(String tagName) {
        assert tagName: 'I need tagName to be valid'
        def createTagCommand = "\"${git}\" tag -a ${tagName} -m \"Jenkins created version ${tagName}\""
        shell(createTagCommand)
    }


    /**
     * Create the next Tag version. 
     * Based on the current version and the tags currently in Git, derived the new incremental semantic version.
     * Where the increment will be on the patch segment.
     *
     * @param currentVersion curent semantic Major.Minor version (e.g. 1.2)
     * @param builderCredentialsId the credentials for connecting to the Git repository
     * @param utilities the utilities class
     * @return the new tag version string (e.g. if version is 1.2 and no tags exists: 1.2.0, if 1.2.2 exists, we get: 1.2.3)
     */
    String createNextTagVersion(String currentVersion, String builderCredentialsId, Utilities utilities){
        def gitTags = retrieveGitTagsForVersion("v${currentVersion}.*")
        def tagsArray = gitTags.split('\n')
        return utilities.getNewVersion(tagsArray, currentVersion)
    }

    /**
     * Create the next tag in Git. 
     * Based on the current version and the tags currently in Git, derived the new incremental semantic version.
     * Where the increment will be on the patch segment.
     *
     * @param currentVersion curent semantic Major.Minor version (e.g. 1.2)
     * @param builderCredentialsId the credentials for connecting to the Git repository
     * @param utilities the utilities class
     * @return the new tag (e.g. if version is 1.2 and no tags exists: v1.2.0, if 1.2.2 exists, we get: v1.2.3)
     */
    String createNextTag(String currentVersion, String builderCredentialsId, Utilities utilities) {
        String newVersion = createNextTagVersion(currentVersion, builderCredentialsId, utilities)
        String newTag = "v${newVersion}"
        
        createTag(newTag)
        pushTagToRepo(newTag, builderCredentialsId)
        return newTag
    }

    /**
     * Push the given tag to the current remote used. <br>
     * <ul>
     *     <li>Checkout: https://p-bitbucket.nl.eu.abnamro.com:7999/scm/~c29874/pipeline-from-scm-tests.git</li>
     *     <li>Get credentials from Jenkins</li>
     *     <li>Push to https://{user}:{pass}@p-bitbucket.nl.eu.abnamro.com:7999/scm/~c29874/pipeline-from-scm-tests.git </li>
     *  </ul>
     *
     * @param tagName
     * @param credentialsId
     */
    void pushTagToRepo(String tagName, String credentialsId) {
        assert tagName: 'I need tagName to be valid'
        assert credentialsId: 'I need credentialsId to be valid'

        /*
         * example:
         * from: https://p-bitbucket.nl.eu.abnamro.com:7999/scm/~c29874/pipeline-from-scm-tests.git
         * to: https://{user}:{pass}@p-bitbucket.nl.eu.abnamro.com:7999/scm/~c29874/pipeline-from-scm-tests.git
         *
        */

        String originRepo = getGitOriginRemote()
        steps.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: credentialsId, passwordVariable: 'pss', usernameVariable: 'usr']]) {
            String repo = originRepo.replace('https://', " https://${steps.env.usr}:${steps.env.pss}@")
            def gitAddRemoteCommand = "\"${git}\" remote add bbTags ${repo}"
            def gitPushCommand = "\"${git}\" push bbTags ${tagName}"

            shell(gitAddRemoteCommand)
            shell(gitPushCommand)
        }
    }

    String getGitOriginRemote() {
        def gitCommand = "\"${git}\" config --get remote.origin.url"
        return shellWithResponse(gitCommand)
    }

    /**
     * Making sure we have a clean checkout in our workspace which is absolutely identical to the branch.
     * @param branchName the branchName
     */
    void cleanAndResetToBranch(String branchName) {
        assert branchName: 'I need branchName to be valid'

        // Clean any locally modified files and ensure we are actually on origin/$env.BRANCH_NAME
        // as a failed release could leave the local workspace ahead of origin/master
        def gitCommand = "\"${git}\" clean -f && \"${git}\" reset --hard origin/${branchName}"
        shell(gitCommand)
    }

    /**
     * Convenience method to use local git client to return the branch name.
     * @return the git branch name
     */
    String gitBranchName() {
        def gitCommand = "\"${git}\" rev-parse --abbrev-ref HEAD"
        return shellWithResponse(gitCommand)
    }

    /**
     * Retrieve a (array)list of git tags for a version in the current git checkout.
     *
     * @param versionMask the version to filter the tags for, e.g. v1.4.*
     * @return the output from the git command (git tag -l ...), which results in a multi-line response with the tags
     */
    String retrieveGitTagsForVersion(String versionMask) {
        String command = "git tag -l \"${versionMask}\""
        return shellWithResponse(command)
    }

    /**
     * Calls pushChangesToOrigin with default branchTarget of master.
     * @param credentialsId the credentialsId for the username/password for updating the git repo
     */
    void pushChangesToOrigin(String credentialsId) {
        pushChangesToOrigin(credentialsId, 'master')
    }

    /**
     * Will push the current git checkout (HEAD) to the current git checkout URL, using the branchTarget as remote branch.
     * @param credentialsId the credentialsId for the username/password for updating the git repo
     * @param branchTarget the target branch in the repo to update
     */
    void pushChangesToOrigin(String credentialsId, String branchTarget) {
        String gitRepo = getGitOriginRemote()
        steps.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: credentialsId, passwordVariable: 'pss', usernameVariable: 'usr']]) {
            gitRepo = gitRepo.replace('https://', "https://${steps.env.usr}:${steps.env.pss}@")
            String gitAddRemoteCommand = "\"${git}\" remote add bbCommit ${gitRepo}"
            String gitUserCommand = "\"${git}\" config user.name ${steps.env.usr}"
            String gitPushCommand = "\"${git}\" push bbCommit HEAD:${branchTarget}"

            shell(gitAddRemoteCommand)
            shell(gitUserCommand)
            shell(gitPushCommand)
        }
    }

    /**
     * Change your current git checkout to the git tag of tagName.
     * @param tagName the name of the tag to change to
     */
    void changeToTag(String tagName) {
        String gitCheckoutTagCommand = "\"${git}\" checkout tags/${tagName}"
        shell(gitCheckoutTagCommand)
    }

    /**
     * Will delete the tag from the remote git repository.
     *
     * @param tagname tag name to delete
     * @param credentialsId the credentialsId for the username/password for updating the git repo
     */
    void deleteTag(String tagname, String credentialsId){
        String gitRepo = getGitOriginRemote()
        steps.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: credentialsId, passwordVariable: 'pss', usernameVariable: 'usr']]) {
            def remoteName = 'bbRemote'
            gitRepo = gitRepo.replace('https://', "https://${steps.env.usr}:${steps.env.pss}@")
            String gitAddRemoteCommand = "\"${git}\" remote add ${remoteName} ${gitRepo}"
            String gitUserCommand = "\"${git}\" config user.name ${steps.env.usr}"
            String removeTagCommand = "git push --delete ${remoteName} $tagname" 
            shell(gitAddRemoteCommand)
            shell(gitUserCommand)
            shell(removeTagCommand)
        }
    }
}
