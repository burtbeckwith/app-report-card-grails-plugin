
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>App Report Card Plugin</title>
</head>
<body>


<center>

    <table width=600>
        <tr>
            <td>
                About the App Report Card Plugin
                <hr size=1/>
            </td>
        </tr>

        <tr>
            <td>
                This plugin aggregates App 'scores' from the <a href="http://www.apple.com/itunes/affiliates/resources/documentation/itunes-store-web-service-search-api.html">iTunes API</a>.
                <br/>
                <br/>
                As you might already know, there are many iTunes Stores, one for each of over 120 countries; a given
                App may be available in some or all of these Stores.

                <br/>
                <br/>

                An App in a particular Store has several interesting scores:

                    <ul>
                        <li>Number of Ratings for all Versions</li>
                        <li>Average Rating for all Versions</li>
                        <li>Number of Ratings for the Current Version</li>
                        <li>Average Rating for for the Current Version</li>

                    </ul>

                Short of seeing the actual sales figures, such numbers may be of some value
                in terms of determining how an App is doing - that is <i>in a single Store</i>; however, to
                get an overall sense of its success would require the tedious switching of
                Stores.

                <br/>
                <br/>
                With this plugin, it becomes much easier.
                <br/>
                <br/>
                Although there are two offered Services - ApiService and AppReportCardService - the main entry point is the Restful interface.
                <br/>
                <br/>
                Here are some examples of what you can do:

                    <ul>
                        <li><a href="/app-report-card/appReportCardRestful/stores" target="_allStores">See all the Stores</a></li>
                        <li><a href="/app-report-card/appReportCardRestful/stores/GB/icon" target="_storeIcon">View the icon for a Store</a></li>
                        <li><a href="/app-report-card/appReportCardRestful/stores/GB/apps/Cats" target="_findApps">Find Apps in a Store</a></li>
                        <li><a href="/app-report-card/appReportCardRestful/stores/GB/apps/542916632" target="_findAppsById">Find a single App by ID in a Store</a></li>
                        <li><a href="/app-report-card/appReportCardRestful/apps/542916632/report/GB" target="_findAppsById">View the report for a single App in a Store</a></li>
                        <li><a href="/app-report-card/appReportCardRestful/apps/542916632/report" target="_findAppsById">View the report for a single App for all Stores</a></li>

                    </ul>

                <br/>
                As you can see in the ApiService, there's declarative caching; however this plugin doesn't have any configuration for this, so it's up to the client.
                Please refer to the <a href="http://grails-plugins.github.io/grails-cache/docs/manual/guide/usage.html#configuration">documentation for the caching plugin</a>.

                <br/>
                <br/>
                <br/>

                Hope that you find this to be useful,
                <br/>
                <a href="http://erasmos.com" target="_erasmos">Sean Rasmussen</a>

                <hr/>

            </td>

        </tr>

    </table>

</center>


</body>
</html>