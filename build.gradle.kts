plugins {
    id("io.freefair.lombok") version "5.1.0"
    id("net.researchgate.release") version "2.8.0"
    id("org.springframework.boot") version "2.3.0.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
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
    implementation("org.jolokia:jolokia-core")
    implementation("de.codecentric:spring-boot-admin-starter-client:$springBootAdminVersion")
    implementation("org.springframework.boot:spring-boot-devtools")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok:1.18.12")
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
