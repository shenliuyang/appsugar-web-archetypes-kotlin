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
val testSystemProps = mutableMapOf<String, Any>()
val repos = listOf("http://maven.aliyun.com/nexus/content/groups/public", "https://jcenter.bintray.com/")
val springCloudVersion: String by project
repositories { repos.forEach(::maven) }
dependencyManagement { imports { mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion") } }
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
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("com.querydsl:querydsl-apt:4.3.1:jpa")
    annotationProcessor("javax.persistence:javax.persistence-api")
    testImplementation("org.flywaydb:flyway-core")
    testImplementation("org.springframework.boot:spring-boot-starter-test") { exclude("org.junit.vintage", "junit-vintage-engine") }
    testImplementation("org.springframework.boot:spring-boot-starter-data-redis")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    testImplementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    testImplementation("org.springframework.kafka:spring-kafka")

    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("it.ozimov:embedded-redis:0.7.3") {
        exclude("commons-logging")
        exclude("org.slf4j")
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
val layerList = mutableListOf<String>()
fun org.springframework.boot.gradle.tasks.bundling.LayeredSpec.IntoLayersSpec.intoLayerEx(layer: String, action: Action<org.springframework.boot.gradle.tasks.bundling.LayeredSpec.IntoLayerSpec>) {
    layerList.add(layer);this.intoLayer(layer, action)
}

fun org.springframework.boot.gradle.tasks.bundling.LayeredSpec.IntoLayersSpec.intoLayerEx(layer: String) {
    layerList.add(layer);this.intoLayer(layer)
}
//make docker file
val baseImage: String by System.getProperties()
val workDir: String by System.getProperties()
val createDockerfile: Task by tasks.creating {
    dependsOn(tasks.bootJar)
    doLast {
        val sb = StringBuilder().apply {
            appendln("FROM $baseImage as builder")
            appendln("WORKDIR $workDir")
            appendln("RUN java -Djarmode=layertools -jar ${tasks.bootJar.get().archiveFileName.get()} extract")
            appendln("FROM $baseImage")
            appendln("WORKDIR $workDir")
            layerList.forEach { appendln("COPY --from=builder application/$it/ ./") }
            val logEnvironment = mapOf("logging.config" to "classpath:logback-spring.xml", "logging.level.ROOT" to "WARN", "logging.file" to "/logs/${project.name}/app.log")
            val springBootAdminEnvironment = mapOf("spring.boot.admin.client.url" to "http://springbootAdmin", "spring.boot.admin.client.username" to "admin", "spring.boot.admin.client.password" to "admin")
            val springServerEnvironment = mapOf("spring.application.name" to project.name, "server.port" to "80")
            (logEnvironment + springBootAdminEnvironment + springServerEnvironment).forEach { (k, v) -> appendln("ENV $k=$v") }
            appendln("ENTRYPOINT [\"java\",\"org.springframework.boot.loader.JarLauncher\"]")
        }
        File("$buildDir/libs/", "Dockerfile").writeText(sb.toString())
    }
}

val integrationTestRedisPort = 6352
testSystemProps["integration.test.redis.port"] = integrationTestRedisPort
val imageNameOfRedis = "redis:6.0.1"
val integrationTestImageNameList = listOf(imageNameOfRedis, "nginx:latest")
val portBindings = mapOf(imageNameOfRedis to listOf("$integrationTestRedisPort:6379"))
val localImageTags = mutableSetOf<String>()
val configContainer = fun(imageName: String, create: com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer) = create.apply {
    portBindings[imageName]?.let { hostConfig.portBindings.set(it) }
    hostConfig.autoRemove.set(true)
}
val listImages by tasks.creating(com.bmuschko.gradle.docker.tasks.image.DockerListImages::class.java) { onNext { localImageTags.addAll((this as com.github.dockerjava.api.model.Image).repoTags) } }
integrationTestImageNameList.forEachIndexed { index, imageName ->

    tasks.register<com.bmuschko.gradle.docker.tasks.image.DockerPullImage>("pullImage$index") {
        dependsOn(listImages)
        onlyIf { !localImageTags.contains(imageName) }
        doFirst { println("prepare to pull docker image $imageName") }
        image.set(imageName)
    }

    tasks.register<com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer>("createContainer$index") {
        dependsOn("pullImage$index")
        doFirst { println("prepare to create container by image name $imageName") }
        targetImageId(imageName)
        configContainer(imageName, this)
    }
    tasks.register<com.bmuschko.gradle.docker.tasks.container.DockerStartContainer>("startContainer$index") {
        val createContainer = tasks.getByName<com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer>("createContainer$index")
        dependsOn(createContainer)
        doFirst { println("trying to start container by image name $imageName") }
        targetContainerId(createContainer.containerId)
    }
    tasks.register<com.bmuschko.gradle.docker.tasks.container.DockerStopContainer>("stopContainer$index") {
        dependsOn("startContainer$index")
        doFirst { println("trying to stop container by image name $imageName") }
        val createContainer = tasks.getByName<com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer>("createContainer$index")
        targetContainerId(createContainer.containerId)
    }
}
tasks.withType(JavaCompile::class) { options.encoding = "UTF-8" }

tasks {
    bootRun {
        sourceResources(sourceSets["main"])
        systemProperty("logging.config", "classpath:logback-console.xml")
        systemProperty("logging.level.ROOT", "INFO")
    }
    bootJar {
        layered {
            application {
                intoLayerEx("spring-boot-loader") {
                    include("org/springframework/boot/loader/**")
                }
                intoLayerEx("application")
            }
            dependencies {
                intoLayerEx("snapshot-dependencies") {
                    include("*:*:*SNAPSHOT")
                }
                intoLayerEx("frequency-change-dependencies") {
                    include("com.h2database:h2:*")
                    include("mysql:mysql-connector-java:*")
                }
                intoLayerEx("dependencies")
            }
            layerOrder = layerList
        }
    }
    build { dependsOn(createDockerfile) }
    test {
        //dependsOn(integrationTestImageNameList.mapIndexed { index, _ -> "startContainer$index" })
        //finalizedBy(integrationTestImageNameList.mapIndexed { index, _ -> "stopContainer$index" })
        systemProperties(testSystemProps)
        failFast = true
        useJUnitPlatform()
        testLogging { showStandardStreams = true }
    }
}
springBoot { buildInfo() }