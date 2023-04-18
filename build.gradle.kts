import org.jetbrains.changelog.date

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.12.0"
    id("org.jetbrains.changelog") version "1.3.1"
}

group = "tk.ogorod98"
version = "1.0.6"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    // version.set("2021.2")
    version.set("2022.3")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        changeNotes.set(provider { changelog.getUnreleased().toHTML() })
        sinceBuild.set("212")
        untilBuild.set("231.*")
    }

    signPlugin {
        certificateChain.set(File("./.keys/chain.crt").readText(Charsets.UTF_8))
        privateKey.set(File("./.keys/private.pem").readText(Charsets.UTF_8))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(File("./.keys/jetbrains.token").readText(Charsets.UTF_8))
    }
}

changelog {
    version.set("1.0.0")
    path.set("${project.projectDir}/CHANGELOG.md")
    header.set(provider { "[${version.get()}] - ${date()}" })
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
}
