plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val variantMultipleOptions = "multipleOptions"
val variantSingleOption = "singleOption"

android {
    namespace = "com.corphish.quicktools"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.corphish.quicktools"
        minSdk = 30
        targetSdk = 34
        versionCode = 15
        versionName = "1.4.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "variant"

    productFlavors {
        /*
         * This flavor provides all the supported process_text options inside the
         * text selection context menu. This provides options directly to the user
         * at the cost of cluttered context menu.
         */
        create(variantMultipleOptions) {
            dimension = "variant"
        }

        /*
         * This flavor provides a single option inside the text selection context menu
         * that reveals all the supported options. This helps to reduce clutter in the
         * context menu at expense of 1 more step.
         */
        create(variantSingleOption) {
            dimension = "variant"
            applicationIdSuffix = ".single"
        }
    }

    androidComponents {
        onVariants { variant ->
            val apkName = if (variant.name.startsWith(variantMultipleOptions)) "app-release.apk" else "app-release-single.apk"

            variant.outputs.forEach { output ->
                if (output is com.android.build.api.variant.impl.VariantOutputImpl) {
                    output.outputFileName = apkName
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.preference:preference-ktx:1.2.1")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-text-google-fonts")
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    // To evaluate mathematical expressions
    implementation("net.objecthunter:exp4j:0.4.8")

    // Testing
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Compose debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}