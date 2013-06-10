package com.erasmos.grails.app_report_card_plugin

import grails.converters.JSON
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import org.springframework.http.HttpStatus

import javax.servlet.ServletContext
import java.text.DecimalFormat

class AppReportCardRestfulController {

    static DecimalFormat DefaultDecimalFormat   = new DecimalFormat('0.00')
    static int DefaultMaxResults                = 200
    static String StoreIconContentType          = 'image/png'
    static String RestfulUrlBase                = 'appReportCardRestful' // TODO: Could probably be figured out with LinkGenerator

    AppReportCardService appReportCardService
    LinkGenerator grailsLinkGenerator
    ServletContext servletContext

    def index(){

    }

    /**
     *
     */
    def findAppById(){

        def requestedStore = figureRequestedStore()
        if(!requestedStore) return

        def requestedAppId = figureRequestedAppId()
        if(!requestedAppId) return

        def storeApp = appReportCardService.findAppById(requestedStore,requestedAppId)
        if(!storeApp){
            renderNotFoundResponse("App for ID ($requestedAppId) wasn't found in the Store (${requestedStore.code})")
            return
        }

        renderAsJSON(storeApp)

    }



    /**
     * TODO: Would be nice to pass in a max.
     */
    def findAppsByName() {

        def requestedStore = figureRequestedStore()
        if(!requestedStore) return

        if(!params.appName){
            renderBadRequestResponse('App Name Required')
            return
        }

        def apps = appReportCardService.findAppsByName(requestedStore,params.appName,DefaultMaxResults)

        renderAsJSON(apps)

    }

    /**
     *
     */
    def showAppReport() {

        def requestedAppId = figureRequestedAppId()
        if(!requestedAppId) return

        def appReport = appReportCardService.generateAppReport(requestedAppId)

        renderAsJSON(appReport)
    }

    /**
     * Note the possibility of not being able to generate such a report.
     *
     */
    def showAppReportForStore() {

        def requestedStore = figureRequestedStore()
        if(!requestedStore) return

        def requestedAppId = figureRequestedAppId()
        if(!requestedAppId) return

        def storeAppReport = appReportCardService.generateStoreAppReport(requestedStore,requestedAppId)
        if(!storeAppReport){
            renderNotFoundResponse("Report not found for Store (${requestedStore.code}) and App ID ($requestedAppId)")
            return
        }

        storeAppReport = storeAppReport!= null ? storeAppReport : [:]

        renderAsJSON(storeAppReport)

    }




    /**
     *
     */
    def showAllStores() {
        renderAsJSON(Store.allSortedByName)
    }

    def showStoreIcon() {

        def requestedStore = figureRequestedStore()
        if(!requestedStore) return

        def iconPath = figureIconPath(requestedStore)
        def iconAsBytes =  getResourceContents(iconPath)
        if(!iconAsBytes){
            if(log.isErrorEnabled()) log.error("Unable to find the Icon at path: $iconPath")
            renderNotFoundResponse("Icon not found for Store: ${requestedStore.code}")
            return
        }

        response.contentType = StoreIconContentType
        response.outputStream << iconAsBytes

    }



    public void registerJSONMarshallers(){


        int marshallerPriority = 0

        [Store,StoreApp,StoreAppReport,AppReport].each {  Class _class ->
            JSON.registerObjectMarshaller(_class,marshallerPriority) {return asMapForJSON(it)}
        }

    }

    private AppId figureRequestedAppId(){

        def rawAppId    = params.int('appId')
        if(!rawAppId){
            renderBadRequestResponse('App ID Required')
            return null
        }

        return new AppId(rawAppId)
    }


    private byte[] getResourceContents(final String resourcePath){
        assert resourcePath != null
        servletContext.getResource(resourcePath)?.bytes
    }

    /**
     * When running standalone, the $pluginContextPath should be empty
     * @param store
     * @return
     */
    private String figureIconPath(final Store store){
        assert store != null

        return "$pluginContextPath/images/stores/${store.code}.png"
    }

    /**
     * Given the current mapping, it's not possible to have a null storeCode.
     *
     * @return
     */
    private Store figureRequestedStore(){

        if(!params.storeCode){
            renderBadRequestResponse('Store Code Required')
            return null
        }

        def store = Store.findByCode(params.storeCode)
        if(!store){
            renderNotFoundResponse("Unknown Store: ${params.storeCode}")
            return null
        }

        return store

    }

    private void renderBadRequestResponse(final String errorMessage){
        render(text: errorMessage, status: HttpStatus.BAD_REQUEST.value())
    }

    private void renderNotFoundResponse(final String errorMessage){
        render(text: errorMessage, status: HttpStatus.NOT_FOUND.value())
    }



    private Map asMapForJSON(final Store store){

        assert store != null

        return [
                code: store.code,
                name: store.name,
                icon: generateLinkToStoreIcon(store)
        ]
    }

    /**
     * @param appReport
     * @return
     */
    private Map asMapForJSON(final AppReport appReport){

        assert appReport != null

        def storeAppReportsAsMapsForJSON =  appReport.storeAppReports.collect{asMapForJSON(it)}

        return [
                appId:                                  appReport.appId as String,
                userRatingCountForAllVersions:          appReport.userRatingCountForAllVersions,
                averageUserRatingForAllVersions:        format(appReport.averageUserRatingForAllVersions),
                userRatingCountForCurrentVersionOnly:   appReport.userRatingCountForCurrentVersionOnly,
                averageUserRatingForCurrentVersionOnly: format(appReport.averageUserRatingForCurrentVersionOnly),
                storeAppReports:                        storeAppReportsAsMapsForJSON,

        ]
    }

    /**
     * @param storeAppReport
     * @return
     */
    private Map asMapForJSON(final StoreAppReport storeAppReport){

        assert storeAppReport != null

        return [
                storeCode:                              storeAppReport.storeCode,
                storeName:                              storeAppReport.storeName,
                appId:                                  storeAppReport.appId as String,
                appName:                                storeAppReport.appName,
                appNameForDisplay:                      storeAppReport.appNameForDisplay,
                appUrl:                                 storeAppReport.appUrl,
                developerName:                          storeAppReport.developerName,
                userRatingCountForAllVersions:          storeAppReport.userRatingCountForAllVersions,
                averageUserRatingForAllVersions:        format(storeAppReport.averageUserRatingForAllVersions),
                userRatingCountForCurrentVersionOnly:   storeAppReport.userRatingCountForCurrentVersionOnly,
                averageUserRatingForCurrentVersionOnly: format(storeAppReport.averageUserRatingForCurrentVersionOnly),
                links: [
                        appReportUrl:generateLinkToAppReport(storeAppReport.appId)
                ]

        ]
    }



    /**
     *
     * @param storeApp
     * @return
     */
    private Map asMapForJSON(final StoreApp storeApp){

        assert storeApp != null

        return [
                storeCode:          storeApp.storeCode,
                storeName:          storeApp.storeName,
                appId:              storeApp.appId.toString(),
                appName:            storeApp.appName,
                appUrl:             storeApp.appUrl,
                appNameForDisplay:  storeApp.appNameForDisplay,
                appSmallIconUrl:    storeApp.appSmallIconUrl,
                appCurrentVersion:  storeApp.appCurrentVersion,
                developerName:      storeApp.developerName,
                links: [
                        storeAppReportUrl:  generateLinkToStoreAppReport(storeApp.store,storeApp.appId),
                        appReportUrl:       generateLinkToAppReport(storeApp.appId)
                ]
        ]
    }

    /**
     *
     * @param store
     * @param appId
     * @return
     */
    private String generateLinkToStoreAppReport(final Store store, final AppId appId) {

        assert store != null
        assert appId != null

        return "$serverBaseURL/$RestfulUrlBase/apps/${appId.id}/report/${store.code}"
    }

    /**
     *
     * @param appId
     * @return
     */
    private String generateLinkToAppReport(final AppId appId) {

        assert appId != null

        return "$serverBaseURL/$RestfulUrlBase/apps/${appId.id}/report"
    }

    /**
     *
     * @param store
     * @return
     */
    private String generateLinkToStoreIcon(final Store store) {

        assert store != null

        return "$serverBaseURL/$RestfulUrlBase/stores/${store.code}/icon"
    }

    /**
     *
     * @return
     */
    private String getServerBaseURL(){
        return grailsLinkGenerator.serverBaseURL
    }

    /**
     *
     * @param original
     * @return
     */
    private static Double format(final BigDecimal original){
        if(!original) return new Double(0)
        return DefaultDecimalFormat.format(original.doubleValue()) as Double
    }


    /**
     * This is probably a kludge; I added it as some of the integration
     * tests were failing with JSON related errors, but only when
     * I ran all of the tests together.
     *
     * @param somethingThatCanBeRenderedAsJSON
     */
    private void renderAsJSON(def somethingThatCanBeRenderedAsJSON){

        try {
            render(somethingThatCanBeRenderedAsJSON as JSON)
        }
        catch(GroovyCastException ex){

            if(log.isWarnEnabled()) log.warn("We failed to render as JSON: $somethingThatCanBeRenderedAsJSON")

            registerJSONMarshallers()  // Seemingly ignore, but one can hope.

            if(somethingThatCanBeRenderedAsJSON instanceof List){

                def listOfMaps =  ((List)somethingThatCanBeRenderedAsJSON).collect{asMapForJSON(it)}
                render(listOfMaps as JSON)
            }
            else{
                render(asMapForJSON(somethingThatCanBeRenderedAsJSON) as JSON)
            }

        }
    }

}
