# Shoe Store App (Jetpack Compose)

Una app Android simple de tienda de calzado construida **desde cero** con **Kotlin + Jetpack Compose**. Incluye navegación, listado de productos, favoritos, detalle de item, perfil y ajustes.

## Funcionalidades

-   **Home** con TopAppBar + FAB central y **BottomBar**.
    
-   **Menú lateral** (pantalla dedicada) con accesos a:
    
    -   Shop list
        
    -   Favourites (contador dinámico)
        
    -   Settings
        
    -   Profile
        
-   **Shop list** con cards (imagen, título, precio, descripción):
    
    -   **Add to favourite** → agrega al ViewModel y navega a Favourites.
        
    -   **Buy** → navega a **Item Detail**.
        
-   **Item Detail**: selector de talle (dropdown), cantidad y botones Back/Buy.
    
-   **Favourites**: listado numerado con miniatura; botón central “+ Buy”.
    
-   **Settings**: secciones “Account Settings” y “More”, switches de ejemplo.
    
-   **Profile**: avatar, nombre/rol y campos de contacto.
    

## Tech stack

-   **Kotlin**, **Android Studio**
    
-   **Jetpack Compose** (Material 3, Foundation, Icons)
    
-   **Navigation Compose**
    
-   **Lifecycle ViewModel** (estado de favoritos con `mutableStateListOf`)
    
-   **Coil** (carga de imágenes remotas)
    
-   **Parcelize** (para pasar `Product` en navegación)
- ## 🚀 Cómo ejecutar

### Prerrequisitos

-   Android Studio **Giraffe+** o más nuevo
    
-   JDK 11
    
-   Android SDK 24+
    

### Pasos

`# Clonar git clone https://github.com/<tu-usuario>/<tu-repo>.git cd <tu-repo> # (Opcional) build por consola ./gradlew assembleDebug # Abrir en Android Studio y Run ▶` 

## Configuración notable

-   `AndroidManifest.xml`
    
    `<uses-permission  android:name="android.permission.INTERNET"/> <uses-permission  android:name="android.permission.ACCESS_NETWORK_STATE"/> <application  ...> <activity  android:name=".MainActivity"  android:exported="true"> <intent-filter> <action  android:name="android.intent.action.MAIN"/> <category  android:name="android.intent.category.LAUNCHER"/> </intent-filter> </activity> </application>` 
    
-   **Imágenes locales**: coloca `leather_boots.jpg` en `app/src/main/res/drawable/`
    
    > Reglas: minúsculas, sin espacios, sin extensión en el código.
    

## Estructura de proyecto (simplificada)

`app/
 ├─ src/main/
 │   ├─ java/com/example/segundochallenge/
 │   │   └─ MainActivity.kt # UI + navegación + pantallas │   ├─ res/
 │   │   ├─ drawable/leather_boots.jpg # imagen local │   │   └─ values/*.xml
 │   └─ AndroidManifest.xml
 └─ build.gradle.kts` 

## Navegación

Rutas registradas en `MainActivity.kt`:

`HOME, MENU, SHOP_LIST, DETAIL, FAVOURITES, SETTINGS, PROFILE` 

-   Menú “hamburger” → `MENU`
    
-   “Shop list” en Menú → `SHOP_LIST`
    
-   “Add to favourite” → agrega a VM y navega a `FAVOURITES`
    
-   “Buy” → `DETAIL`
    
-   Menú “Settings” → `SETTINGS`
    
-   Menú y BottomBar “Profile” → `PROFILE`
    

## Dependencias clave (build.gradle.kts)

`implementation(platform("androidx.compose:compose-bom:2024.10.01"))
implementation("androidx.activity:activity-compose:1.9.2")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.foundation:foundation")
implementation("androidx.compose.material:material-icons-extended")
implementation("androidx.navigation:navigation-compose:2.8.3")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
implementation("io.coil-kt:coil-compose:2.6.0")` 

Plugins:

`plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-parcelize")
}`
