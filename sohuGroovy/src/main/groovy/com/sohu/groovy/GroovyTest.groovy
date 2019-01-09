package com.sohu.groovy

import org.gradle.api.Plugin
import org.gradle.api.Project

public class GroovyTest implements Plugin<Project> {

    void apply(Project project) {
        project.task('hello') {
            doLast {
                println "Hello world form the GroovyTest"
            }
        }
    }
}