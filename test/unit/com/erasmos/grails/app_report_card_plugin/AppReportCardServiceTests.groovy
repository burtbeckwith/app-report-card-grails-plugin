package com.erasmos.grails.app_report_card_plugin

import grails.test.mixin.TestFor

import org.junit.Before
import org.junit.Test

import com.erasmos.grails.app_report_card_plugin.ApiService.RawStoreApp

@TestFor(AppReportCardService)
class AppReportCardServiceTests {

    private static final int MaxResults = 50

    def mockControlForAPIService

    @Before
    void setUp(){

        mockControlForAPIService    =  mockFor(ApiService)
        service.apiService          = mockControlForAPIService.createMock()
    }


    @Test
    void findAppsByNameWhenBlank() {

        def store = Store.UnitedKingdom

        def appName = ' '

        def apps = service.findAppsByName(store,appName,MaxResults)

        assertTrue(apps.empty)
    }

    @Test
    void findAppsByNameWhenSomeExist() {

        def store       = Store.UnitedKingdom
        def appName     = 'French Numbers'
        def maxResults  = 100

        def rawAppOne = generateRawStoreApp(367242295L,'French Numbers','Erasmos Inc')
        def rawAppTwo = generateRawStoreApp(367242296,'French Alphabet','Erasmos Inc')

        def returnedRawApps = [rawAppOne,rawAppTwo]
        expectFindRawStoreAppsByName(store,appName,maxResults,returnedRawApps)

        def apps = service.findAppsByName(store,appName,maxResults)

        assertEquals(2,apps.size())

        assertEquivalent(rawAppOne,apps[0],store)
        assertEquivalent(rawAppTwo,apps[1],store)
    }

    @Test
    void findAppsByNameWhenNoneExist() {

        def store = Store.UnitedKingdom
        def appName = 'French Numbers'

        def returnedRawApps = []
        expectFindRawStoreAppsByName(store,appName,MaxResults,returnedRawApps)

        def apps = service.findAppsByName(store,appName,MaxResults)

        assertTrue(apps.empty)
    }

    @Test
    void findAppByIdWhenItDoesNotExist(){

        def store = Store.UnitedKingdom
        def appId = new AppId(367242295)

        def returnedRawStoreApp = null
        expectFindRawStoreApp(store,appId,returnedRawStoreApp)

        def storeApp = service.findAppById(store,appId)

        assertNull(storeApp)
    }

    @Test
    void findAppByIdWhenItDoesExist(){

        def store = Store.UnitedKingdom
        def appId = new AppId(367242295)

        def returnedRawStoreApp = generateRawStoreApp(367242295,'French Numbers','Erasmos Inc')
        expectFindRawStoreApp(store,appId,returnedRawStoreApp)

        def storeApp = service.findAppById(store,appId)

        assertEquivalent(returnedRawStoreApp,storeApp,store)
    }

    @Test
    void convertRawStoreAppToStoreApps(){

        def store = Store.UnitedKingdom

        def rawStoreApp = [
                trackId:'367242295',
                trackName:'French Numbers',
                artistName:'Erasmos Inc',
                artworkUrl60:'http://a600.phobos.apple.com/us/r1000/075/Purple2/v4/0a/7e/1e/0a7e1ef6-a4bb-1cef-738b-b8c0626c7ddc/57.png',
                appCurrentVersion:'4.20',

        ] as ApiService.RawStoreApp

        def storeApp = service.convertToStoreApp(store,rawStoreApp)

        assertEquals(store, storeApp.store)
        assertEquals(rawStoreApp.trackId as Long,storeApp.appId.id)
        assertEquals(rawStoreApp.trackName,storeApp.appName)
        assertEquals(rawStoreApp.artistName,storeApp.developerName)
        assertEquals(rawStoreApp.artworkUrl60,storeApp.appSmallIconUrl)
        assertEquals(rawStoreApp.version,storeApp.appCurrentVersion)
    }

    @Test
    void successfullyGenerateReportForAppInStore(){

        def store   = Store.UnitedKingdom
        def appId   = new AppId(367242295)

        def returnedAverageUserRatingForAllVersions         = 4.5
        def returnedUserRatingCountForAllVersions           = 4264
        def returnedAverageUserRatingForCurrentVersionOnly  = 5.0
        def returnedUserRatingCountForCurrentVersionOnly    = 200

        def returnedRawStoreApp = [
                averageUserRating:returnedAverageUserRatingForAllVersions,
                averageUserRatingForCurrentVersion:returnedAverageUserRatingForCurrentVersionOnly,
                userRatingCount:returnedUserRatingCountForAllVersions,
                userRatingCountForCurrentVersion:returnedUserRatingCountForCurrentVersionOnly,
                trackName:'French Numbers',
                developerName:'Erasmos Inc'
        ] as ApiService.RawStoreApp

        expectFindRawStoreApp(store,appId,returnedRawStoreApp)

        def storeAppReport = service.generateStoreAppReport(store,appId)

        assertSame(appId,storeAppReport.appId)
        assertSame(store,storeAppReport.store)
        assertEquals(returnedRawStoreApp['trackName'],storeAppReport.storeApp.appName)
        assertEquals(returnedRawStoreApp['artistName'],storeAppReport.storeApp.developerName)

        assertEquals(returnedAverageUserRatingForCurrentVersionOnly,storeAppReport.averageUserRatingForCurrentVersionOnly,0.01)
        assertEquals(returnedAverageUserRatingForAllVersions,storeAppReport.averageUserRatingForAllVersions,0.01)
        assertEquals(returnedUserRatingCountForCurrentVersionOnly,storeAppReport.userRatingCountForCurrentVersionOnly)
        assertEquals(returnedUserRatingCountForAllVersions,storeAppReport.userRatingCountForAllVersions)
    }

    @Test
    void failedToGenerateReportForStoreAppAsItsNotAvailableThere(){

        def store       = Store.UnitedKingdom
        def appId       =  new AppId(367242295)

        def returnedRawApp = null
        expectFindRawStoreApp(store,appId,returnedRawApp)

        def appInStoreReport = service.generateStoreAppReport(store,appId)

        assertNull(appInStoreReport)
    }

    @Test
    void successfullyGenerateStoreAppReport(){

        def appId = new AppId(367242295)

        def returnedStores = [Store.UnitedStates,Store.UnitedKingdom, Store.Canada]
        expectGetAllStores(returnedStores)

        def returnedStoreAppReportForUnitedStates   = new StoreAppReport()
        def returnedStoreAppReportForCanada         = new StoreAppReport()

        service.metaClass.generateStoreAppReport {
            Store _store,
            AppId _appId    ->

                switch(_store) {
                    case Store.UnitedStates: return returnedStoreAppReportForUnitedStates
                    case Store.UnitedKingdom: return returnedStoreAppReportForCanada
                    case Store.Canada: return null // Pretending that it's not available.
                    default: fail("Unexpected Store:$_store")

                }

                assertSame(appId,_appId)
        }

        def appReport = service.generateAppReport(appId)

        assertSame(appId,appReport.appId)
        assertEquals(2,appReport.storeAppReports.size())
        assertSame(returnedStoreAppReportForUnitedStates,appReport.storeAppReports[0])
        assertSame(returnedStoreAppReportForCanada,appReport.storeAppReports[1])
    }

    private void expectGetAllStores(final List<Store> returnedStores){

        service.metaClass.getAllStores {
            return returnedStores
        }
    }

    private void expectFindRawStoreApp(final Store expectedStore,final AppId expectedAppId, final RawStoreApp returnedRawStoreApp){

        mockControlForAPIService.demand.findRawStoreApp {
            Store _store,
            AppId _appId ->

                assertEquals(expectedStore,_store)
                assertEquals(expectedAppId,_appId)

                return returnedRawStoreApp
        }
    }

    private void expectFindRawStoreAppsByName(final Store expectedStore,final String expectedName, final int expectedMaxResults, final def returnedRawApps){

        mockControlForAPIService.demand.findRawStoreAppsByName {
            Store _store,
            String _name,
            int _maxResults ->

                assertSame(expectedStore,_store)
                assertSame(expectedName,_name)
                assertEquals(expectedMaxResults,_maxResults)

                return returnedRawApps
        }
    }

    private void assertEquivalent(final RawStoreApp rawStoreApp, final StoreApp storeApp, final Store expectedStore){

        assertEquals(rawStoreApp.trackId as Long,storeApp.appId.id)
        assertEquals(rawStoreApp.trackName,storeApp.appName)
        assertEquals(rawStoreApp.artistName,storeApp.developerName)
        assertEquals(expectedStore, storeApp.store)
    }

    private RawStoreApp generateRawStoreApp(final Long trackId, final String trackName, final String artistName){
        return [trackId:(trackId as String),trackName:trackName,artistName:artistName] as ApiService.RawStoreApp
    }
}
