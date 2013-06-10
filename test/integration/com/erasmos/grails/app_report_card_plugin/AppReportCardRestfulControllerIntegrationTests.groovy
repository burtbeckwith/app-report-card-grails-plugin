package com.erasmos.grails.app_report_card_plugin

import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.junit.Before
import org.junit.Test

class AppReportCardRestfulControllerIntegrationTests extends GroovyTestCase {

    static final String ServerBaseURL = "http://localhost:8080/app-report-card"

    private AppReportCardRestfulController controller = new AppReportCardRestfulController()

    @Before
    void setUp() {

      //  controller.registerJSONMarshallers()

        controller.servletContext = ServletContextHolder.servletContext

        expectGetServerBaseURL(ServerBaseURL)

        ensureJSONMarshallersRegistered()
    }

    private void ensureJSONMarshallersRegistered(){

        def needToRegisterAgain = false
        [Store,StoreApp,StoreAppReport,AppReport].each {  Class _class ->

            if(_class.respondsTo('asType')){
                println("Yes - the class ($_class) does respond to 'asType'")
            }
            else{
                needToRegisterAgain = true
            }
        }

        if(needToRegisterAgain) {controller.registerJSONMarshallers()}
    }

    @Test
    void findAppByIdWhenItExists() {

        def store = Store.Canada
        def appId = new AppId(367242295)

        controller.params.storeCode    = store.code
        controller.params.appId        = appId.id as String

        controller.findAppById()

        def jsonMap = getSingleJSONObjectAsMap()

        assertEquals(10,jsonMap.size())

        assertEquals('CA',jsonMap.storeCode)
        assertEquals('Canada',jsonMap.storeName)
        assertEquals('367242295',jsonMap.appId)
        assertEquals('French Numbers',jsonMap.appName)
        assertEquals('https://itunes.apple.com/ca/app/x/id367242295',jsonMap.appUrl)
        assertEquals('French Numbers',jsonMap.appNameForDisplay)
        assertNotBlank(jsonMap.appSmallIconUrl)
        assertNotBlank(jsonMap.appCurrentVersion)
        assertEquals('Erasmos Inc',jsonMap.developerName)

        assertEquals(2,jsonMap.links.size())

        assertEquals('http://localhost:8080/app-report-card/appReportCardRestful/apps/367242295/report/CA',jsonMap.links.storeAppReportUrl)
        assertEquals('http://localhost:8080/app-report-card/appReportCardRestful/apps/367242295/report',jsonMap.links.appReportUrl)
    }

    @Test
    void findAppsByName(){

        def store   = Store.Canada
        def appName = 'Learn French'

        controller.params.storeCode = store.code
        controller.params.appName   = appName

        controller.findAppsByName()


        def jsonMaps = getMultipleJSONObjectsAsMap()

        assertFalse(jsonMaps.empty)

        jsonMaps.each {assertValidStoreAppAsMap(it)}
    }

    @Test
    void showAppReportForStore(){

        def store = Store.Canada
        def appId = new AppId(367242295)

        controller.params.storeCode    = store.code
        controller.params.appId        = appId.id as String

        controller.showAppReportForStore()

        def jsonMap = getSingleJSONObjectAsMap()

        assertEquals(12,jsonMap.size())

        assertEquals('CA',jsonMap.storeCode)
        assertEquals('Canada',jsonMap.storeName)
        assertEquals('367242295',jsonMap.appId)
        assertEquals('French Numbers',jsonMap.appName)
        assertEquals('French Numbers',jsonMap.appNameForDisplay)
        assertEquals('https://itunes.apple.com/ca/app/x/id367242295',jsonMap.appUrl)
        assertNumber(jsonMap.userRatingCountForAllVersions)
        assertNumber(jsonMap.averageUserRatingForAllVersions)
        assertNumber(jsonMap.userRatingCountForCurrentVersionOnly)
        assertNumber(jsonMap.averageUserRatingForCurrentVersionOnly)
        assertEquals(1,jsonMap.links.size())
        assertEquals('http://localhost:8080/app-report-card/appReportCardRestful/apps/367242295/report',jsonMap.links.appReportUrl)
    }

    @Test
    void showAllStores() {

        controller.showAllStores()

        def jsonMap = getMultipleJSONObjectsAsMap()

        assertEquals(Store.values().size(),jsonMap.size())
    }

    /**
     * Ensures that we have an icon for
     * each of the Stores.
     */
    @Test
    void showStoreIcon(){

        Store.values().each {
            Store store ->

                controller.params.storeCode = store.code

                controller.showStoreIcon()

                assertEquals(200, controller.response.status)
                assertTrue(controller.response.contentAsByteArray.length>0)
        }
    }

    @Test
    void showAppReport(){

        def appId = new AppId(367242295)
        controller.params.appId = appId.id as String

        controller.showAppReport()

        def jsonMap = getSingleJSONObjectAsMap()

        assertEquals(appId as String,jsonMap.appId)
        assertNumber(jsonMap.userRatingCountForAllVersions)
        assertNumber(jsonMap.averageUserRatingForAllVersions)
        assertNumber(jsonMap.userRatingCountForCurrentVersionOnly)
        assertNumber(jsonMap.averageUserRatingForCurrentVersionOnly)

        assertEquals(Store.values().size(),jsonMap.storeAppReports.size())

        jsonMap.storeAppReports.each {assertValidateStoreAppReportAsMap(it)}
    }

    private void assertValidateStoreAppReportAsMap(final Map storeAppReportAsMap){

        assertEquals(12,storeAppReportAsMap.size())

        assertNotBlank(storeAppReportAsMap.storeCode)

        def store =  Store.findByCode(storeAppReportAsMap.storeCode)
        assertNotNull(store)
        assertEquals(store.name,storeAppReportAsMap.storeName)
        assertNotBlank(storeAppReportAsMap.appId)
        assertNotBlank(storeAppReportAsMap.appName)
        assertNotBlank(storeAppReportAsMap.appNameForDisplay)
        assertNotBlank(storeAppReportAsMap.appUrl)
        assertNumber(storeAppReportAsMap.userRatingCountForAllVersions)
        assertNumber(storeAppReportAsMap.averageUserRatingForAllVersions)
        assertNumber(storeAppReportAsMap.userRatingCountForCurrentVersionOnly)
        assertNumber(storeAppReportAsMap.averageUserRatingForCurrentVersionOnly)
        assertEquals(1,storeAppReportAsMap.links.size())
        assertNotBlank(storeAppReportAsMap.links.appReportUrl)
    }

    private void assertNumber(def value){
        assertTrue(value instanceof Number)
    }

    private void assertValidStoreAppAsMap(final Map storeAppAsMap){

        assertEquals(10,storeAppAsMap.size())
        assertNotBlank(storeAppAsMap.storeCode)
        assertNotBlank(storeAppAsMap.storeName)
        assertNotBlank(storeAppAsMap.appId)
        assertNotBlank(storeAppAsMap.appName)
        assertNotBlank(storeAppAsMap.appUrl)
        assertNotBlank(storeAppAsMap.appNameForDisplay)
        assertNotBlank(storeAppAsMap.appSmallIconUrl)
        assertNotBlank(storeAppAsMap.appCurrentVersion)
        assertNotBlank(storeAppAsMap.developerName)
        assertEquals(2,storeAppAsMap.links.size())
        assertNotBlank(storeAppAsMap.links.storeAppReportUrl)
        assertNotBlank(storeAppAsMap.links.appReportUrl)
    }

    private Map getSingleJSONObjectAsMap(){

        assertNotNull(controller.response.json)

        def rawJSON = controller.response.text

        return new JsonSlurper().parseText(rawJSON)
    }

    private List<Map> getMultipleJSONObjectsAsMap(){

        assertNotNull(controller.response.json)

        def rawJSON = controller.response.text

        return new JsonSlurper().parseText(rawJSON)
    }

    private void assertNotBlank(final String thing){
        assertFalse(thing.trim().empty)
    }

    /**
     * I wasn't able to mock the LinkGenerator; no matter, Grails
     * automatically injects one, even for unit tests.
     *
     * @param returnedServerBaseURL
     */
    private void expectGetServerBaseURL(final String returnedServerBaseURL){

        controller.grailsLinkGenerator.metaClass.getServerBaseURL { ->
            return returnedServerBaseURL
        }
    }
}
