plugins {
    val lombokVersion: String by System.getProperties()
    val releaseVersion: String by System.getProperties()
    val springBootVersion: String by System.getProperties()
    val dependencyVersion: String by System.getProperties()
    id("io.freefair.lombok") version lombokVersion
    id("net.researchgate.release") version releaseVersion
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version dependencyVersion
    id("com.bmuschko.docker-remote-api") version "6.6.1"
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
    val layered = tasks.bootJar.get().layered
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
val integrationTestImageNameList = listOf<String>(imageNameOfRedis)
val startContainerByImageName = mutableMapOf<String, com.bmuschko.gradle.docker.tasks.container.DockerStartContainer>()
val stopContainerByImageName = mutableMapOf<String, com.bmuschko.gradle.docker.tasks.container.DockerStopContainer>()
val portBindings = mapOf<String, List<String>>(imageNameOfRedis to listOf("$integrationTestRedisPort:6379"))
val configContainer = fun(imageName: String, create: com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer) = create.apply {
    portBindings[imageName]?.let { hostConfig.portBindings.set(it) }
    hostConfig.autoRemove.set(true)
}
integrationTestImageNameList.forEach { imageName ->
    val pullImage by tasks.creating(com.bmuschko.gradle.docker.tasks.image.DockerPullImage::class.java) {
        image.set(imageName)
    }
    val createContainer by tasks.creating(com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer::class.java) {
        dependsOn(pullImage)
        targetImageId(imageName)
        configContainer(imageName, this)
    }
    val startContainer by tasks.creating(com.bmuschko.gradle.docker.tasks.container.DockerStartContainer::class.java) {
        dependsOn(createContainer)
        targetContainerId(createContainer.containerId)
        startContainerByImageName[imageName] = this
    }
    val stopContainer by tasks.creating(com.bmuschko.gradle.docker.tasks.container.DockerStopContainer::class.java) {
        targetContainerId(createContainer.containerId)
        stopContainerByImageName[imageName] = this
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
        dependsOn(startContainerByImageName.values)
        finalizedBy(stopContainerByImageName.values)
        systemProperties(testSystemProps)
        failFast = true
        useJUnitPlatform()
        testLogging { showStandardStreams = true }
    }
}
springBoot { buildInfo() }