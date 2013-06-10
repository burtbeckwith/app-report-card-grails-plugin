package com.erasmos.grails.app_report_card_plugin

/**
 * A simple wrapper; I figured it would make for better
 * self-documentation.
 */
class AppId  {

    private Long id

    AppId(final Long id){
        this.id = id
    }

    /**
     * TODO: Test
     * @param o
     * @return
     */
    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof AppId)) return false

        AppId appId = o

        if (id != appId.id) return false

        return true
    }

    int hashCode() {
        return id?.hashCode()
    }

    String toString(){
        return id.toString()
    }
}
