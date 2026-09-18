// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

subprojects {
    afterEvaluate {
        val androidExtension = extensions.findByName("android") as? com.android.build.gradle.BaseExtension
        androidExtension?.compileSdkVersion(35)
    }

    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
            force("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            force("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
            force("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.9.0")
            force("com.google.android.gms:play-services-ads:23.6.0")
            force("com.android.billingclient:billing:7.1.1")
            force("com.android.billingclient:billing-ktx:7.1.1")
            force("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
            force("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
            force("androidx.lifecycle:lifecycle-process:2.8.7")
        }
    }

    tasks.withType<com.android.build.gradle.internal.tasks.CheckAarMetadataTask>().configureEach {
        enabled = false
    }
}
