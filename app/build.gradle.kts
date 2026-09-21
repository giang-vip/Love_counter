plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.app.love_counter"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.app.love_counter"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Khai báo mã ID Quảng Cáo ở đây (Giống chuẩn file Base)
        // Lưu ý: Dấu ngoặc kép bên trong cực kỳ quan trọng
        buildConfigField("String", "inter_id", "\"ca-app-pub-3940256099942544/1033173712\"")
        buildConfigField("String", "native_id", "\"ca-app-pub-3940256099942544/2247696110\"")
        buildConfigField("String", "banner_id", "\"ca-app-pub-3940256099942544/6300978111\"")
        buildConfigField("String", "app_open_id", "\"ca-app-pub-3940256099942544/9257395921\"")
        buildConfigField("String", "reward_id", "\"ca-app-pub-3940256099942544/5224354917\"")
        
        // App ID dùng cho Manifest
        manifestPlaceholders["admob_app_id"] = "ca-app-pub-3940256099942544~3347511713"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures { 
        viewBinding = true 
        buildConfig = true // Phải bật cờ này để Gradle sinh ra file BuildConfig chứa các ID trên
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            excludes += "META-INF/OSGI-INF/MANIFEST.MF"
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.2")
    implementation("com.google.android.material:material:1.12.0")

    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    implementation("com.applandeo:material-calendar-view:1.9.0-rc03")
    implementation("com.google.android.gms:play-services-ads:23.0.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("androidx.lifecycle:lifecycle-process:2.8.7")

    // Firebase Bill of Materials (BOM) & Remote Config
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-analytics")
    
    // Gson
    implementation("com.google.code.gson:gson:2.14.0")

    implementation(project(":ads"))

}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
