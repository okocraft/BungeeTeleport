plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

group = "net.okocraft"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("net.okocraft:bungeeteleport-bukkit:1.0.0")
    implementation("net.okocraft:bungeeteleport-bungeecord:1.0.0")
}

tasks {
    compileJava {
        options.release.set(17)
    }
    build {
        dependsOn(shadowJar)
        dependsOn(gradle.includedBuild("bukkit").task(":reobfJar"))
    }
}
