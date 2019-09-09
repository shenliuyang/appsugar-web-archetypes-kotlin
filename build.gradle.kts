buildscript {}
plugins {
    val kotlinVersion = "1.3.50"
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("net.researchgate.release") version "2.8.1"
    id("org.springframework.boot") version "2.1.8.RELEASE"
    idea
}
apply { plugin("io.spring.dependency-management") }
object Versions {
    const val kotlinVersion = "1.3.50"
    const val coroutineVersion = "1.3.0"
    const val nettyVersion = "4.1.39.Final"
    const val fstVersion = "2.57"
    const val dubboVersion = "2.7.3"
    const val springBootAdminVersion = "2.1.6"
}
extra["kotlin.version"] = Versions.kotlinVersion
extra["netty.version"] = Versions.nettyVersion

val repos = listOf("http://maven.aliyun.com/nexus/content/groups/public", "https://jcenter.bintray.com/")
val dynamicJarNames = ArrayList<String>()
val isMatchAny = { name: String -> dynamicJarNames.contains(name) }
val dynamic: Configuration by configurations.creating
repositories { repos.forEach(::maven) }

dependencies {
    api(kotlin("stdlib-jdk8"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutineVersion}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${Versions.coroutineVersion}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${Versions.coroutineVersion}")
    api("org.springframework.boot:spring-boot-starter-webflux") { exclude("io.netty") }
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.springframework.boot:spring-boot-starter-cache")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    compileOnly("org.springframework.boot:spring-boot-starter-data-redis-reactive") { exclude("io.netty") }
    api("de.codecentric:spring-boot-admin-starter-client:${Versions.springBootAdminVersion}")
    api("org.springframework.boot:spring-boot-devtools")
    api("com.querydsl:querydsl-jpa")
    api("de.ruedigermoeller:fst:${Versions.fstVersion}")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("net.logstash.logback:logstash-logback-encoder:6.1")
    api("org.apache.dubbo:dubbo-spring-boot-starter:${Versions.dubboVersion}")
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
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=enable", "-XXLanguage:+InlineClasses")
    }
}

var mainApplicationClassName = "org.appsugar.archetypes.ApplicationKt"

tasks {
    bootJar { archiveClassifier.set("boot") }
    bootRun { sourceResources(sourceSets["main"]) }
    jar {
        dependsOn(copyToLib, copyToLibDynamic)
        enabled = true
        archiveFileName.set("${project.name}-${archiveVersion.get()}.jar")
        manifest {
            attributes(
                    mapOf("Main-Class" to mainApplicationClassName, "Class-Path" to configurations.runtimeClasspath.get().joinToString(" ") {
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
    test {
        failFast = true
        useJUnitPlatform()
        systemProperties["spring.jpa.hibernate.ddl-auto"] = "create-drop"
    }
}
kapt {
    useBuildCache = true
}


springBoot { buildInfo() }
