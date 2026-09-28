# libxposed API is supplied by the framework at runtime and is intentionally compileOnly.
-dontwarn io.github.libxposed.api.**

# LSPosed loads the module entry from META-INF/xposed/java_init.list by class name.
-keep class com.yagay.YNFC.xposed.NfcInjectionModule { *; }

# Hook targets/profile serialization depend on stable member metadata and reflection.
-keep class com.yagay.YNFC.xposed.discovery.** { *; }
-keep class com.yagay.YNFC.xposed.profile.** { *; }
-keep class com.yagay.YNFC.xposed.payload.** { *; }

# Keep Android component entry points explicit for release verification.
-keep class com.yagay.YNFC.ConfigProvider { *; }
-keep class com.yagay.YNFC.MainActivity { *; }

-keepattributes SourceFile,LineNumberTable,InnerClasses,EnclosingMethod,Signature
