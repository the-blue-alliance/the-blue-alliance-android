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
