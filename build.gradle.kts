/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.6/userguide/building_java_projects.html
 */

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    java
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

sourceSets.main.get().java.srcDir("src")
sourceSets.test.get().java.srcDir("test")

dependencies {
    // Use JUnit test framework.
    testImplementation("junit:junit:4.13.2")
    implementation("junit:junit:4.13.2")

    // ASM library for bytecode manipulation.
    implementation("org.ow2.asm:asm-all:5.2")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:31.1-jre")

    // Use the json library from
    implementation("org.json:json:20231013")
}

application {
    // Define the main class for the application.
    mainClass.set("compiler.Compiler")
}
