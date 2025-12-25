plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "2.1.21-2.0.1"
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

apply(plugin = "stringfog")
configure<com.github.megatronking.stringfog.plugin.StringFogExtension> {
    implementation = "com.github.megatronking.stringfog.xor.StringFogImpl"
    packageName = "com.github.megatronking.stringfog.app"
    kg = com.github.megatronking.stringfog.plugin.kg.RandomKeyGenerator()
    mode = com.github.megatronking.stringfog.plugin.StringFogMode.base64
}


android {
    namespace = "com.szr.co.smart.qr"
    compileSdk = 35

    defaultConfig {
        //com.qr.qrscanner.barcodescanner.smartscanner
        applicationId = ""
        minSdk = 26
        targetSdk = 35
        versionCode = 4
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("smart.jks")
            storePassword = "smart34"
            keyAlias = "smart"
            keyPassword = "smart34"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.viewbinding)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.github.jenly1314:zxing-lite:3.3.0")
    //lottie
    implementation("com.airbnb.android:lottie:6.6.2")

    //gilde
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("jp.wasabeef:glide-transformations:4.3.0")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation("androidx.room:room-runtime:2.7.2")
    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:2.7.1")
    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor("androidx.room:room-compiler:2.7.2")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.7.2")
    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:2.7.2")
    implementation("androidx.paging:paging-runtime:3.3.6")
    implementation("com.github.lihangleo2:ShadowLayout:3.4.1")

    //admob 广告
    implementation("com.google.android.gms:play-services-ads:24.2.0")
//    //admob 广告聚合
    implementation("com.google.ads.mediation:applovin:13.2.0.1")
    implementation("com.google.ads.mediation:ironsource:8.8.0.0")
    implementation("com.google.ads.mediation:mintegral:16.9.61.0")
    implementation("com.google.ads.mediation:pangle:6.5.0.8.0")
    implementation("com.unity3d.ads:unity-ads:4.14.1")
    implementation("com.google.ads.mediation:unity:4.14.2.0")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-messaging-ktx")

    //ump
    implementation("com.google.android.ump:user-messaging-platform:3.1.0")
    //fb
    implementation("com.facebook.android:facebook-android-sdk:latest.release")
    //热云
    implementation("com.reyun.solar.engine.oversea:solar-engine-core:1.2.9.6")
    //instal ref
    implementation("com.android.installreferrer:installreferrer:2.2")

    implementation("org.greenrobot:eventbus:3.3.1")

    implementation("com.github.megatronking.stringfog:xor:5.0.0")
    //okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.tencent:mmkv:1.3.14")

    //gson
    implementation("com.google.code.gson:gson:2.11.0")

//    debugImplementation("com.facebook.flipper:flipper:0.233.0")
//    debugImplementation("com.facebook.soloader:soloader:0.10.5")
//
//    releaseImplementation("com.facebook.flipper:flipper-noop:0.233.0")
}