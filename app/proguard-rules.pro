# ──────────────────────────────────────────────
# General
# ──────────────────────────────────────────────
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# JNI — enlaces a código nativo
-keepclasseswithmembernames class * {
    native <methods>;
}

# ──────────────────────────────────────────────
# Kotlin
# ──────────────────────────────────────────────
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**
-dontwarn kotlinx.datetime.**

# ──────────────────────────────────────────────
# KotlinX Serialization
# ──────────────────────────────────────────────
-keep @kotlinx.serialization.Serializable class * { *; }
-keep @androidx.annotation.Keep class *
-keepclassmembers class * {
    *** Companion;
}

# ──────────────────────────────────────────────
# Hilt
# ──────────────────────────────────────────────
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
}
-keep class * extends dagger.hilt.android.internal.managers.ActivityComponentManager { *; }
-dontwarn dagger.hilt.**

# ──────────────────────────────────────────────
# WorkManager + Hilt Worker
# ──────────────────────────────────────────────
-keepclassmembers class * extends androidx.work.ListenableWorker { *; }

# ──────────────────────────────────────────────
# Room
# ──────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase { *; }
-keepclassmembers @androidx.room.Entity class * { *; }

# ──────────────────────────────────────────────
# Navigation — rutas serializables
# ──────────────────────────────────────────────
-keep @kotlinx.serialization.Serializable class * { *; }

# ──────────────────────────────────────────────
# Coil
# ──────────────────────────────────────────────
-dontwarn coil.**

# ──────────────────────────────────────────────
# Sceneview & Filament (AR y Renderizado 3D)
# ──────────────────────────────────────────────
-keep class io.github.sceneview.** { *; }
-dontwarn io.github.sceneview.**

# ──────────────────────────────────────────────
# Zero Allocation Hashing
# ──────────────────────────────────────────────
-keep class net.openhft.hashing.** { *; }
-dontwarn net.openhft.hashing.**
-dontwarn sun.misc.**

# ──────────────────────────────────────────────
# ML Kit & ZXing (consumer rules incluidas en AAR,
# se mantienen aquí solo como respaldo explícito)
# ──────────────────────────────────────────────
-dontwarn com.google.mlkit.vision.barcode.**
-dontwarn com.google.zxing.**