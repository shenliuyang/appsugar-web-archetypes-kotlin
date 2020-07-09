plugins {
    val lombokVersion: String by System.getProperties()
    val releaseVersion: String by System.getProperties()
    val springBootVersion: String by System.getProperties()
    val dependencyVersion: String by System.getProperties()
    val dockerVersion: String by System.getProperties()
    id("io.freefair.lombok") version lombokVersion
    id("net.researchgate.release") version releaseVersion
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version dependencyVersion
    id("com.bmuschko.docker-remote-api") version dockerVersion
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
val springBootAdminVersion: String by project
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-config") { exclude(module = "spring-cloud-config-client") }
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.querydsl:querydsl-jpa")
    implementation("com.h2database:h2")
    implementation("mysql:mysql-connector-java")
    implementation("de.codecentric:spring-boot-admin-starter-client:$springBootAdminVersion")
    implementation("org.jolokia:jolokia-core")
    implementation("org.apache.commons:commons-lang3")
    //for huge excel file read
    implementation("org.apache.poi:poi:4.1.2")
    implementation("org.apache.poi:poi-ooxml:4.1.2")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("com.querydsl:querydsl-apt:4.3.1:jpa")
    annotationProcessor("javax.persistence:javax.persistence-api")
    testImplementation("org.flywaydb:flyway-core")
    testImplementation("org.apache.ant:ant:1.10.1")
    testImplementation("org.dbunit:dbunit:2.5.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }
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
val layerList = listOf("dependencies", "spring-boot-loader", "snapshot-dependencies", "frequency-change-dependencies", "application")
//make docker file
val baseImage: String by System.getProperties()
val workDir: String by System.getProperties()
val createDockerfile by tasks.creating(com.bmuschko.gradle.docker.tasks.image.Dockerfile::class) {
    from("$baseImage as builder")
    workingDir(workDir)
    copyFile(tasks.bootJar.get().archiveFileName.get(), "app.jar")
    runCommand("java -Djarmode=layertools -jar app.jar extract")
    from(baseImage)
    workingDir(workDir)
    layerList.forEach { copyFile("--from=builder application/$it/", "./") }
    val logEnvironment = mapOf("logging.config" to "classpath:logback-spring.xml", "logging.level.ROOT" to "WARN", "logging.file" to "/logs/${project.name}/app.log")
    val springBootAdminEnvironment = mapOf("spring.boot.admin.client.url" to "http://springbootAdmin", "spring.boot.admin.client.username" to "admin", "spring.boot.admin.client.password" to "admin")
    val springServerEnvironment = mapOf("spring.application.name" to project.name, "server.port" to "80")
    (logEnvironment + springBootAdminEnvironment + springServerEnvironment).forEach { (k, v) -> environmentVariable(k, v) }
    entryPoint("java", "org.springframework.boot.loader.JarLauncher")
}
//make docker image
val imageName: String by System.getProperties()
val imageNames = imageName.split(",")
val buildImage = tasks.create("buildImage", com.bmuschko.gradle.docker.tasks.image.DockerBuildImage::class) {
    dependsOn(createDockerfile, tasks.build)
    inputDir.set(file("$buildDir/libs/"))
    doFirst { createDockerfile.destFile.get().asFile.copyTo(File(inputDir.get().asFile, "Dockerfile"), true) }
    images.addAll(imageNames)
}
//create redis container for integration test
val createRedisContainer by tasks.creating(com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer::class) {
    targetImageId("redis:6.0.5-alpine3.12")
    hostConfig.portBindings.set(listOf("8080:8080"))
    hostConfig.autoRemove.set(true)
}
val startRedisContainer by tasks.creating(com.bmuschko.gradle.docker.tasks.container.DockerStartContainer::class) {
    dependsOn(createRedisContainer)
    targetContainerId(createRedisContainer.containerId)
}

val stopRedisContainer by tasks.creating(com.bmuschko.gradle.docker.tasks.container.DockerStopContainer::class) {
    targetContainerId(createRedisContainer.containerId)
}


tasks {
    bootRun {
        sourceResources(sourceSets["main"])
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
            layerOrder = layerList
        }
    }
    test {
        //TODO waiting for windows10 2004, wsl2 docker
        //dependsOn(startRedisContainer)
        //finalizedBy(stopRedisContainer)
        failFast = true
        useJUnitPlatform()
        testLogging { showStandardStreams = true }
    }
}
springBoot { buildInfo() }