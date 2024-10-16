plugins {
    id("com.android.library")
//    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    // gpt
//    id("com.android.library")
    id("kotlin-android")
    `maven-publish`
}

android {
    namespace = "com.pukkait.showlocationonimage"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            multiDexEnabled = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
//    configure<PublishingExtension> {
//        publications.create<MavenPublication>("showlocationonimage") {
////            from(components["release"])
//            groupId = "com.pukkait"
//            artifactId = "showlocationonimage"
//            version = "0.0.18"
////            pom.packaging = "jar"
////            artifact("ShowLoctionOnImage/showlocationonimage")
//            pom {
//                name.set("ShowLocationOnImage")
//                description.set("A library for showing location on image.")
//                url.set("https://github.com/pukkait/ShowLocationOnImage")
//            }
//
//        }
//        repositories {
//            maven{
//                url = uri("https://jitpack.io")
//            }
//        }
//    }

}

dependencies {
    implementation(libs.kotlin.stdlib)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.exifinterface)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.activity.ktx)
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.camera.extensions)

}
publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.pukkait"
            artifactId = "showlocationonimage"
            version = "0.0.22"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
