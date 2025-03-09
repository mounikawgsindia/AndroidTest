plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.wingspan.androidassment"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.wingspan.androidassment"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding=true
    }
}

dependencies {
//glide
    implementation ("com.github.bumptech.glide:glide:4.16.0") // Glide dependency
    implementation ("com.github.bumptech.glide:okhttp3-integration:4.13.2") // If you're using OkHttp


    //retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    implementation ("com.squareup.retrofit2:converter-simplexml:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")

    implementation ("androidx.activity:activity-ktx:1.10.1")
    //viewmodelscope
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.core:core-ktx:1.15.0")


    // Room dependencies
    implementation ("androidx.room:room-runtime:2.6.1" )
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    //noinspection KaptUsageInsteadOfKsp
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}