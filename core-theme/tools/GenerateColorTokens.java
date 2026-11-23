import com.google.android.material.color.utilities.*;
import java.lang.reflect.Method;
import java.util.*;
public class GenerateColorTokens {
  static String hex(int argb){ return String.format("0x%08X", argb); }
  public static void main(String[] a) throws Exception {
    int seed = 0xFF3F51B5;
    Hct h = Hct.fromInt(seed);
    System.out.printf("// seed hue=%.1f chroma=%.1f tone=%.1f%n", h.getHue(), h.getChroma(), h.getTone());
    DynamicScheme light = new SchemeFidelity(h, false, 0.0), dark = new SchemeFidelity(h, true, 0.0);
    String[][] pals = {{"Primary","primaryPalette"},{"Secondary","secondaryPalette"},{"Tertiary","tertiaryPalette"},{"Neutral","neutralPalette"},{"NeutralVariant","neutralVariantPalette"},{"Error","errorPalette"}};
    int[] tones = {0,4,5,6,10,12,15,17,20,22,24,25,30,35,40,50,60,70,80,87,90,92,94,95,96,98,99,100};
    System.out.println("PALETTE");
    for (String[] p: pals){ TonalPalette tp=(TonalPalette)DynamicScheme.class.getField(p[1]).get(light);
      for(int t: tones) System.out.println(p[0]+t+" "+hex(tp.tone(t))); }
    MaterialDynamicColors mdc = new MaterialDynamicColors();
    String[] roles = {"primary","onPrimary","primaryContainer","onPrimaryContainer","inversePrimary","secondary","onSecondary","secondaryContainer","onSecondaryContainer","tertiary","onTertiary","tertiaryContainer","onTertiaryContainer","background","onBackground","surface","onSurface","surfaceVariant","onSurfaceVariant","surfaceTint","inverseSurface","inverseOnSurface","error","onError","errorContainer","onErrorContainer","outline","outlineVariant","scrim","surfaceBright","surfaceContainer","surfaceContainerHigh","surfaceContainerHighest","surfaceContainerLow","surfaceContainerLowest","surfaceDim","primaryFixed","primaryFixedDim","onPrimaryFixed","onPrimaryFixedVariant","secondaryFixed","secondaryFixedDim","onSecondaryFixed","onSecondaryFixedVariant","tertiaryFixed","tertiaryFixedDim","onTertiaryFixed","onTertiaryFixedVariant"};
    for (Object[] sc: new Object[][]{{"LIGHT",light},{"DARK",dark}}){ System.out.println(sc[0]);
      for(String r: roles){ String m = r.equals("inverseOnSurface")?"inverseOnSurface":r; Method mm; try { mm=MaterialDynamicColors.class.getMethod(m);} catch(NoSuchMethodException e){ System.out.println(r+" MISSING"); continue;}
        DynamicColor dc=(DynamicColor)mm.invoke(mdc); System.out.println(r+" "+hex(dc.getArgb((DynamicScheme)sc[1]))); } }
  }
}
