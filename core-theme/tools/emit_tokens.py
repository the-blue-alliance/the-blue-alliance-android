"""Turns GenerateColorTokens' output into the Kotlin token files. Run via generate.sh."""
import sys

lines = sys.stdin.read().splitlines()
sec = None
pal = []
roles = {"LIGHT": [], "DARK": []}
for line in lines:
    if line in ("PALETTE", "LIGHT", "DARK"):
        sec = line
        continue
    if line.startswith("//"):
        continue
    k, v = line.split()
    (pal if sec == "PALETTE" else roles[sec]).append((k, v))

out = sys.argv[1]
hdr = "package com.thebluealliance.android.core.theme\n\nimport androidx.compose.ui.graphics.Color\n\n"
gen = (
    "// GENERATED from the TBA brand seed #3F51B5 with Material Color Utilities' SchemeFidelity (the\n"
    '// "color match" option in Material Theme Builder), standard contrast. Regenerate rather than\n'
    "// hand-edit: see this module's README.\n"
)


def cap(k):
    return k[0].upper() + k[1:]


with open(f"{out}/TbaPaletteTokens.kt", "w") as f:
    f.write(hdr + gen + """
/**
 * The TBA tonal palettes: every tone each Material library's own default scheme reads. The same
 * shape as the libraries' internal `PaletteTokens`, so a platform whose scheme is defined as tone
 * lookups (Wear) maps them one to one.
 */
object TbaPaletteTokens {
""" + "".join(f"    val {k} = Color({v})\n" for k, v in pal) + "}\n")

with open(f"{out}/TbaColorTokens.kt", "w") as f:
    f.write(hdr + gen + """
/** Material 3 color roles for light themes, generated from the TBA seed. */
object TbaLightColorTokens {
""" + "".join(f"    val {cap(k)} = Color({v})\n" for k, v in roles["LIGHT"]) + """}

/** Material 3 color roles for dark themes, generated from the TBA seed. */
object TbaDarkColorTokens {
""" + "".join(f"    val {cap(k)} = Color({v})\n" for k, v in roles["DARK"]) + "}\n")
