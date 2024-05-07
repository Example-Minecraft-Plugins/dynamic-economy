import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
}

group = "me.davipccunha.tests"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly(fileTree("libs") { include("*.jar") })
    compileOnly(fileTree("D:\\Minecraft Dev\\artifacts\\") { include("*.jar") })
    compileOnly("net.md-5:bungeecord-chat:1.8-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    implementation("redis.clients:jedis:5.2.0-beta1")
    compileOnly(fileTree("D:\\Local Minecraft Server\\plugins") { include("bukkit-utils.jar") })
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "11"
    targetCompatibility = "11"
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    archiveFileName.set("${project.name}.jar")

    destinationDirectory.set(file("D:\\Local Minecraft Server\\plugins"))
}

bukkit {
    name = project.name
    version = "${project.version}"
    main = "me.davipccunha.tests.dynamiceconomy.DynamicEconomyPlugin"
    description = "Plugin that analyses and changes item prices according to market tendencies."
    author = "davipccunha"
    prefix = "Dynamic Economy" // As shown in console
    apiVersion = "1.8"
    softDepend = listOf("sign-shop")

    commands {
        register("dynamiceconomy") {
            description = "Força a atualização de todos os preços"
            aliases = listOf("de")
        }
    }
}