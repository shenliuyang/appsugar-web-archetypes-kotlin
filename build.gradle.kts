plugins {
    val sysProps = System.getProperties()
    val lombokVersion: String by sysProps
    val releaseVersion: String by sysProps
    val springBootVersion: String by sysProps
    val dependencyVersion: String by sysProps
    id("io.freefair.lombok") version lombokVersion
    id("net.researchgate.release") version releaseVersion
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version dependencyVersion
    java
    idea
}
val testSystemProps = mutableMapOf<String, Any>()
val repos = listOf("https://maven.aliyun.com/nexus/content/groups/public", "https://jcenter.bintray.com/")
val springCloudVersion: String by project
val springBootAdminVersion: String by project
val springfoxVersion: String by project
val entityGraphVersion: String by project

repositories { repos.forEach { maven(it) } }
dependencyManagement { imports {
    mavenBom("de.codecentric:spring-boot-admin-dependencies:$springBootAdminVersion")
} }
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.cosium.spring.data:spring-data-jpa-entity-graph:$entityGraphVersion")
    implementation("com.querydsl:querydsl-jpa")
    implementation("com.h2database:h2")
    implementation("mysql:mysql-connector-java")
    implementation("de.codecentric:spring-boot-admin-starter-client")
    implementation("org.jolokia:jolokia-core")
    implementation("org.apache.commons:commons-lang3")
    implementation("com.lmax:disruptor:3.4.4")
    implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("net.logstash.logback:logstash-logback-encoder:6.6")


    compileOnly("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework:spring-context-indexer")
    annotationProcessor("com.querydsl:querydsl-apt:4.4.0:jpa")
    annotationProcessor("javax.annotation:javax.annotation-api")
    annotationProcessor("javax.persistence:javax.persistence-api")
    annotationProcessor("org.hibernate:hibernate-jpamodelgen")
    annotationProcessor("com.cosium.spring.data:spring-data-jpa-entity-graph-generator:$entityGraphVersion")

    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test") { exclude("org.junit.vintage", "junit-vintage-engine") }
    testImplementation("org.apache.ant:ant:1.10.1")
    testImplementation("org.dbunit:dbunit:2.5.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
/*****config plugin and task*****/
idea {
    module {
        inheritOutputDirs = false
        outputDir = file("$buildDir/classes/java/main/")
        testOutputDir = file("$buildDir/classes/java/test/")
    }
}


tasks.withType(JavaCompile::class) { options.encoding = "UTF-8" }

tasks {
    bootRun {
        sourceResources(sourceSets["main"])
        systemProperty("spring.datasource.hikari.jdbcUrl","jdbc:h2:./build/appsugar-integration-test")
        systemProperty("logging.config", "classpath:logback-console.xml")
        systemProperty("logging.level.ROOT", "INFO")
    }
    bootJar {
        layered {
            application {
                intoLayer("spring-boot-loader") {
                    include("org/springframework/boot/loader/**")
                }
                intoLayer("application")
            }
            dependencies {
                intoLayer("snapshot-dependencies") {
                    include("*:*:*SNAPSHOT")
                }
                intoLayer("frequency-change-dependencies") {
                    include("com.h2database:h2:*")
                    include("mysql:mysql-connector-java:*")
                }
                intoLayer("dependencies")
            }
            layerOrder = listOf("dependencies", "spring-boot-loader", "frequency-change-dependencies","snapshot-dependencies", "application")
        }
    }
    test {
        failFast = true
        useJUnitPlatform()
        testLogging { showStandardStreams = true }
    }
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
springBoot { buildInfo() }
