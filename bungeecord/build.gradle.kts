plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

group = "net.okocraft"
version = "1.0.0"

println(projectDir.absolutePath)

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("file://${projectDir.absolutePath}/libraries/")
}

dependencies {
    compileOnly("io.github.waterfallmc:waterfall-proxy:1.19-R0.1-SNAPSHOT")

    implementation("net.okocraft:bungeeteleport-core:1.0.0")

    implementation("org.jetbrains:annotations:23.1.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks {
    compileJava {
        options.release.set(17)
    }
    build {
        dependsOn(shadowJar)
    }
    test {
        useJUnitPlatform()
    }
}
