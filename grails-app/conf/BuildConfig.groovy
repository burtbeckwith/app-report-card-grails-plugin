grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        compile("org.codehaus.gpars:gpars:1.0.0")
    }

    plugins {
        build ':release:2.2.1', {
            export = false
        }

        compile(":rest-client-builder:1.0.3")
    }
}
