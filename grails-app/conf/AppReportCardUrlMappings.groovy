class AppReportCardUrlMappings {

	static mappings = {


        "/appReportCardRestful/stores/$storeCode/icon" {
            controller  = 'appReportCardRestful'
            action      = 'showStoreIcon'
        }

        "/appReportCardRestful/stores" {
            controller  = 'appReportCardRestful'
            action      = 'showAllStores'
        }

        "/appReportCardRestful/stores/$storeCode/apps/$appId/" {
            controller  = 'appReportCardRestful'
            action      = 'findAppById'
            constraints {
                appId matches: /[0-9]*/
            }
        }


        "/appReportCardRestful/stores/$storeCode/apps/$appName/" {
            controller  = 'appReportCardRestful'
            action      = 'findAppsByName'

        }

        "/appReportCardRestful/apps/$appId/report" {
            controller  = 'appReportCardRestful'
            action      = 'showAppReport'
            constraints {
                appId matches: /[0-9]*/
            }
        }

        "/appReportCardRestful/apps/$appId/report/$storeCode" {
            controller  = 'appReportCardRestful'
            action      = 'showAppReportForStore'
        }

        "/" {
            controller  = 'appReportCard'
            action      = 'index'
        }
    }
}
