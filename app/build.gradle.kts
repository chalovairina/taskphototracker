plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
    id("kotlin-parcelize")
}

val vkApiVersion by extra("\"5.131\"")
val vkApiUrl by extra("\"https://api.vk.com\"")

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.chalova.irina.taskphototracker"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "VK_API_URL", vkApiUrl)
        buildConfigField("String", "VK_API_VER", vkApiVersion)
    }

    buildTypes {
        getByName("debug") {
            @Suppress("UnstableApiUsage")
            isMinifyEnabled = true
        }
        getByName("release") {
            @Suppress("UnstableApiUsage")
            isMinifyEnabled = true
            proguardFiles(
                @Suppress("UnstableApiUsage")
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    viewBinding.isEnabled = true
    dataBinding.enable = true

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        animationsDisabled = true
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {

    implementation("com.jakewharton.timber:timber:5.0.1")

    val appcompat_version = "1.6.1"
    implementation("androidx.appcompat:appcompat:$appcompat_version")
    implementation("androidx.appcompat:appcompat-resources:$appcompat_version")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.annotation:annotation:1.5.0")

    // SplashScreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // RecyclerView selection
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")

    val navigationVersion = rootProject.extra.get("navigation_version")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    androidTestImplementation("androidx.navigation:navigation-testing:$navigationVersion")

    // Room components
    val room_version = "2.4.3"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    androidTestImplementation("androidx.room:room-testing:$room_version")
    testImplementation("androidx.room:room-testing:$room_version")

    // Data binding
    kapt("com.android.databinding:compiler:3.1.4")
    kapt("androidx.databinding:databinding-common:7.3.1")

    // ShimmerLayout
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.preference:preference:1.2.0")

    // Recyclerview
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // Retrofit
    val retrofit_version = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit_version")

    // Coroutines
    val coroutines_version = "1.6.4"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")

    // Lifecycle
    val lifecycle_version = "2.5.1"
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")

//    for java time lib
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

//    implementation("androidx.test.espresso:espresso-idling-resource:$espresso_version")

    // CircleImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // For Window Insets
    implementation("dev.chrisbanes.insetter:insetter:0.6.1")

    // Glide
    val glide_version = "4.12.0"
    implementation("com.github.bumptech.glide:glide:$glide_version")
    kapt("com.github.bumptech.glide:compiler:$glide_version")

    // Worker
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    // dagger2
    val dagger2_version = "2.45"
    implementation("com.google.dagger:dagger:$dagger2_version")
    kapt("com.google.dagger:dagger-compiler:$dagger2_version")

    val assisted_inject_version = "0.8.1"
    runtimeOnly("com.squareup.inject:assisted-inject-annotations-dagger2:$assisted_inject_version")
    kapt("com.squareup.inject:assisted-inject-processor-dagger2:$assisted_inject_version")

    // Testing

//    testImplementation("androidx:test:core:1.5.0")
    // Unit testing

    val junit_version = "4.13.2"
    testImplementation("junit:junit:$junit_version")
    testImplementation("app.cash.turbine:turbine:0.7.0")

//    testImplementation("androidx.arch.core:core-testing:2.1.0")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")

    // mockk
    testImplementation("io.mockk:mockk:1.13.4")

    // Instrumented testing

//    androidTestImplementation("androidx.test.runner:1.3.0")
    // Espresso dependencies
    val espresso_version = "3.4.0"
    androidTestImplementation("androidx.test.espresso:espresso-core:$espresso_version")

    // dagger
    kaptAndroidTest("com.google.dagger:dagger-compiler:$dagger2_version")
    androidTestImplementation("com.google.dagger:dagger:$dagger2_version")

    // for RunWith(AndroidJUnit4::class)
    androidTestImplementation("androidx.test.ext:junit:1.1.3")

    val mockito_version = "3.12.4"
//    testImplementation("org.mockito:mockito-core:$mockito_version")
    androidTestImplementation("org.mockito:mockito-core:$mockito_version")
    // to mock NavController
    androidTestImplementation("org.mockito:mockito-android:$mockito_version")

    // for InstantTaskExecutorRule
//    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")

    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")

    // to launchFragmentInContainer
//    debugImplementation("androidx.fragment:fragment-testing:1.5.4")

    // Assertions
    // contains / doesNotContains used
    androidTestImplementation("com.google.truth:truth:1.1.3")


    // for recyclerViewActions
//    androidTestImplementation ("androidx.test.espresso:espresso-contrib:$espresso_version") {
//        exclude("protobuf-lite")
//    }
}