import org.springframework.context.ApplicationContext
import com.erasmos.grails.app_report_card_plugin.AppReportCardRestfulController

class AppReportCardGrailsPlugin {
    def version = "1.0"
    def grailsVersion = "2.0 > *"

    def title = "App Report Card Plugin"
    def author = "Sean Rasmussen"
    def authorEmail = "sean@erasmos.com"
    def description = '''Aggregates various 'scores' for a given iTunes app, across all stores; offers a Restful interface as well as Services.'''
    def documentation = "http://grails.org/plugin/app-report-card"

    def license = "APACHE"
    def organization = [name: "Erasmos Inc", url: "http://www.erasmos.com/"]
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/erasmos/app-report-card-grails-plugin/issues']
    def scm = [url: 'https://github.com/erasmos/app-report-card-grails-plugin']

    def doWithApplicationContext = { applicationContext ->
        configureRestfulController(applicationContext)
    }

    /**
     * @param applicationContext
     */
    private void configureRestfulController(final ApplicationContext applicationContext){
        def candidates = applicationContext.getBeansOfType(AppReportCardRestfulController)
        AppReportCardRestfulController restfulController = candidates.get(AppReportCardRestfulController.canonicalName)
        restfulController.registerJSONMarshallers()
    }
}
