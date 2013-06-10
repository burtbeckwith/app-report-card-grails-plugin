package com.erasmos.grails.app_report_card_plugin

/**
 * There's no such thing as an App that exists on its own - it's
 * always associated with a Store; when you publish an App, you
 * have a choice as to which Stores can offer it. As well, even
 * the appName can change according to the Store's language (TODO: Confirm)
 *
 * However, what is constant about the app is it's AppId
 *
 */
class StoreApp {

    static int MaxCharsForDisplayName = 60

    Store store

    AppId appId
    String appName
    String appSmallIconUrl
    String appCurrentVersion


    String developerName


    String getStoreCode(){
        return store?.code
    }

    String getStoreName(){
        return store?.name
    }

    /**
     * TODO: Unit Test
     * @return
     */
    String getAppNameForDisplay(){

        if(!appName) return null

        return (appName.size() <= MaxCharsForDisplayName) ? appName : appName.substring(0,MaxCharsForDisplayName) + '...'
    }

    String getAppUrl(){
        if(!appId) return null
        return "https://itunes.apple.com/${store?.code?.toLowerCase()}/app/x/id${appId.id}"
    }

    String toString() {
        return "$store) $appId) $appName"
    }
}
