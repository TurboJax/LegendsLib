plugins {
    `java-library`
    alias(libs.plugins.paperweight)
    alias(libs.plugins.run.paper)
}

val javaVersion = (project.property("javaVersion") as String).toInt();
val minecraftVersion = project.property("minecraftVersion");

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("${minecraftVersion}+")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("${minecraftVersion}")
        jvmArgs("-Xms2G", "-Xmx2G", "-Dlog4j.configurationFile=log4j2.xml")
    }

    processResources {
        val props = mapOf("version" to version, "minecraftVersion" to minecraftVersion)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
