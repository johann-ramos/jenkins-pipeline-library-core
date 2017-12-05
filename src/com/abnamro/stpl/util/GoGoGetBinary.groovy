package com.abnamro.stpl.util

class GoGoGetBinary implements Serializable {
    def steps

    GoGoGetBinary(steps) {this.steps = steps}


    /**
     * GoGoGetBinary only returns the assumed binary name; you have to find the path yourself
     * @param app base-name of the expected application-binary
     */
    
    String getBinary(app) {
        String platform = _platform()
        String architecture = _architecture()
        if(platform == 'windows') {
            if(architecture == 'x86') {
                return "${app}-32.exe"
            } else {
                return "${app}.exe"
            }
        } else {
            if(architecture == 'x86') {
                return "${app}-${platform}-${architecture}"
            } else {
                return "${app}-${platform}"
            }
            
        }
        return "${app}"
    }

    // returns 'darwin', 'windows' or 'linux'
    private String _platform() {
        if(!steps.isUnix()) {
            return 'windows'
        }
        steps.sh 'uname > PLATFORM'
        def platform = steps.readFile('PLATFORM').trim()
        switch(platform) {
            case 'Darwin': 
                return 'darwin'
            case 'WindowsNT': 
                return 'windows'
            default:
                return 'linux'
        }
    }

    private String _architecture() {
        steps.sh 'uname -p > ARCHITECTURE'
        def architecture = steps.readFile('ARCHITECTURE').trim()
        switch(architecture) {
            case 'i386':
                return 'x86'
             default:
                return 'x64'
        }
    }
}