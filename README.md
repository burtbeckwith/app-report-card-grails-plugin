The App Report Card Grails Plugin 
=============================

This plugin aggregates App 'scores' from the <a href="http://www.apple.com/itunes/affiliates/resources/documentation/itunes-store-web-service-search-api.html">iTunes API</a>.
                 
As you might already know, there are many iTunes Stores, one for each of over 120 countries; a given App may be available in some or all of these Stores.


An App in a particular Store has several interesting scores:

* Number of Ratings for all Versions
* Average Rating for all Versions
* Number of Ratings for the Current Version
* Average Rating for for the Current Version

Short of seeing the actual sales figures, such numbers may be of some value
in terms of determining how an App is doing - that is <i>in a single Store</i>; however, to
get an overall sense of its success would require the tedious switching of
Stores.

With this plugin, it becomes much easier.
                 
Although there are two offered Services - ApiService and AppReportCardService - the main entry point is the Restful interface.
                
Here are some examples of what you can do:

* **See all the Stores**: /app-report-card/appReportCardRestful/stores
* **View the icon for a Store**: /app-report-card/appReportCardRestful/stores/GB/icon
* **Find a single App by ID in a Store**: /app-report-card/appReportCardRestful/stores/GB/apps/Cats
* **View the report for a single App for all Stores**: /app-report-card/appReportCardRestful/apps/542916632/  

As you can see in the ApiService, there's declarative caching; however this plugin doesn't have any configuration for this, so it's up to the client.
                
Please refer to the <a href="http://grails-plugins.github.io/grails-cache/docs/manual/guide/usage.html#configuration">documentation for the caching plugin</a>.

Hope that you find this to be useful,

<a href="http://erasmos.com" target="_erasmos">Sean Rasmussen</a>
 
