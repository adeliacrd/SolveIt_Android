plugins {
    alias(libs.plugins.android.application)
    // A linha do plugin Kotlin foi REMOVIDA daqui
    alias(libs.plugins.googleGmsGoogleServices) // Aplica o plugin Google Services para o Firebase
}

android {
    namespace = "com.example.solveit" // Certifique-se que este é o namespace correto do seu app
    compileSdk = 36 // Você pode ajustar para a versão mais recente estável que estiver usando

    defaultConfig {
        applicationId = "com.example.solveit" // Certifique-se que este é o ID correto da sua aplicação
        minSdk = 24 // Mantenha a sua minSdk
        targetSdk = 36 // Mantenha a sua targetSdk
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
        // Configurações para compilação Java
        // Java 8 é uma escolha comum e estável para compatibilidade
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        // Se você estava usando Java 11 especificamente e funcionava bem para seu projeto Java puro,
        // você poderia considerar voltar para:
        // sourceCompatibility = JavaVersion.VERSION_11
        // targetCompatibility = JavaVersion.VERSION_11
    }
    // O bloco kotlinOptions foi REMOVIDO pois o projeto é puramente Java
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Dependências do Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    implementation(libs.firebase.auth)       // Para Firebase Authentication
    implementation(libs.firebase.firestore)  // Para Cloud Firestore

}

