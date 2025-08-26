plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradleup.shadow") version "8.3.6"
    id("maven-publish")
}

group = "dev.expx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.apache.maven:maven-resolver-provider:3.9.10")
    implementation("org.apache.maven.resolver:maven-resolver-impl:1.9.24")
    implementation("org.apache.maven.resolver:maven-resolver-connector-basic:1.9.24")
    implementation("org.apache.maven.resolver:maven-resolver-transport-file:1.9.24")
    implementation("org.apache.maven.resolver:maven-resolver-transport-http:1.9.24")
    implementation("org.apache.maven.resolver:maven-resolver-transport-classpath:1.9.24")
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("")
        archiveFileName.set("DependencyManager.jar")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJar") {
            artifact(tasks.shadowJar) {
                builtBy(tasks.shadowJar)
            }
        }
    }
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}