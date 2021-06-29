plugins {
    kotlin("jvm") version "1.5.20"
}


group = "software.amazon.awssdk.forge"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("script-runtime"))
    implementation(kotlin("scripting-common"))
    implementation(kotlin("scripting-jvm"))
    implementation(kotlin("scripting-jvm-host"))
//    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.20")
//    implementation("org.reflections:reflections:0.9.12")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
