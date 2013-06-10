package com.erasmos.grails.app_report_card_plugin



import grails.test.mixin.*
import org.codehaus.groovy.grails.web.json.JSONArray
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(AppReportCardRestfulController)
class AppReportCardRestfulControllerTests {

    static String ServerBaseURL = "http://localhost:8080/app-report-card"

    def mockControlAppReportCardService


    @Before
    void setUp() {

        controller.registerJSONMarshallers()
        mockControlAppReportCardService = mockFor(AppReportCardService)
        controller.appReportCardService = mockControlAppReportCardService.createMock()

        expectGetServerBaseURL(ServerBaseURL)

    }

    @After
    void tearDown(){
        clearJSONMarshallers()
    }



    @Test
    void findAppByIdWhenNoStore() {

        assertNull(params.storeCode)

        controller.findAppById()

        assertEquals(400,response.status)
        assertEquals("Store Code Required",response.text)

    }

    @Test
    void findAppByIdWhenUnknownStore() {

        def storeCode = 'XX'
        params.storeCode = storeCode
        assertNull(Store.findByCode(storeCode))

        controller.findAppById()

        assertEquals(404,response.status)
        assertEquals("Unknown Store: $storeCode".toString(),response.text)

    }

    @Test
    void findAppByIdWhenNoId() {

        params.storeCode = Store.UnitedKingdom.code

        assertNull(params.appId)

        controller.findAppById()

        assertEquals(400,response.status)
        assertEquals("App ID Required",response.text)

    }

    @Test
    void findAppByIdWhenInvalid() {

        params.storeCode = Store.UnitedKingdom.code

        params.appId = 'NotANumber'

        controller.findAppById()

        assertEquals(400,response.status)
        assertEquals("App ID Required",response.text)

    }

    @Test
    void findAppByIdWhenZero() {

        params.storeCode = Store.UnitedKingdom.code

        params.appId = 0

        controller.findAppById()

        assertEquals(400,response.status)
        assertEquals("App ID Required",response.text)

    }

    @Test
    void findAppByIdWhenItDoesNotExist() {

        def store = Store.Belarus
        def appId = new AppId(88888888)

        params.storeCode    = store.code
        params.appId        = appId.id as String

        def returnedStoreApp = null
        expectFindAppById(store,appId,returnedStoreApp)

        controller.findAppById()

        assertEquals(404,response.status)
        assertEquals("App for ID (${appId.id}) wasn't found in the Store (${store.code})".toString(),response.text)

    }

    @Test
    void findAppByIdWhenItExists() {

        def store = Store.Belarus
        def appId = new AppId(88888888)

        params.storeCode    = store.code
        params.appId        = appId.id as String

        def returnedStoreApp = new StoreApp()
        expectFindAppById(store,appId,returnedStoreApp)

        def returnedMapForJson = [:]
        expectAsMapForJSON(returnedStoreApp,returnedMapForJson)

        controller.findAppById()

        assertEquals(returnedMapForJson, response.json)

    }


    @Test
    void findAppsByNameWhenNoStore(){

        assertNull(params.storeCode)

        controller.findAppsByName()

        assertEquals(400,response.status)
        assertEquals("Store Code Required",response.text)

    }

    @Test
    void findAppsByNameWhenUnknownStore(){

        def storeCode = 'XX'
        params.storeCode = storeCode
        assertNull(Store.findByCode(storeCode))

        controller.findAppsByName()

        assertEquals(404,response.status)
        assertEquals("Unknown Store: $storeCode".toString(),response.text)

    }

    @Test
    void findAppsByNameWhenNoAppName(){

        def store = Store.Qatar
        params.storeCode = store.code

        assertNull(params.appName)

        controller.findAppsByName()

        assertEquals(400,response.status)
        assertEquals('App Name Required',response.text)

    }

    @Test
    void findAppsByName(){

        def store = Store.Denmark
        params.storeCode = store.code

        def appName = 'Clarity'
        params.appName = appName

        def returnedStoreAppOne = new StoreApp()
        def returnedStoreAppTwo = new StoreApp()

        def returnedStoreApps = [returnedStoreAppOne,returnedStoreAppTwo]

        expectFindAppsByName(store,appName,AppReportCardRestfulController.DefaultMaxResults,returnedStoreApps)

        def returnedMapForStoreAppOne = [:]
        def returnedMapForStoreAppTwo = [:]

        controller.metaClass.asMapForJSON {
            StoreApp _storeApp ->
                switch (_storeApp) {
                    case returnedStoreAppOne : return returnedMapForStoreAppOne
                    case returnedStoreAppTwo : return returnedMapForStoreAppTwo
                    default: fail("Unexpected StoreApp: $_storeApp")

                }
        }


        controller.findAppsByName()

        assertEquals(200,response.status)
        assertNotNull(response.json)

    }

    @Test
    void showAppReportCardWhenNoId(){

        assertNull(params.appId)

        controller.showAppReport()

        assertEquals(400,response.status)
        assertEquals("App ID Required",response.text)

    }

    @Test
    void showAppReportCardWhenInvalidId(){

        params.appId = 'NotANumber'

        controller.showAppReport()

        assertEquals(400,response.status)
        assertEquals("App ID Required",response.text)

    }

    @Test
    void showAppReport(){

        def appId = new AppId(367242295)
        params.appId = appId as String

        def returnedAppReport = new AppReport()
        expectGenerateAppReport(appId,returnedAppReport)

        def returnedAppReportAsMap = [:]
        expectAsMapForJSON(returnedAppReport,returnedAppReportAsMap)

        controller.showAppReport()

        assertNotNull(response.json)

    }

    @Test
    void showAppReportForStoreWhenNoStore(){

        assertNull(params.storeCode)

        controller.showAppReportForStore()

        assertStoreRequiredResponse()

    }


    @Test
    void showAppReportForStoreWhenInvalidStore(){

        params.storeCode = 'XX'
        assertNull(Store.findByCode(params.storeCode))

        controller.showAppReportForStore()

        assertUnknownStoreResponse()

    }

    @Test
    void showAppReportForStoreWhenNoAppId(){

        def store = Store.HongKong
        params.storeCode = store.code

        assertNull(params.appId)

        controller.showAppReportForStore()

        assertEquals(400,response.status)
        assertEquals("App ID Required",response.text)

    }


    @Test
    void showAppReportForStoreWhenInvalidAppId(){

        def store = Store.HongKong
        params.storeCode = store.code

        params.appId = 'NotANumber'

        controller.showAppReportForStore()

        assertEquals(400,response.status)
        assertEquals("App ID Required",response.text)

    }

    @Test
    void showAppReportForStoreWhenNotReportReturned(){

        def store = Store.HongKong
        params.storeCode = store.code

        def appId = new AppId(367242295)
        params.appId = appId as String

        def returnedStoreAppReport = null
        expectGenerateStoreAppReport(store,appId,returnedStoreAppReport)

        controller.showAppReportForStore()

        assertEquals(404,response.status)
        assertEquals("Report not found for Store (${store.code}) and App ID ($appId)".toString(),response.text)

    }

    @Test
    void showAppReportForStore(){

        def store = Store.HongKong
        params.storeCode = store.code

        def appId = new AppId(367242295)
        params.appId = appId as String

        def returnedStoreAppReport = new StoreAppReport()
        expectGenerateStoreAppReport(store,appId,returnedStoreAppReport)

        def returnedStoreAppReportAsMap = [:]
        expectAsMapForJSON(returnedStoreAppReport,returnedStoreAppReportAsMap)

        controller.showAppReportForStore()

        assertNotNull(response.json)

    }

    @Test
    void showAllStores(){

        controller.showAllStores()

        assertTrue(response.json instanceof JSONArray)
        def storesAsJSON = response.json as JSONArray
        assertEquals(Store.values().size(),storesAsJSON.size())

        def expectedFirstStore = Store.allSortedByName.first()
        def firstStoreAsJSON = storesAsJSON.first()
        assertEquals(expectedFirstStore.code,firstStoreAsJSON['code'])


    }

    @Test
    void showStoreIconWhenNoStore(){

        assertNull(params.storeCode)

        controller.showStoreIcon()

        assertEquals(400,response.status)
        assertEquals("Store Code Required",response.text)

    }


    @Test
    void showStoreIconWhenUnknownStore(){

        def storeCode = 'XX'
        params.storeCode = storeCode
        assertNull(Store.findByCode(storeCode))

        controller.showStoreIcon()

        assertEquals(404,response.status)
        assertEquals("Unknown Store: $storeCode".toString(),response.text)

    }

    @Test
    void showStoreIconWhenIconFileNotFound(){

        def store = Store.Japan
        params.storeCode = store.code

        def expectedResourcePath = '/images/stores/JP.png'

        def returnedContent = null
        expectGetResourceContents(expectedResourcePath,returnedContent)

        controller.showStoreIcon()

        assertEquals(404,response.status)
        assertEquals("Icon not found for Store: JP",response.text)

    }

    @Test
    void showStoreIcon(){

        def store = Store.Canada
        params.storeCode = store.code

        def expectedResourcePath = '/images/stores/CA.png'

        def returnedContent = "Pretend this is an icon".bytes
        expectGetResourceContents(expectedResourcePath,returnedContent)

        controller.showStoreIcon()

        assertEquals('image/png',response.contentType)
        // TODO: Fails when run with 'test-app'
        // assertArrayEquals(returnedContent,response.contentAsByteArray)
        assertEquals(returnedContent.size(),response.contentAsByteArray.size())

    }

    @Test
    void getStoreAsMapForJSON(){

        def store = Store.Iceland

        def storeAsMap  = controller.asMapForJSON(store)

        assertEquals(store.code,storeAsMap['code'])
        assertEquals(store.name,storeAsMap['name'])
        assertEquals("$ServerBaseURL/appReportCardRestful/stores/IS/icon" as String,storeAsMap['icon'])

    }

    @Test
    void getStoreAppReportAsMapForJSON(){

        def store = Store.Canada
        def appId = new AppId(367242295)
        def storeApp = new StoreApp(
                appId: appId,
                store:store,
                appName: 'French Numbers',
                developerName: 'Erasmos Inc')

        def storeAppReport = new StoreAppReport(
                storeApp:storeApp,
                userRatingCountForAllVersions:1000,
                averageUserRatingForAllVersions:4.2842,
                userRatingCountForCurrentVersionOnly:200,
                averageUserRatingForCurrentVersionOnly: 3.856
        )

        def storeAppReportAsMap = controller.asMapForJSON(storeAppReport)

        assertEquals(store.code,storeAppReportAsMap['storeCode'])
        assertEquals(store.name,storeAppReportAsMap['storeName'])
        assertEquals(appId as String,storeAppReportAsMap['appId'])
        assertEquals(storeApp.appName,storeAppReportAsMap['appName'])
        assertEquals(storeApp.appNameForDisplay,storeAppReportAsMap['appNameForDisplay'])
        assertEquals(storeApp.appUrl,storeAppReportAsMap['appUrl'])
        assertEquals(storeApp.developerName,storeAppReportAsMap['developerName'])
        assertEquals(storeAppReport.userRatingCountForAllVersions,storeAppReportAsMap['userRatingCountForAllVersions'])
        assertEquals('4.28',storeAppReportAsMap['averageUserRatingForAllVersions'] as String)
        assertEquals(storeAppReport.userRatingCountForCurrentVersionOnly,storeAppReportAsMap['userRatingCountForCurrentVersionOnly'])
        assertEquals('3.86',storeAppReportAsMap['averageUserRatingForCurrentVersionOnly'] as String)

        def links = storeAppReportAsMap['links'] as Map
        assertEquals(1,links.size())
        assertEquals("$ServerBaseURL/appReportCardRestful/apps/367242295/report" as String, links['appReportUrl'])


    }


    @Test
    void getStoreAppAsMapForJSON(){

        def store = Store.Canada
        def appId = new AppId(367242295)
        def storeApp = new StoreApp(
                appId: appId,
                store:store,
                appName: 'French Numbers',
                developerName: 'Erasmos Inc',
                appCurrentVersion: '4.2.0')

        def storeAppAsMap = controller.asMapForJSON(storeApp)

        assertEquals(store.code, storeAppAsMap['storeCode'])
        assertEquals(store.name, storeAppAsMap['storeName'])
        assertEquals(appId as String, storeAppAsMap['appId'])
        assertEquals(storeApp.appName, storeAppAsMap['appName'])
        assertEquals(storeApp.appUrl, storeAppAsMap['appUrl'])
        assertEquals(storeApp.appNameForDisplay, storeAppAsMap['appNameForDisplay'])
        assertEquals(storeApp.appSmallIconUrl, storeAppAsMap['appSmallIconUrl'])
        assertEquals(storeApp.appCurrentVersion, storeAppAsMap['appCurrentVersion'])
        assertEquals(storeApp.developerName, storeAppAsMap['developerName'])

        def links = storeAppAsMap['links'] as Map
        assertEquals(2,links.size())
        assertEquals("${ServerBaseURL}/appReportCardRestful/apps/367242295/report/CA" as String,links['storeAppReportUrl'])
        assertEquals("${ServerBaseURL}/appReportCardRestful/apps/367242295/report" as String,links['appReportUrl'])

    }

    @Test
    void getAppReportAsMapForJSON(){

        def storeCanada         = Store.Canada
        def storeUnitedKingdom  = Store.UnitedKingdom

        def appId = new AppId(367242295)

        def storeAppForCanada = new StoreApp(
                appId:appId,
                store:storeCanada,
                appName: 'French Numbers',
                developerName: 'Erasmos Inc')

        def storeAppForUnitedKingdom = new StoreApp(
                appId:appId,
                store:storeUnitedKingdom,
                appName: 'French Numbers',
                developerName: 'Erasmos Inc')


        def storeAppReportForCanada = new StoreAppReport(
                storeApp:storeAppForCanada,
                userRatingCountForAllVersions:1000,
                averageUserRatingForAllVersions:3.00,
                userRatingCountForCurrentVersionOnly:200,
                averageUserRatingForCurrentVersionOnly: 4.00
        )

        def storeAppReportForUnitedKingdom = new StoreAppReport(
                storeApp:storeAppForUnitedKingdom,
                userRatingCountForAllVersions:400,
                averageUserRatingForAllVersions:5.00,
                userRatingCountForCurrentVersionOnly:50,
                averageUserRatingForCurrentVersionOnly: 3.00
        )


        def appReport = new AppReport(appId:appId,storeAppReports: [storeAppReportForCanada,storeAppReportForUnitedKingdom])

        def appReportAsMapForJSON = controller.asMapForJSON(appReport)

        assertEquals(appId as String,appReportAsMapForJSON['appId'])
        assertEquals(1400,appReportAsMapForJSON['userRatingCountForAllVersions'])
        assertEquals(4.0,appReportAsMapForJSON['averageUserRatingForAllVersions'],0.01)
        assertEquals(250,appReportAsMapForJSON['userRatingCountForCurrentVersionOnly'])
        assertEquals(3.5,appReportAsMapForJSON['averageUserRatingForCurrentVersionOnly'],0.01)

        def storeAppReportsAsMapsForJSON = appReportAsMapForJSON['storeAppReports'] as List
        assertEquals(2,storeAppReportsAsMapsForJSON.size())

        def storeAppReportForCanadaAsMap = storeAppReportsAsMapsForJSON.first()
        assertEquivalent(storeAppReportForCanada,storeAppReportForCanadaAsMap)

        def storeAppReportForUnitedKingdomAsMap = storeAppReportsAsMapsForJSON.last()
        assertEquivalent(storeAppReportForUnitedKingdom,storeAppReportForUnitedKingdomAsMap)


    }

    private void assertEquivalent(final StoreAppReport storeReportApp, final Map storeAppReportAsMapForJSON){

        assertEquals(storeReportApp.store.code,storeAppReportAsMapForJSON['storeCode'])
        assertEquals(storeReportApp.store.name,storeAppReportAsMapForJSON['storeName'])
        assertEquals(storeReportApp.appId as String,storeAppReportAsMapForJSON['appId'])
        assertEquals(storeReportApp.appName,storeAppReportAsMapForJSON['appName'])
        assertEquals(storeReportApp.appNameForDisplay,storeAppReportAsMapForJSON['appNameForDisplay'])
        assertEquals(storeReportApp.appUrl,storeAppReportAsMapForJSON['appUrl'])
        assertEquals(storeReportApp.developerName,storeAppReportAsMapForJSON['developerName'])
        assertEquals(storeReportApp.userRatingCountForAllVersions,storeAppReportAsMapForJSON['userRatingCountForAllVersions'])
        assertEquals(storeReportApp.averageUserRatingForAllVersions,storeAppReportAsMapForJSON['averageUserRatingForAllVersions'],0.01)
        assertEquals(storeReportApp.userRatingCountForCurrentVersionOnly,storeAppReportAsMapForJSON['userRatingCountForCurrentVersionOnly'])
        assertEquals(storeReportApp.averageUserRatingForCurrentVersionOnly,storeAppReportAsMapForJSON['averageUserRatingForCurrentVersionOnly'],0.01)

        def links = storeAppReportAsMapForJSON['links'] as Map
        assertEquals(1,links.size())
        assertEquals("$ServerBaseURL/appReportCardRestful/apps/${storeReportApp.appId.id}/report" as String, links['appReportUrl'])

    }

    private void expectGetResourceContents(final String expectedResourcePath, final byte[] returnedContent){

        controller.metaClass.getResourceContents {
            String _resourcePath ->
                assertEquals(expectedResourcePath,_resourcePath)
                return returnedContent

        }
    }

    private void expectGenerateStoreAppReport(final Store expectedStore, final AppId expectedAppId, final StoreAppReport returnedStoreAppReport){

        mockControlAppReportCardService.demand.generateStoreAppReport  {
            Store _store,
            AppId _appId ->
                assertEquals(expectedStore,_store)
                assertEquals(expectedAppId,_appId)
                return returnedStoreAppReport
        }
    }


    private void assertStoreRequiredResponse(){

        assertEquals(400,response.status)
        assertEquals("Store Code Required",response.text)
    }

    private void assertUnknownStoreResponse(){

        assertEquals(404,response.status)
        assertEquals("Unknown Store: ${params.storeCode}".toString(),response.text)
    }

    private void expectGenerateAppReport(final AppId expectedAppId, final AppReport returnedAppReport){

        mockControlAppReportCardService.demand.generateAppReport {
            AppId _appId ->
                assertEquals(expectedAppId,_appId)
                return returnedAppReport
        }
    }

    private void expectFindAppsByName(final Store expectedStore, final String expectedAppName, final int expectedMaxResults, final List<StoreApp> returnedStoreApps) {

        mockControlAppReportCardService.demand.findAppsByName {
            Store _store,
            String _appName,
            int _maxResults ->
                assertEquals(expectedStore,_store)
                assertEquals(_appName,expectedAppName)
                assertEquals(_maxResults,expectedMaxResults)
                return returnedStoreApps
        }
    }

    private void expectAsMapForJSON(final StoreApp expectedStoreApp, final Map returnedMap){

        controller.metaClass.asMapForJSON {
            StoreApp _storeApp ->
                assertSame(expectedStoreApp,_storeApp)
                return returnedMap
        }
    }

    private void expectAsMapForJSON(final AppReport expectedAppReport, final Map returnedMap){

        controller.metaClass.asMapForJSON {
            AppReport _appReport ->
                assertSame(expectedAppReport,_appReport)
                return returnedMap
        }
    }

    private void expectAsMapForJSON(final StoreAppReport expectedStoreAppReport, final Map returnedMap){

        controller.metaClass.asMapForJSON {
            StoreAppReport _storeAppReport ->
                assertSame(expectedStoreAppReport,_storeAppReport)
                return returnedMap
        }
    }


    private void expectFindAppById(final Store expectedStore, final AppId expectedAppId, final StoreApp returnedStoreApp){

        mockControlAppReportCardService.demand.findAppById {
            Store _store,
            AppId _appId ->
                assertEquals(expectedStore,_store)
                assertEquals(expectedAppId,_appId)
                return returnedStoreApp
        }
    }

    /**
     * I wasn't able to mock the LinkGenerator; no matter, Grails
     * automatically injects one, even for unit tests.
     *
     * @param returnedServerBaseURL
     */
    private void expectGetServerBaseURL(final String returnedServerBaseURL){

        controller.grailsLinkGenerator.metaClass.getServerBaseURL {
            ->
            return returnedServerBaseURL
        }

    }

    /**
     * For some reason this was necessary when all of the test classes were run
     * by test-app; when test-app was run only with this class, everything was fine.
     */
    private void clearJSONMarshallers(){

        [Store,StoreApp,StoreAppReport,AppReport].each {
            it.metaClass.asType = null
        }
    }

}
