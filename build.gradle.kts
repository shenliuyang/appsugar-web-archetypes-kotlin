plugins {
    val kotlinVersion: String by System.getProperties()
    val springBootVersion: String by System.getProperties()
    val releasePluginVersion: String by System.getProperties()
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("net.researchgate.release") version releasePluginVersion
    id("org.springframework.boot") version springBootVersion
    idea
}
apply { plugin("io.spring.dependency-management") }

val kotlinVersion: String by System.getProperties()
val coroutineVersion: String by project
val springBootAdminVersion: String by project
val exposedVersion: String by project

extra["kotlin.version"] = kotlinVersion

val repos = listOf("https://maven.aliyun.com/nexus/content/groups/public", "https://jcenter.bintray.com/", "https://repo.spring.io/milestone")
val dynamicJarNames = ArrayList<String>()
val isMatchAny = { name: String -> dynamicJarNames.contains(name) }
repositories {
    mavenLocal()
    repos.forEach(::maven)
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutineVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutineVersion")
    api("org.springframework.boot:spring-boot-starter-webflux")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.springframework.boot:spring-boot-starter-cache")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("de.codecentric:spring-boot-admin-starter-client:$springBootAdminVersion")
    api("org.springframework.boot:spring-boot-devtools")
    api("com.querydsl:querydsl-jpa")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    //api("org.jetbrains.exposed:exposed-spring-boot-starter:$exposedVersion")
    apiDynamic("com.h2database:h2")
    api("mysql:mysql-connector-java")
    kapt("com.querydsl:querydsl-apt:4.2.1:jpa")
    kapt("javax.persistence:javax.persistence-api")
    testApi("org.apache.ant:ant:1.10.1")
    testApi("org.dbunit:dbunit:2.5.4")
    testApi("org.springframework.boot:spring-boot-starter-test") { exclude("junit") }
    testApi("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testApi("org.bouncycastle:bcprov-jdk15on:1.65")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    testApi("org.apache.commons:commons-lang3:3.10")


}
fun DependencyHandlerScope.apiDynamic(notation: String) = add(configurations.api.name, notation)!!.apply { dynamicJarNames.add("$name-$version.jar") }
/*****config plugin and task*****/
idea {
    module {
        inheritOutputDirs = false
        outputDir = file("$buildDir/classes/kotlin/main/")
        testOutputDir = file("$buildDir/classes/kotlin/test/")
    }
}
val classNameList = listOf("javax.persistence.Entity", "javax.persistence.MappedSuperclass", "javax.persistence.Embeddable")
allOpen { classNameList.forEach { annotation(it) } }
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=enable")
    }
}

var mainApplicationClassName = "detected by spring boot plugin"

tasks {
    bootJar { doLast { mainApplicationClassName = mainClassName } }
    bootRun { sourceResources(sourceSets["main"]) }
    jar {
        dependsOn(bootJar, copyToLib, copyToLibDynamic)
        enabled = true
        archiveFileName.set("app.jar")
        doFirst { manifest { attributes(mapOf("Main-Class" to mainApplicationClassName, "Class-Path" to configurations.runtimeClasspath.get().joinToString(" ") { if (isMatchAny(it.name)) "lib-dynamic/${it.name}" else "lib/${it.name}" })) } }
    }
    test {
        failFast = true
        useJUnitPlatform()
        systemProperties["spring.jpa.hibernate.ddl-auto"] = "update"
        testLogging { showStandardStreams = true }
    }
}
kapt { useBuildCache = true }
springBoot { buildInfo() }
