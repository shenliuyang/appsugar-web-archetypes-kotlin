plugins {
    val lombokVersion : String by System.getProperties()
    val releaseVersion : String by System.getProperties()
    val springBootVersion : String by System.getProperties()
    val dependencyVersion : String by System.getProperties()
    id("io.freefair.lombok") version lombokVersion
    id("net.researchgate.release") version releaseVersion
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version dependencyVersion
    java
    idea
}
val repos = listOf("http://maven.aliyun.com/nexus/content/groups/public", "https://jcenter.bintray.com/")
repositories { repos.forEach(::maven) }
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:Hoxton.SR5")
    }
}
val springBootAdminVersion:String by project
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-config") { exclude(module="spring-cloud-config-client") }
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")
    implementation("mysql:mysql-connector-java")
    implementation("de.codecentric:spring-boot-admin-starter-client:$springBootAdminVersion")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok:1.18.12")
    testImplementation("org.flywaydb:flyway-core")
    testImplementation("org.apache.ant:ant:1.10.1")
    testImplementation("org.dbunit:dbunit:2.5.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test") { exclude("junit") }
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}


/*****config plugin and task*****/
idea {
    module {
        inheritOutputDirs = false
        outputDir = file("$buildDir/classes/kotlin/main/")
        testOutputDir = file("$buildDir/classes/kotlin/test/")
    }
}
tasks {
    bootRun { sourceResources(sourceSets["main"]) }
    test {
        failFast = true
        useJUnitPlatform()
        testLogging { showStandardStreams = true }
    }
}
springBoot { buildInfo() }
