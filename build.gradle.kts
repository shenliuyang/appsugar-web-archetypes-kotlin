import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

buildscript {
    val repos by extra { listOf("http://maven.aliyun.com/nexus/content/groups/public", "https://jcenter.bintray.com/") }
    extra["kotlin.version"] = "1.3.30"
    repositories {
        for (u in repos) {
            maven(u)
        }
    }
}

plugins {
    val kotlinVersion = "1.3.30"
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    idea
    id("net.researchgate.release") version "2.8.0"
    id("org.springframework.boot") version "2.1.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
}
val repos: List<String> by extra
val dynamicJarNames = ArrayList<String>()
val isMatchAny = { name: String -> dynamicJarNames.contains(name) }
val dynamic: Configuration by configurations.creating
val coroutineVersion = "1.1.1"
repositories {
    for (u in repos) {
        maven(u)
    }
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.boot:spring-boot-starter-security")
    compile("org.springframework.boot:spring-boot-starter-actuator")
    compile("org.springframework.boot:spring-boot-starter-cache")
    compile("org.springframework.boot:spring-boot-starter-data-jpa")
    compile("org.springframework.boot:spring-boot-devtools")
    compile("com.querydsl:querydsl-jpa")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin")
    compile(dynamic("com.h2database:h2")!!)
    compile(dynamic("mysql:mysql-connector-java")!!)
    kapt("com.querydsl:querydsl-apt:4.2.1:jpa")
    kapt("javax.persistence:javax.persistence-api")
    testCompile("com.squareup.retrofit2:converter-jackson:2.5.0")
    testCompile("com.squareup.retrofit2:retrofit:2.5.0")
    testCompile("org.apache.ant:ant:1.10.1")
    testCompile("org.dbunit:dbunit:2.5.4")
    testCompile("org.springframework.boot:spring-boot-starter-test") { exclude("junit") }
    testCompile("org.junit.jupiter:junit-jupiter-api")
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
    from(configurations.runtime)
    exclude("*spring-boot-devtools*.jar")
    exclude { isMatchAny(it.file.name) }
    outputs.upToDateWhen { true }
}
val copyToLibDynamic by tasks.creating(Copy::class) {
    into("$buildDir/libs/lib-dynamic")
    from(configurations.runtime)
    include { isMatchAny(it.file.name) }
    outputs.upToDateWhen { true }
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

var mainApplicationClassName = "org.appsugar.archetypes.ApplicationKt"
tasks {
    "bootJar"(BootJar::class) { archiveClassifier.set("boot") }
    "bootRun"(BootRun::class) { sourceResources(sourceSets["main"]) }
    "jar"(Jar::class) {
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
        systemProperties["refreshDb"] = true
        systemProperties["spring.jpa.hibernate.ddl-auto"] = "create-drop"
    }
}
kapt {
    useBuildCache = true
}
springBoot { buildInfo() }
