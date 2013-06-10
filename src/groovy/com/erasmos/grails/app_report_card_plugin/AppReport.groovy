package com.erasmos.grails.app_report_card_plugin

/**
 * TODO: Unit Test
 */
class AppReport {

    AppId appId
    List<StoreAppReport> storeAppReports = []

    Integer getUserRatingCountForCurrentVersionOnly(){
        return storeAppReports.userRatingCountForCurrentVersionOnly.inject(0,{sum,value -> sum + value})
    }

    Integer getUserRatingCountForAllVersions(){
        return storeAppReports.userRatingCountForAllVersions.inject(0,{sum,value -> sum + value})
    }

    BigDecimal getAverageUserRatingForCurrentVersionOnly(){
        return getOverallAverageRating(storeAppReports.averageUserRatingForCurrentVersionOnly)

    }

    BigDecimal getAverageUserRatingForAllVersions(){
        return getOverallAverageRating(storeAppReports.averageUserRatingForAllVersions)
    }

    List<StoreAppReport> getStoreAppReportsWithRatings(){
        return storeAppReports.findAll {it.userRatingCountForAllVersions > 0 }
    }

    /**
     * We exclude any 0 as it is not a possible average reading, and implies that no ratings
     * were received.
     *
     * @param averages
     * @return
     */
    private BigDecimal getOverallAverageRating(final List<Float> averages){

        def qualifiedAverages  = averages.findAll {it>0}

        def number  = qualifiedAverages.size()
        def total   = qualifiedAverages.inject(0.0,{sum,value -> sum + value}) as float

        return (number ? total / number : 0.0) as BigDecimal
    }

    String toString(){

        def builder = new StringBuilder()
        builder.append("\n\n")
        builder.append("= Report Card For App ID (${appId}) ==")
        builder.append("\n\n")

        storeAppReports.each {
            builder.append(it.toString())
            builder.append("\n")
        }

        builder.append("\n\n")

        return builder.toString()
    }
}
