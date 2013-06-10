package com.erasmos.grails.app_report_card_plugin

import groovyx.gpars.GParsPool
import org.apache.commons.lang.time.StopWatch

class AppReportCardService {



    /**
     * Seems necessary, otherwise Grails looks for a TransactionManager (besides
     * the Hibernate one that doesn't seem to be included in a plug-in).
     */
    def static transactional = false


    ApiService apiService


    /**
     *
     * @param store
     * @param appName
     * @param maxResults May not be supported by the actual iTunes API.
     * @return
     */
    public List<StoreApp> findAppsByName(final Store store, final String appName, final int maxResults) {

        assert store != null, "Missing a Store"
        assert appName != null, "Missing an App Name"

        if(log.isDebugEnabled()) log.debug("Searching for apps for name ($appName) in Store ($store) ...")

        if(appName.trim().empty){
            if(log.isWarnEnabled()) log.warn("... ignoring blank appName")
            return []
        }

        def storesApps =  apiService.findRawStoreAppsByName(store,appName,maxResults).collect{convertToStoreApp(store,it)}

        if(log.isDebugEnabled()) log.debug("... we found ${storesApps.size()}")

        return storesApps
    }

    /**
     *
     * @param store
     * @param appId
     * @return
     */
    public StoreApp findAppById(final Store store, final AppId appId) {

        assert store != null, "Missing a Store"
        assert appId != null, "Missing an AppId"

        if(log.isDebugEnabled()) log.debug("Searching for the App for ID ($appId) in Store ($store) ...")


        def rawStoreApp = apiService.findRawStoreApp(store,appId)
        if(!rawStoreApp){
            if(log.isErrorEnabled()) log.error("... not found.")
            return null
        }

        def storeApp = convertToStoreApp(store,rawStoreApp)

        if(log.isDebugEnabled()) log.debug("... we found it: $storeApp")

        return storeApp
    }

    /**
     * The only way that we'll return a null StoreAppReport is we cannot
     * find an app by that AppId, in the Store.
     *
     * @param appId
     * @param store
     * @return
     */
    public StoreAppReport generateStoreAppReport(final Store store, final AppId appId){

        assert store != null, "Missing a Store"
        assert appId != null, "Missing an AppId"

        if(log.isDebugEnabled()) log.debug("Attempting to generate a StoreAppReport for App ($appId) and Store ($store). First we'll try and get the RawStoreApp ...")

        def rawStoreApp = apiService.findRawStoreApp(store,appId)
        if(!rawStoreApp){
            if(log.isErrorEnabled()) log.error("The App ($appId) doesn't seem to exist in the Store ($store).")
            return null

        }

        def storeApp =  new StoreApp(   store:store,
                appId: appId,
                appName: rawStoreApp.trackName,
                developerName: rawStoreApp.artistName)

        if(log.isDebugEnabled()) log.debug("... found it. We'll now use it along with the StoreApp ($storeApp) to generate the report ...")

        def storeAppReport =  convertToStoreAppReport(storeApp,rawStoreApp)

        if(log.isDebugEnabled()) log.debug("... done: $storeAppReport")

        return storeAppReport
    }



    /**
     * Generates a report on for AppId across all of the Stores.
     *
     * @param appId
     * @return
     */
    public AppReport generateAppReport(final AppId appId){

        assert appId != null, "Missing AppId"

        if(log.isDebugEnabled()) log.debug("About to generate the AppReport for AppId ($appId) ...")

        def stopWatch = new StopWatch()

        stopWatch.start()

        def storeAppReports = generateStoreAppReports(appId,getAllStores())

        def storeAppReportsSortedByStoreName = storeAppReports.sort {a,b -> a.storeName <=> b.storeName}

        def appReport = new AppReport(appId:appId, storeAppReports:storeAppReportsSortedByStoreName)

        stopWatch.stop()

        if(log.isDebugEnabled()) log.debug("... done. That took ${stopWatch.time} milliseconds.")

        return appReport
    }

    /**
     *
     * @param appId
     * @param stores
     * @return Note how we strip out any nulls, which we could have if that AppId doesn't exist in a given Store.
     */
    private List<StoreAppReport> generateStoreAppReports(final AppId appId, final List<Store> stores){

        def numberOfThreads = stores.size()

        if(log.isDebugEnabled()) log.debug("About to generate the StoreAppReports for AppId($appId) and ${stores.size()} Stores; the number of threads is: $numberOfThreads")

        GParsPool.withPool(numberOfThreads){
            return stores.makeConcurrent().collect{generateStoreAppReport(it, appId)}.findAll {it!=null}.makeSequential()
        }

    }

    /**
     * Currently the full list; but even so, it makes
     * unit tests easier.
     *
     * @return
     */
    public List<Store> getAllStores(){
        return Store.values()
    }

    /**
     *
     * @param storeApp
     * @param rawStoreApp
     * @return
     */
    private StoreAppReport convertToStoreAppReport(final StoreApp storeApp, final ApiService.RawStoreApp rawStoreApp){

        assert storeApp     != null, 'Missing StoreApp'
        assert rawStoreApp  != null, 'Missing RawStoreApp'

        def report = new StoreAppReport()

        report.storeApp                                 = storeApp
        report.averageUserRatingForAllVersions          = rawStoreApp['averageUserRating'] ?: 0
        report.averageUserRatingForCurrentVersionOnly   = rawStoreApp['averageUserRatingForCurrentVersion'] ?: 0
        report.userRatingCountForAllVersions            = rawStoreApp['userRatingCount'] ?: 0
        report.userRatingCountForCurrentVersionOnly     = rawStoreApp['userRatingCountForCurrentVersion'] ?: 0

        return report
    }

    /**
     *
     * @param store
     * @param rawStoreApp
     * @return
     */
    private StoreApp convertToStoreApp(final Store store,final ApiService.RawStoreApp rawStoreApp){

        assert store    != null, 'Missing StoreApp'
        assert rawStoreApp  != null, 'Missing RawStoreApp'

        return new StoreApp(store: store,
                appId:new AppId(rawStoreApp.trackId as Long),
                appName: rawStoreApp.trackName,
                appSmallIconUrl: rawStoreApp.artworkUrl60,
                appCurrentVersion:rawStoreApp.version,
                developerName: rawStoreApp.artistName,
        )

    }

}
