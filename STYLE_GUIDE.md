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

- Brand names: YouTube, Twitch, Livestream, Google, Nexus (FRC Nexus), Maps (the app)
- FRC-specific proper nouns: Red alliance, Blue alliance (alliance is not a proper noun, but Red/Blue are color names used as proper team identifiers — keep "Red" and "Blue" capitalized)
- Product names: myTBA, The Blue Alliance, Team @ Event (TBA's name for a team's page at one event)
- FIRST program, event and award names, written the way the Game Manual writes them:
  - FIRST Championship, and "Championship" on its own when it means that event.
  - Einstein: the Championship's finals field.
  - Regional Pool: the manual capitalizes it as the name of the regional Championship qualification pool.
  - Impact Award, FIRST Leadership Award, Engineering Inspiration, Rookie All Star: official award names.
  - Week 1, Week 2, …: the manual's names for competition weeks.
  - Finals: the manual's name for the last playoff round.
- Days of the week and months.

These are **not** proper nouns, so they take sentence case. The manual and the TBA website write
them in lowercase in running text, and the website's Title Case headings are its heading style,
not a sign of a proper noun:

- district points, regional points, team age (the manual's section titles are Title Case, its prose is not)
- regional advancement, pit map, pit location, upper bracket, lower bracket

The list is enforced by `SentenceCaseTest`
(`app/src/test/kotlin/com/thebluealliance/android/ui/SentenceCaseTest.kt`). It fails on a capital
anywhere but a sentence's first word unless the word is in
`app/src/test/resources/sentence-case-allowlist.txt`. Add a proper noun here and to that file together.

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

These match the TBA web server CSS variables (`tba_variables.less`). In code they live in
`TbaBrandColors` (`:core-theme`), shared by the phone, Wear and TV apps.

| Name             | Hex       | Usage                                                        |
|------------------|-----------|--------------------------------------------------------------|
| TBA Blue         | `#3F51B5` | Canonical brand color (Material Indigo 500): app bar, tab row, launcher and shortcut icons, color-scheme seed |
| TBA Header Blue  | `#5C6BC0` | Section and table headers (Material Indigo 400), a step lighter than the app bar |
| TBA Red          | `#770000` | Debug/beta builds, launcher background                       |
| TBA Red Dark     | `#440000` | Darker variant                                               |

Content on TBA Blue and TBA Header Blue is white, in both light and dark mode.

### Color scheme

Every Material 3 color role (all three apps) is generated from TBA Blue with Material Color
Utilities, the library behind Material Theme Builder ("fidelity" / color-match scheme). The
generated tokens and the generator live in `:core-theme`; see `core-theme/README.md` to regenerate.
Don't hand-pick role colors: change the seed and regenerate instead.

Key roles:

| Role                 | Light     | Dark      |
|----------------------|-----------|-----------|
| `primary`            | `#24389C` | `#BAC3FF` |
| `onPrimary`          | `#FFFFFF` | `#08218A` |
| `primaryContainer`   | `#3F51B5` | `#3F51B5` |
| `background`         | `#FBF8FF` | `#121319` |

`primaryContainer` is exactly TBA Blue in both modes, so brand bars use it. Brand surfaces
(bars, headers) use white content rather than `onPrimaryContainer`, for contrast.

### Accessibility (WCAG contrast ratios)

| Foreground            | Background         | Ratio   | Where                          |
|-----------------------|--------------------|---------|--------------------------------|
| White                 | `#3F51B5`          | 6.87:1  | App bar, tab row, week chips   |
| White                 | `#5C6BC0`          | 4.86:1  | Section and table headers      |
| `#24389C` (primary)   | `#FBF8FF`          | 9.42:1  | Light-mode links and buttons   |
| White                 | `#24389C`          | 9.90:1  | Light-mode filled button text  |
| `#BAC3FF` (primary)   | `#121319`          | 10.89:1 | Dark-mode links and buttons    |
| `#08218A` (onPrimary) | `#BAC3FF`          | 7.74:1  | Dark-mode filled button text   |

All pass WCAG AA for normal text (4.5:1).

Dynamic colors (Material You) are **disabled** in the app, which always uses TBA colors. The
home-screen widget follows the wallpaper on Android 12+, the norm for widgets.
