import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
//    id("org.tboox.gradle-xmake-plugin") version "1.1.5" //https://github.com/xmake-io/xmake-gradle/issues/11 (zh_hans)
}

group = "io.github.pumpkinxd.examples"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.aliyun.com/repository/public/")//Ali mirror
    mavenCentral()
}
val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}
dependencies {
    shadowImpl("com.github.jnr:jnr-ffi:2.2.16")
    shadowImpl("com.github.jnr:jffi:1.3.13")
//    implementation("com.github.jnr:jnr-ffi:2.2.16")
//    implementation("com.github.jnr:jffi:1.3.13")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
tasks.register("buildNative") {
    exec {
        workingDir = File("./jni")
        commandLine("xmake", "-y")
    }

}


tasks.jar {
    dependsOn("buildNative")
    archiveBaseName.set("NOshadow")
    archiveClassifier.set("")
    archiveVersion.set("")
    enabled = false

}


tasks.shadowJar {
    enabled = true
    dependsOn("buildNative")
    configurations = listOf(shadowImpl)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveBaseName.set("test")
    archiveClassifier.set("shadow")
    archiveVersion.set("")
    manifest {
        attributes["Main-Class"] = "io.github.pumpkinxd.examples.Main"
    }
    if (DefaultNativePlatform.getCurrentOperatingSystem().isWindows && DefaultNativePlatform.getCurrentArchitecture().isAmd64) {

        from(
            "./jni/build/" + DefaultNativePlatform.getCurrentOperatingSystem().internalOs.familyName + "/" + "x64/release"
        ) {
            include(System.mapLibraryName("test"))
            into(
                "jni/" + (if (DefaultNativePlatform.getCurrentArchitecture().name.equals("x86-64")) "x86_64" else DefaultNativePlatform.getCurrentArchitecture().name) + "-" + DefaultNativePlatform.getCurrentOperatingSystem().internalOs.familyName
            )
        }
    } else {
        from(
            "./jni/build/" + DefaultNativePlatform.getCurrentOperatingSystem().internalOs.familyName + "/" + DefaultNativePlatform.getCurrentArchitecture().name + "/release"
        ) {
            include(System.mapLibraryName("test"))

            into(
                "jni/" + (if (DefaultNativePlatform.getCurrentArchitecture().name.equals("x86-64")) "x86_64" else DefaultNativePlatform.getCurrentArchitecture().name) + "_" + DefaultNativePlatform.getCurrentOperatingSystem().internalOs.familyName
            )
        }
    }


}

tasks.assemble.get().dependsOn(tasks.shadowJar)


