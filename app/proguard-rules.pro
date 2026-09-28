# LSPosed loads the module entry from META-INF/xposed/java_init.list by class name.
-keep class com.yagay.YNFC.xposed.NfcInjectionModule { *; }

# Hook targets/profile serialization depend on stable member metadata and reflection.
-keep class com.yagay.YNFC.xposed.discovery.** { *; }
-keep class com.yagay.YNFC.xposed.profile.** { *; }
-keep class com.yagay.YNFC.xposed.payload.** { *; }

# Keep Android component entry points explicit for release verification.
-keep class com.yagay.YNFC.ConfigProvider { *; }
-keep class com.yagay.YNFC.MainActivity { *; }

# Preserve useful source/member names in crash diagnostics while still shrinking the app.
-keepattributes SourceFile,LineNumberTable,InnerClasses,EnclosingMethod,Signature
