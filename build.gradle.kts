plugins {
    id("java")
}

group = "io.mikaple"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.opencollab.dev/main/") {
        name = "opencollab"
    }

}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.geysermc.mcprotocollib:protocol:26.1-SNAPSHOT")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("io.netty:netty-all:4.2.1.Final")
}

tasks.test {
    useJUnitPlatform()
}