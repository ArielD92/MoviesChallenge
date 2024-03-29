plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.dagger.hilt.android")
  kotlin("kapt")
  id("androidx.navigation.safeargs.kotlin")
}

android {
  namespace = "com.arieldywelski.movieschallenge"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.arieldywelski.movieschallenge"
    minSdk = 23
    targetSdk = 34
    versionCode = 1
    versionName = "0.0.1"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  buildFeatures {
    viewBinding = true

  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_17.toString()
  }
}

dependencies {
  implementation("androidx.core:core-ktx:1.12.0")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("com.google.android.material:material:1.10.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")

  implementation("androidx.navigation:navigation-fragment-ktx:2.7.4")
  implementation("androidx.navigation:navigation-ui-ktx:2.7.4")
  implementation("androidx.legacy:legacy-support-v4:1.0.0")

  implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

  implementation("com.google.dagger:hilt-android:2.48.1")
  kapt("com.google.dagger:hilt-android-compiler:2.48.1")

  implementation("com.github.bumptech.glide:glide:4.16.0")

  implementation("com.google.code.gson:gson:2.10.1")
  implementation ("com.squareup.retrofit2:retrofit:2.9.0")
  implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
  implementation ("com.squareup.okhttp3:okhttp:4.10.0")
  implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")
  implementation ("com.squareup.okhttp3:okhttp-urlconnection:4.5.0")

  implementation("androidx.room:room-runtime:2.6.0")
  implementation ("androidx.room:room-ktx:2.6.0")
  kapt("androidx.room:room-compiler:2.6.0")

  implementation( "androidx.paging:paging-runtime-ktx:3.2.1")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

kapt {
  correctErrorTypes = true
}