# Add project specific ProGuard rules here.

# Firebase component registrars are instantiated via reflection.
# R8 strips their no-arg constructors without this rule.
-keep class com.google.firebase.** implements com.google.firebase.components.ComponentRegistrar {
    <init>();
}
