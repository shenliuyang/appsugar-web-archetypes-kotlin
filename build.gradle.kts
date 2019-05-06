import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

buildscript {
    extra["kotlin.version"] = "1.3.31"
}

plugins {
    val kotlinVersion = "1.3.31"
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("net.researchgate.release") version "2.8.0"
    id("org.springframework.boot") version "2.1.4.RELEASE"
    idea
}
apply { plugin("io.spring.dependency-management") }
val repos = listOf("http://maven.aliyun.com/nexus/content/groups/public", "https://jcenter.bintray.com/")
val dynamicJarNames = ArrayList<String>()
val isMatchAny = { name: String -> dynamicJarNames.contains(name) }
val dynamic: Configuration by configurations.creating

object Versions {
    const val coroutineVersion = "1.2.1"
}
repositories { repos.forEach(::maven) }

dependencies {
    api(kotlin("stdlib-jdk8"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutineVersion}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${Versions.coroutineVersion}")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.springframework.boot:spring-boot-starter-cache")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-devtools")
    api("com.querydsl:querydsl-jpa")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    api(dynamic("com.h2database:h2")!!)
    api(dynamic("mysql:mysql-connector-java")!!)
    kapt("com.querydsl:querydsl-apt:4.2.1:jpa")
    kapt("javax.persistence:javax.persistence-api")
    testApi("com.squareup.retrofit2:converter-jackson:2.5.0")
    testApi("com.squareup.retrofit2:retrofit:2.5.0")
    testApi("org.apache.ant:ant:1.10.1")
    testApi("org.dbunit:dbunit:2.5.4")
    testApi("org.springframework.boot:spring-boot-starter-test") { exclude("junit") }
    testApi("org.junit.jupiter:junit-jupiter-api")
    testRuntime("org.junit.jupiter:junit-jupiter-engine")
    dynamic.forEach { dynamicJarNames.add(it.name) }
}


/*****config plugin and task*****/
idea {
    module {
        inheritOutputDirs = false
        outputDir = file("$buildDir/classes/kotlin/main/")
        testOutputDir = file("$buildDir/classes/kotlin/test/")
    }
}
allOpen {
    val classNameList = listOf("javax.persistence.Entity", "javax.persistence.MappedSuperclass", "javax.persistence.Embeddable")
    classNameList.forEach { annotation(it) }
}
val copyToLib by tasks.creating(Copy::class) {
    into("$buildDir/libs/lib")
    from(configurations.runtimeClasspath)
    exclude("*spring-boot-devtools*.jar")
    exclude { isMatchAny(it.file.name) }
    outputs.upToDateWhen { true }
}
val copyToLibDynamic by tasks.creating(Copy::class) {
    into("$buildDir/libs/lib-dynamic")
    from(configurations.runtimeClasspath)
    include { isMatchAny(it.file.name) }
    outputs.upToDateWhen { true }
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

var mainApplicationClassName = "org.appsugar.archetypes.ApplicationKt"
tasks {
    "bootJar"(BootJar::class) { archiveClassifier.set("boot") }
    "bootRun"(BootRun::class) { sourceResources(sourceSets["main"]) }
    "jar"(Jar::class) {
        dependsOn(copyToLib, copyToLibDynamic)
        enabled = true
        archiveFileName.set("${project.name}-${archiveVersion.get()}.jar")
        manifest {
            attributes(
                    mapOf("Main-Class" to mainApplicationClassName, "Class-Path" to configurations.runtime.get().joinToString(" ") {
                        if (isMatchAny(it.name)) "lib-dynamic/${it.name}" else "lib/${it.name}"
                    }))
        }
        doLast {
            copy {
                from("$buildDir/libs/${archiveFileName.get()}")
                into("$buildDir/libs/")
                rename(archiveFileName.get(), "app.jar")
            }
        }
    }
    "test"(Test::class) {
        failFast = true
        useJUnitPlatform()
        systemProperties["spring.jpa.hibernate.ddl-auto"] = "create-drop"
    }
}
kapt {
    useBuildCache = true
}
springBoot { buildInfo() }
