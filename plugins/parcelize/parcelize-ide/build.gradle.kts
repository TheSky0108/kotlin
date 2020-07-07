description = "IDE support for the Parcelize compiler plugin"

plugins {
    kotlin("jvm")
    id("jps-compatible")
}

dependencies {
    compile(project(":compiler:util"))
    compile(project(":compiler:plugin-api"))
    compile(project(":compiler:frontend"))
    compile(project(":compiler:frontend.java"))
    compile(project(":compiler:light-classes"))
    compile(project(":idea"))
    compile(project(":plugins:parcelize:parcelize-compiler"))
    compile(project(":plugins:parcelize:parcelize-runtime"))

    compile(intellijDep())
    compile(intellijPluginDep("gradle"))

    Platform[191].orLower {
        compileOnly(intellijDep()) { includeJars("java-api", "java-impl") }
    }

    Platform[192].orHigher {
        compileOnly(intellijPluginDep("java")) { includeJars("java-api", "java-impl") }
    }

    testCompile(projectTests(":idea"))

    testRuntime(project(":allopen-ide-plugin")) { isTransitive = false }
    testRuntime(project(":kotlin-allopen-compiler-plugin"))
    testRuntime(project(":noarg-ide-plugin")) { isTransitive = false }
    testRuntime(project(":kotlin-noarg-compiler-plugin"))
    testRuntime(project(":plugins:annotation-based-compiler-plugins-ide-support")) { isTransitive = false }
    testRuntime(project(":kotlin-scripting-idea")) { isTransitive = false }
    testRuntime(project(":kotlin-scripting-compiler-impl"))
    testRuntime(project(":sam-with-receiver-ide-plugin")) { isTransitive = false }
    testRuntime(project(":kotlinx-serialization-compiler-plugin"))
    testRuntime(project(":kotlinx-serialization-ide-plugin")) { isTransitive = false }

    testRuntime(toolsJar())

    Platform[192].orHigher {
        testRuntimeOnly(intellijPluginDep("java"))
    }
}

sourceSets {
    "main" { projectDefault() }
    "test" { projectDefault() }
}

runtimeJar()
javadocJar()
sourcesJar()

testsJar {}

projectTest(parallel = true) {
    dependsOn(":dist")
    workingDir = rootDir
    useAndroidSdk()
    useAndroidJar()
}

apply(from = "$rootDir/gradle/kotlinPluginPublication.gradle.kts")
