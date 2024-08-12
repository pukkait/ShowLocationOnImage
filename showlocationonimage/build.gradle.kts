plugins {
    alias(libs.plugins.android.application)
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
        applicationId = "com.pukkait.showlocationonimage"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled =  true

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
    configure<PublishingExtension> {
        publications.create<MavenPublication>("release") {
            groupId = "com.pukkait"
            artifactId = "showlocationonimage"
            version = "0.0.14"
//            pom.packaging = "jar"
//            artifact("ShowLoctionOnImage/showlocationonimage")
            pom {
                name.set("ShowLocationOnImage")
                description.set("A library for showing location on image.")
                url.set("https://github.com/pukkait/ShowLocationOnImage")
            }
            afterEvaluate {
                from(components["release"])
            }
        }
        repositories {
            mavenLocal()
        }
    }

}

dependencies {
    implementation(libs.kotlin.stdlib) // Adjust the version as needed

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}