plugins {
    id("com.android.application")
//    id("com.buyi.huxq17.AgencyPlugin")

}

android {
    namespace = "com.example.update"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.update"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

//    implementation("com.buyi.huxq17:agencyplugin:1.1.2")
    implementation("com.github.bumptech.glide:glide:3.7.0")
    implementation(files("libs\\jsoup-1.16.1.jar"))
    implementation(files("libs\\jedis-2.9.0.jar"))
    implementation("io.apisense:rhino-android:1.2.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.github.iammert:ReadableBottomBar:0.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
