package com.abnamro.stpl.summary

/**
 * Class to keep track of ImpactfulActions in the pipeline.
 */
class ImpactfulAction implements Serializable {
    private String name
    private String message

    ImpactfulAction(String name, String message) {
        this.name = name
        this.message = message
    }

    @Override
    String toString() {
        return "<li>" +
                "<b>" + name + '</b>: ' +
                message + '</li>'
    }

}
