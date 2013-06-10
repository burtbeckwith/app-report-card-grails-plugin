package com.erasmos.grails.app_report_card_plugin

import grails.plugins.rest.client.RestBuilder
import grails.test.mixin.*
import org.junit.Before
import org.junit.Test

@TestFor(ApiService)
class ApiServiceTests {

    def mockControlRestBuilder
    def mockRestBuilder

    @Before
    void setup(){

        mockControlRestBuilder  = mockFor(RestBuilder)
        mockRestBuilder         = mockControlRestBuilder.createMock()
        service.metaClass.generateRestBuilder() { ->
            return mockRestBuilder
        }
    }

    @Test
    void generateURLForAppTermSearch() {

        def store       = Store.UnitedKingdom
        def term        = 'French Numbers'
        def maxResults  = 64

        def expectedUrl = 'https://itunes.apple.com/search?entity=software&country=GB&term=French+Numbers&limit=64'

        def url = ApiService.generateURLForAppTermSearch(store,term,maxResults)

        assertEquals(expectedUrl,url)
    }

    @Test
    void generateURLForAppLookup() {

        def store   = Store.UnitedKingdom
        def appId   = new AppId(367242295)

        def expectedUrl = 'https://itunes.apple.com/lookup?country=GB&id=367242295'

        def url = ApiService.generateURLForAppLookup(store,appId)

        assertEquals(expectedUrl,url)
    }

    @Test
    void findRawStoreAppsByName(){

        def store       = Store.UnitedKingdom
        def term        = 'French Numbers'
        def maxResults  = 100

        def expectedUrl = 'https://itunes.apple.com/search?entity=software&country=GB&term=French+Numbers&limit=100'

        def returnedResults = new ArrayList<HashMap>()

        returnedResults << ['trackId':'367242295','trackName':'French Numbers']
        returnedResults << ['trackId':'367242294','trackName':'French Numbers (Free)']

        expectGetResultsAsJSON(expectedUrl,returnedResults)


        def rawStoreApps = service.findRawStoreAppsByName(store,term,maxResults)

        assertEquals(2,rawStoreApps.size())

        assertEquals('367242295',returnedResults[0]['trackId'])
        assertEquals('French Numbers',returnedResults[0]['trackName'])

        assertEquals('367242294',returnedResults[1]['trackId'])
        assertEquals('French Numbers (Free)',returnedResults[1]['trackName'])

    }

    @Test
    void findRawStoreAppWhenMultipleMatchesExist(){

        def store   = Store.UnitedStates
        def appId   = new AppId(367242295)

        def returnedResults = new ArrayList<HashMap>()

        def firstResult     =  ['trackId':'367242295','trackName':'French Numbers']
        def secondResult    =  ['trackId':'367242294','trackName':'French Numbers (Free)']

        returnedResults << firstResult
        returnedResults << secondResult

        def expectedUrl = 'https://itunes.apple.com/lookup?country=US&id=367242295'

        expectGetResultsAsJSON(expectedUrl,returnedResults)

        def rawStoreApp = service.findRawStoreApp(store,appId)

        assertEquals(firstResult,rawStoreApp)

    }

    @Test
    void findRawStoreAppWhenItDoesNotExist(){

        def store   = Store.UnitedStates
        def appId   = new AppId(367242295)

        def returnedResults = new ArrayList<HashMap>()


        def expectedUrl = 'https://itunes.apple.com/lookup?country=US&id=367242295'

        expectGetResultsAsJSON(expectedUrl,returnedResults)

        def rawStoreApp = service.findRawStoreApp(store,appId)

        assertNull(rawStoreApp)
    }

    @Test
    void findRawStoreAppWhenASingleMatchExists(){

        def store   = Store.UnitedStates
        def appId   = new AppId(367242295)

        def returnedResults = new ArrayList<HashMap>()

        def onlyResult     =  ['trackId':'367242295','trackName':'French Numbers']

        returnedResults << onlyResult

        def expectedUrl = 'https://itunes.apple.com/lookup?country=US&id=367242295'

        expectGetResultsAsJSON(expectedUrl,returnedResults)

        def rawStoreApp = service.findRawStoreApp(store,appId)

        assertEquals(onlyResult,rawStoreApp)

    }

    @Test
    void getResultsAsJSONWhenNothingReturned() {

        def url = "http://itunes.apple.com/anything"

        def returnedResults = null
        expectGetOnRestBuilder(url,returnedResults)

        def resultsAsJSON  = service.getResultsAsJSON(url)

        assertTrue(resultsAsJSON.empty)
    }

    @Test
    void getResultsAsJSONWhenNoJSONReturned() {

        def url = "http://itunes.apple.com/anything"

        def returnedResults = [:]
        expectGetOnRestBuilder(url,returnedResults)

        def resultsAsJSON  = service.getResultsAsJSON(url)

        assertTrue(resultsAsJSON.empty)
    }

    @Test
    void getResultsAsJSONWhenJSONResultsReturned() {

        def url = "http://itunes.apple.com/anything"

        def firstResult     =  ['trackId':'367242295','trackName':'French Numbers']
        def secondResult    =  ['trackId':'367242294','trackName':'French Numbers (Free)']


        def returnedResults = [json:[results:[firstResult,secondResult]]]
        expectGetOnRestBuilder(url,returnedResults)

        def resultsAsJSON  = service.getResultsAsJSON(url)

        assertEquals([firstResult,secondResult],resultsAsJSON)


    }


    @Test
    void urlEncodeStore(){

        def store = Store.UnitedStates

        def urlEncodedStore = service.urlEncode(store)

        assertEquals('US',urlEncodedStore)
    }

    @Test
    void urlEncodeAppId(){

        def appId = new AppId(367242295)

        def urlEncodedAppId = service.urlEncode(appId)

        assertEquals('367242295',urlEncodedAppId)
    }


    private void expectGetOnRestBuilder(final String expectedUrl, final def returnedResults){

        mockControlRestBuilder.demand.get {
            String _url ->
                assertSame(expectedUrl,_url)
                return returnedResults
        }
    }


    private void expectGetResultsAsJSON(final String expectedUrl, final List<Map> returnedResults){

        service.metaClass.getResultsAsJSON {
            String _url ->
                assertEquals(expectedUrl,_url)
                return returnedResults
        }
    }
}
