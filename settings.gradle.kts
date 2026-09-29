pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://api.xposed.info/") }
    }
}
sourceControl {
    gitRepository(uri("https://github.com/yagay/YSuite.git")) {
        producesModule("com.github.yagay.YSuite:ui")
    }
}
rootProject.name = "YNFC"
include(":app", ":feature")
