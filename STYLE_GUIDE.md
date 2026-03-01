# TBA Android Style Guide

## Text capitalization: Sentence case

All UI strings use **sentence case**: capitalize only the first word and proper nouns.

### Examples

| Correct              | Incorrect            |
|----------------------|----------------------|
| Happening now        | Happening Now        |
| Other events         | Other Events         |
| All teams            | All Teams            |
| Score breakdown      | Score Breakdown      |
| Notification types   | Notification Types   |
| Upcoming match       | Upcoming Match       |
| Match score          | Match Score          |
| Sign in              | Sign In              |

### What counts as a proper noun (keep capitalized)

- Brand names: YouTube, Twitch, Livestream
- FRC-specific proper nouns: Red alliance, Blue alliance (alliance is not a proper noun, but Red/Blue are color names used as proper team identifiers — keep "Red" and "Blue" capitalized)
- Product names: myTBA

### Applies to

- Section headers
- Button labels
- Dialog titles and button text
- Menu items
- Notification type display names
- Score breakdown field labels
- Tab labels (single words are unaffected)

### Does not apply to

- Proper nouns and brand names (see above)
- Acronyms (RP, BCVI)
- Data from the server (event names, team names, etc.)

## Terminology

| Preferred (user-facing)  | Avoid (user-facing)      | Notes                                                |
|--------------------------|--------------------------|------------------------------------------------------|
| Notifications            | Subscriptions            | Users configure what they get *notified* about. The internal code still uses `Subscription` as a domain/data model name — this guideline only applies to UI strings. |

## Brand colors

These match the TBA web server CSS variables (`tba_variables.less`).

| Name           | Hex       | Usage                                      |
|----------------|-----------|---------------------------------------------|
| TBA Blue       | `#3F51B5` | Primary brand color, launcher background    |
| TBA Blue Dark  | `#303F9F` | Darker variant                              |
| TBA Red        | `#770000` | Debug/beta builds, launcher background      |
| TBA Red Dark   | `#440000` | Darker variant                              |

### Indigo tonal scale

All brand blues are drawn from the Material Design Indigo scale:

| Shade | Hex       | Token              | Usage                                    |
|-------|-----------|--------------------|------------------------------------------|
| 50    | `#E8EAF6` | —                  | Reserved (not currently used)            |
| 100   | `#C5CAE9` | `TBAPastelBlue`    | Light-mode `primaryContainer`            |
| 200   | `#9FA8DA` | `TBABlueLight`     | Dark-mode `primary`                      |
| 300   | `#7986CB` | —                  | Reserved                                 |
| 400   | `#5C6BC0` | —                  | Reserved                                 |
| 500   | `#3F51B5` | `TBABlue`          | Canonical brand color, TopAppBar         |
| 600   | `#3949AB` | —                  | Reserved                                 |
| 700   | `#303F9F` | `TBABlueDark`      | Dark-mode `primaryContainer`             |
| 800   | `#283593` | —                  | Reserved                                 |
| 900   | `#1A237E` | `TBAIndigo900`     | Light-mode `onPrimaryContainer`          |

### Material 3 color role mappings

**Light scheme:**

| Role                 | Value       | Source        |
|----------------------|-------------|---------------|
| `primary`            | `#3F51B5`   | Indigo 500    |
| `onPrimary`          | `White`     |               |
| `primaryContainer`   | `#C5CAE9`   | Indigo 100    |
| `onPrimaryContainer` | `#1A237E`   | Indigo 900    |
| `surfaceTint`        | `#3F51B5`   | Indigo 500    |
| Other roles          | M3 defaults |               |

**Dark scheme:**

| Role                 | Value       | Source        |
|----------------------|-------------|---------------|
| `primary`            | `#9FA8DA`   | Indigo 200    |
| `onPrimary`          | `#00174D`   |               |
| `primaryContainer`   | `#303F9F`   | Indigo 700    |
| `onPrimaryContainer` | `#C5CAE9`   | Indigo 100    |
| `surfaceTint`        | `#9FA8DA`   | Indigo 200    |
| Other roles          | M3 defaults |               |

**TopAppBar** uses `TBABlue` (`#3F51B5`) with white content in both light and dark mode via `TBATopAppBar`.

### Accessibility (WCAG contrast ratios)

| Foreground          | Background          | Ratio  | Passes         |
|---------------------|---------------------|--------|----------------|
| White on `#3F51B5`  | TopAppBar text      | 4.57:1 | AA normal text |
| `#1A237E` on `#C5CAE9` | Container text   | 8.84:1 | AAA            |
| `#9FA8DA` on dark surface | Dark-mode primary | 6.5:1 | AA          |

Dynamic colors (Material You) are **disabled** — the app always uses TBA brand colors.
