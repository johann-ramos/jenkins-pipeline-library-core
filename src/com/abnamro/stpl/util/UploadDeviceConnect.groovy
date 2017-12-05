package com.abnamro.stpl.util

class UploadDeviceConnect implements Serializable {

    def steps

    UploadDeviceConnect(steps) {
        this.steps = steps
    }

    def doStage(String filenames, String backend) {


        steps.print "Uploading ${filenames} to ${backend}"

        steps.withCredentials([[$class: 'StringBinding', credentialsId: 'DeviceConnectUsername', variable: 'deviceConnectUsername']]) {
            steps.withCredentials([[$class: 'StringBinding', credentialsId: 'DeviceConnectApiToken', variable: 'deviceConnectApiToken']]) {
                // do one by one because when the server will give errors when you upload too much at once
                def filenamesSplit = filenames.split(',')
                for(int i = 0; i< filenamesSplit.size(); i++) {
                    String filename = filenamesSplit[i].toString().trim()
                    def status = steps.sh returnStatus: true, script: "curl -XPOST -u '${steps.env.deviceConnectUsername}':'${steps.env.deviceConnectApiToken}' 'http://${backend}/apiv1/Application?verb=add' --form \"upload=@${filename}\""
                    if(status != 0) {
                        steps.error 'error uploading to deviceConnect'
                    }
                    // add a delay because deviceConnect does not like us hammering their Rest API with big files
                    steps.sleep 5
                }
            }
        }
    }
}