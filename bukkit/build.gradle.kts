plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.3.8"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

group = "net.okocraft"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperDevBundle("1.19.3-R0.1-20230217.021609-97")
    // compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")

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
        dependsOn(reobfJar)
    }
    test {
        useJUnitPlatform()
    }
}
