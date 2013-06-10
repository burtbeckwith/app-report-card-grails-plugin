package com.erasmos.grails.app_report_card_plugin

import grails.plugins.rest.client.RestBuilder
import org.springframework.cache.annotation.Cacheable


/**
 * Ref: http://www.apple.com/itunes/affiliates/resources/documentation/itunes-store-web-service-search-api.html
 *
 * A RawApp exists in store and carries much info
 */
class ApiService {

    static def transactional = false

    private static String BaseUrl = 'https://itunes.apple.com'
    private static int DefaultMaxSearchResults = 20
    private static String BaseUrlForSearch = "${BaseUrl}/search?entity=software&"
    private static String BaseUrlForLookup = "${BaseUrl}/lookup?"

    /**
     *
     * @param store
     * @param term
     * @return
     */
    @Cacheable('ApiService_FindRawStoreAppsByName')
    public List<RawStoreApp> findRawStoreAppsByName(final Store store,final String term, final int maxResults = DefaultMaxSearchResults){

        assert store != null, 'Missing Store'
        assert term != null, 'Missing Term'

        if(log.isDebugEnabled()) log.debug("Searching Store ($store) with term: $term, limit of $maxResults matches ...")

        return getResultsAsJSON(generateURLForAppTermSearch(store,term,maxResults)).collect{convertToRawAppInStore(it)}
    }

    /**
     *
     * @param store
     * @param appId
     * @return
     */
    @Cacheable('ApiService_FindRawStoreApp')
    public RawStoreApp findRawStoreApp(final Store store, final AppId appId){

        assert store    != null, 'Missing Store'
        assert appId    != null, 'Missing AppId'

        if(log.isDebugEnabled()) log.debug("Finding the App ($appId) in Store ($store) ...")

        def results = getResultsAsJSON(generateURLForAppLookup(store,appId))
        if(results.empty){
            if(log.isWarnEnabled()) log.warn("... no matches. Perhaps it's not available in this store.")
            return null
        }

        def numberOfMatches = results.size()

        if(log.isDebugEnabled()) log.debug("... # of matches: $numberOfMatches")

        if(numberOfMatches>1){
            if(log.isWarnEnabled()) log.warn("Weird ... there were multiple matches ($numberOfMatches) for a supposedly unique id ($appId). We might as well take the first.")
        }

        def rawStoreApp = convertToRawAppInStore(results.first())

        if(log.isDebugEnabled()) log.debug("... found it :)")
        if(log.isTraceEnabled()) log.trace(rawStoreApp)

        return rawStoreApp

    }

    /**
     * @param url
     * @return Never null
     */
    private List<Map> getResultsAsJSON(final String url){

        assert url != null, "Missing url"

        if(log.isDebugEnabled()) log.debug("Attempting to call API with: $url ....")

        def response = generateRestBuilder().get(url)
        if(response==null){
            if(log.isWarnEnabled()) log.warn("... there was no response at all.")
            return []
        }

        def responseAsJSON = response.json
        if(responseAsJSON==null){
            if(log.isWarnEnabled()) log.warn("... there was no JSON response at all; instead we had: $responseAsJSON")
            return []
        }

        def resultsAsJSON = responseAsJSON.results
        if(!resultsAsJSON){
            if(log.isDebugEnabled()) log.debug("... we received no results; the raw JSON returned was: $responseAsJSON")
            return []
        }

        if(log.isDebugEnabled()) log.debug("... we received ${resultsAsJSON.size()}")

        assert resultsAsJSON != null, "We guarantee to return non-null results."

        return  resultsAsJSON
    }

    /**
     *
     * @param store
     * @param term
     * @param maxResults
     * @return
     */
    private static String generateURLForAppTermSearch(final Store store, final String term, final int maxResults){

        assert store != null, 'Missing Store'
        assert term != null, 'Missing Term'

        return "${BaseUrlForSearch}country=${urlEncode(store)}&term=${urlEncode(term)}&limit=$maxResults"
    }


    /**
     * @param storeCode
     * @param appId
     * @return
     */
    private static String generateURLForAppLookup(final Store store, final AppId appId){

        assert store != null, 'Missing Store'
        assert appId != null, 'Missing AppId'

        return "${BaseUrlForLookup}country=${urlEncode(store)}&id=${urlEncode(appId)}"
    }

    /**
     *
     *
     * @param store
     * @return
     */
    private static String urlEncode(final Store store){

        assert store != null, 'Missing Store'
        assert store.code != null, 'Missing Store Code'

        return urlEncode(store.code)
    }

    /**
     *
     * @param appId
     * @return
     */
    private static String urlEncode(final AppId appId){

        assert appId != null, 'Missing AppId'
        assert appId.id != null, 'Missing AppId id'

        return urlEncode(appId.id as String)
    }

    private static String urlEncode(final String original){
        return URLEncoder.encode(original,'UTF-8')
    }


    /**
     *
     * RawStoreApp is just a copy of the original Map (JSON)
     *
     * @param appAsJSON
     * @return
     */
    private RawStoreApp convertToRawAppInStore(final Map appAsJSON){

        assert appAsJSON != null, 'Missing appAsJSON'

        def rawAppInStore = new RawStoreApp()

        rawAppInStore.putAll(appAsJSON)

        return rawAppInStore
    }

    /**
     * @return
     */
    private RestBuilder generateRestBuilder(){
        return new RestBuilder()
    }

    public static class RawStoreApp extends HashMap {

    }
}
