# R8 release build of the verification application (ADR-0044, docs/android-compatibility.md).
# The application calls no engine code itself: every engine class is kept, so that R8 processes, desugars and
# optimizes all of them and verifyReleaseDex can find each one. A missing class (for example java.desktop used in a
# shared module) is an R8 error.
-keep class com.b3dgs.lionengine.** { *; }
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,Record
