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
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-config") { exclude(module = "spring-cloud-config-client") }
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.querydsl:querydsl-jpa")
    implementation("com.h2database:h2")
    implementation("mysql:mysql-connector-java")
    implementation("de.codecentric:spring-boot-admin-starter-client:$springBootAdminVersion")
    implementation("org.apache.commons:commons-lang3")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("com.querydsl:querydsl-apt:4.3.1:jpa")
    annotationProcessor("javax.persistence:javax.persistence-api")
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
        outputDir = file("$buildDir/classes/java/main/")
        testOutputDir = file("$buildDir/classes/java/test/")
    }
}
val layerList = listOf("dependencies", "spring-boot-loader", "snapshot-dependencies", "frequency-change-dependencies", "application")
tasks {
    bootRun { sourceResources(sourceSets["main"]) }
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
        failFast = true
        useJUnitPlatform()
        testLogging { showStandardStreams = true }
    }
}
val baseImage: String by System.getProperties()
val createDockerfile by tasks.creating(com.bmuschko.gradle.docker.tasks.image.Dockerfile::class) {
    from(baseImage)
    workingDir("application")
    copyFile("*.jar", "app.jar")
    runCommand("java -Djarmode=layertools -jar app.jar extract")
    from(baseImage)
    workingDir("application")
    layerList.forEach { copyFile("--from=builder application/$it/", "./") }
    entryPoint("java", "org.springframework.boot.loader.JarLauncher")
}

tasks.create("buildImage", com.bmuschko.gradle.docker.tasks.image.DockerBuildImage::class) {
    dependsOn(createDockerfile)
    dependsOn(tasks.build)
    val contextDir = file("$buildDir/libs/")
    doFirst { createDockerfile.destFile.get().asFile.copyTo(File(contextDir, "Dockerfile")) }
    inputDir.set(contextDir)
    images.add("shenliuyang/appsugar:${project.version}")
}


springBoot { buildInfo() }
