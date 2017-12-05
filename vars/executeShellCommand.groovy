def call(String command) {
    String response
    if (isUnix()) {
        response = sh returnStdout: true, script: command
    } else {
        response = bat returnStdout: true, script: command
    }
    return response
}