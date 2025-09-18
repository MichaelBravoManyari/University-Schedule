# Mantener todos los Fragments y DialogFragments
-keep class * extends androidx.fragment.app.Fragment { void <init>(); }
-keep class * extends com.google.android.material.bottomsheet.BottomSheetDialogFragment { void <init>(); }

# Navigation component
-keepclassmembers class * extends androidx.fragment.app.Fragment {
    <init>();
}