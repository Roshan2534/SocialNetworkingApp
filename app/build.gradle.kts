plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("/Users/roshansaundankar/Desktop/CustomKey")
            storePassword = "Techconsulting101"
            keyAlias = "releaseCustomKey"
            keyPassword = "Techconsulting101"
        }
    }
    namespace = "com.example.capstone"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.capstone"
        minSdk = 24
        targetSdk = 33
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

            val awsAccessKey = findProperty("aws_access_key")?.toString() ?: "default_aws_access_key"
            val awsSecretKey = findProperty("aws_secret_key")?.toString() ?: "default_aws_secret_key"
            val dbUrl = findProperty("db_url")?.toString() ?: "default_db_url"
            val dbUsername = findProperty("db_username")?.toString() ?: "default_db_username"
            val dbPassword = findProperty("db_password")?.toString() ?: "default_db_password"


            buildConfigField("String", "AWS_ACCESS_KEY", "\"$awsAccessKey\"")
            buildConfigField("String", "AWS_SECRET_KEY", "\"$awsSecretKey\"")
            buildConfigField("String", "DB_URL", "\"$dbUrl\"")
            buildConfigField("String", "DB_USERNAME", "\"$dbUsername\"")
            buildConfigField("String", "DB_PASSWORD", "\"$dbPassword\"")
            signingConfig = signingConfigs.getByName("release")
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
        viewBinding = true
        buildConfig = true
    }
}



dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.4")
    implementation("com.amazonaws:aws-android-sdk-s3:2.17.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    testImplementation("junit:junit:4.13.2")
    implementation("org.postgresql:postgresql:42.2.5")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.4.0")
    implementation ("androidx.navigation:navigation-ui-ktx:2.4.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.ToxicBakery.library.bcrypt:bcrypt:1.0.9")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}