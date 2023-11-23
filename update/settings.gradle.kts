pluginManagement {
    repositories {
        google()
        mavenCentral()
        jcenter()
////        gradlePluginPortal()
//        maven { url=uri("https://maven.aliyun.com/repository/jcenter/")}
//        maven { url=uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { url=uri("https://jitpack.io") }
//        maven { url=uri("https://maven.aliyun.com/repository/public/")}
//        maven { url=uri("https://maven.aliyun.com/repository/google/")}
//        maven { url=uri("https://maven.aliyun.com/repository/jcenter/")}
//        maven { url=uri("https://maven.aliyun.com/repository/central/") }
    }
}

rootProject.name = "update"
include(":app")
 