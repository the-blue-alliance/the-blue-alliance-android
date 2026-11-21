# core-theme

The one TBA color scheme, shared by the phone (`:app`), TV (`:tv`) and Wear (`:wear`) apps.

- `TbaPaletteTokens`: the tonal palettes (Primary0…Primary100, Secondary…, Tertiary…, Neutral…,
  NeutralVariant…, Error…).
- `TbaLightColorTokens` / `TbaDarkColorTokens`: the Material 3 color roles.
- `TbaBrandColors`: fixed brand colors that aren't roles (the lamp tile).

All of it is generated from the brand seed **#3F51B5** with Material Color Utilities'
`SchemeFidelity`, the "color match" option in Material Theme Builder. It keeps the brand indigo as
`primaryContainer` in both light and dark, and derives contrast-safe tones around it.

This module holds tokens only, with no Material library dependency. Each app builds its own
library's scheme from them, so all three stay one family:
- Phone: `lightColorScheme` / `darkColorScheme` from the role tokens.
- TV: tv-material `darkColorScheme` from the dark role tokens.
- Wear: `ColorScheme` from the palette tokens, using the same tones wear-compose-material3's
  defaults use.

To change the seed or regenerate, edit `tools/GenerateColorTokens.java` and run
`core-theme/tools/generate.sh`. Don't hand-edit the generated files.
