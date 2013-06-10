package com.erasmos.grails.app_report_card_plugin

/**
 * Original fields:
 *
 * // averageUserRating
 // averageUserRatingForCurrentVersion
 // userRatingCount
 // userRatingCountForCurrentVersion

 */
class StoreAppReport {

    StoreApp storeApp

    BigDecimal averageUserRatingForAllVersions
    Integer userRatingCountForAllVersions

    BigDecimal averageUserRatingForCurrentVersionOnly
    Integer userRatingCountForCurrentVersionOnly

    public Store getStore(){
        return storeApp?.store
    }

    public String getStoreName(){
        return storeApp?.store?.name
    }

    public String getStoreCode(){
        return storeApp?.store?.code
    }

    public String getAppName(){
        return storeApp?.appName
    }

    public String getAppNameForDisplay(){
        return storeApp?.appNameForDisplay
    }

    public String getAppUrl(){
        return storeApp?.appUrl
    }

    public AppId getAppId() {
        return storeApp?.appId
    }

    public String getDeveloperName(){
        return storeApp?.developerName
    }

    String toString() {

        def builder = new StringBuilder()

        builder.append("\n")
        builder.append("\n")
        builder.append("=== Store App (${storeApp}) ===")
        builder.append("\n")

        builder.append("Current Version: Number of Ratings: ${userRatingCountForCurrentVersionOnly}")
        builder.append("\n")
        builder.append("Current Version: Average Rating: ${averageUserRatingForCurrentVersionOnly}")
        builder.append("\n")

        builder.append("All Versions: Number of Ratings: ${userRatingCountForAllVersions}")
        builder.append("\n")
        builder.append("All Version: Average Rating: ${averageUserRatingForAllVersions}")
        builder.append("\n")

        builder.append("\n")
        builder.append("=================================")
        return builder.toString()
    }

}
