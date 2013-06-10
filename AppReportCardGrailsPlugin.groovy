import com.erasmos.grails.app_report_card_plugin.AppReportCardRestfulController
import org.springframework.context.ApplicationContext

class AppReportCardGrailsPlugin {
    // the plugin version
    def version = "1.0"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.2 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = "App Report Card Plugin" // Headline display appName of the plugin
    def author = "Sean Rasmussen (Erasmos Inc)"
    def authorEmail = "sean@erasmos.com"
    def description = '''
            Aggregates various 'scores' for a given iTunes app, across all stores; offers a Restful interface
            as well as Services.'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/app-report-card"


    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        configureRestfulController(applicationContext)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }

    /**
     * @param applicationContext
     */
    private void configureRestfulController(final ApplicationContext applicationContext){
        def candidates = applicationContext.getBeansOfType(AppReportCardRestfulController)
        def restfulController = (AppReportCardRestfulController)candidates.get(AppReportCardRestfulController.canonicalName)
        restfulController.registerJSONMarshallers()
    }
}