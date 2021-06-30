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
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
}

tasks.compileKotlin {
    kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
}

tasks.test {
    useJUnitPlatform()
}
