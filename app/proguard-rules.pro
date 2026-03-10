# Add project specific ProGuard rules here.

# Glance ActionCallback subclasses are instantiated by class name via reflection
# (Class.getDeclaredConstructor() with zero args). R8 must preserve the class name
# AND the no-arg constructor, otherwise widget actions silently fail with
# NoSuchMethodException on <init>.
-keep class * implements androidx.glance.appwidget.action.ActionCallback {
    <init>();
}
