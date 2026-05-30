# RFC 0001: Fix district/regional advancement points data-loss race

- **Status:** Draft / Request for Comments
- **Author:** @gregmarra
- **Date:** 2026-05-29
- **Type:** Bug fix (schema change)

## Summary

District points and regional points are two distinct datasets from two TBA
endpoints, but they are stored in **one Room table with no discriminator**.
Their refreshes overwrite each other, so the Advancement / Points tab can
silently go blank, and `TeamEventDetailViewModel` mislabels the same physical
rows as two separate maps. Fix by adding a `source` column to the primary key.

## Problem

`EventDistrictPointsEntity` is a single table keyed only by `(eventKey, teamKey)`:

```
app/src/main/.../data/local/entity/EventDistrictPointsEntity.kt:5
@Entity(tableName = "event_district_points", primaryKeys = ["eventKey", "teamKey"])
```

Both endpoints share that table and that response DTO
(`TbaApi.kt:154-162` → `EventDistrictPointsResponseDto`, whose `points` defaults
to an empty map). In `EventRepository.kt:194-232` the two observe methods are
byte-identical, and **both** refresh methods do `deleteByEvent(eventKey)` then
insert:

```
observeEventDistrictPoints / observeEventRegionalPoints
    → eventDistrictPointsDao.observeByEvent(eventKey)   // identical
refreshEventDistrictPoints / refreshEventRegionalPoints
    → deleteByEvent(eventKey); insertAll(...)           // same table, same key
```

### How it loses data

- `EventDetailViewModel.kt:105-113` *observes* the correct one conditionally
  (`if (event.district != null)`), but `:307-322` (refreshAll) and `:475-491`
  (per-tab refresh) **refresh both endpoints unconditionally, in parallel
  `launch` blocks**.
- For a district event, `getEventRegionalPoints` returns an empty `200`
  (`points = {}`). `refreshEventRegionalPoints` then runs `deleteByEvent` and
  inserts nothing → it **wipes the real district rows**. Whichever of the two
  parallel refreshes lands last wins, so the tab flickers/empties
  non-deterministically.
- All refresh callers wrap the call in `catch (_: Exception) {}`, so it fails
  **silently** (also against the project's "don't swallow exceptions" guidance).

### Secondary correctness bug

`TeamEventDetailViewModel.kt:92-106` combines both observe flows into separate
`districtPointsByTeam` and `regionalPointsByTeam` maps — but since both read the
same table, the two maps are **always identical**. The code presents one
physical dataset as if it were two.

## Proposal (recommended): add a `source` discriminator

Make the table able to hold both datasets at once by adding `source` to the
primary key. The DB already uses `fallbackToDestructiveMigration(dropAllTables =
true)` (`DatabaseModule.kt:41`) over cached API data, so this needs only a
version bump — no hand-written migration, no user-visible data loss.

**Entity** (`EventDistrictPointsEntity.kt`):
```diff
-@Entity(tableName = "event_district_points", primaryKeys = ["eventKey", "teamKey"])
+@Entity(tableName = "event_district_points", primaryKeys = ["eventKey", "teamKey", "source"])
 data class EventDistrictPointsEntity(
     val eventKey: String,
     val teamKey: String,
+    val source: String, // "district" | "regional"
     val qualPoints: Int,
     ...
 )
```

**DAO** (`EventDistrictPointsDao.kt`):
```diff
-@Query("SELECT * FROM event_district_points WHERE eventKey = :eventKey ORDER BY total DESC")
-fun observeByEvent(eventKey: String): Flow<List<EventDistrictPointsEntity>>
+@Query("SELECT * FROM event_district_points WHERE eventKey = :eventKey AND source = :source ORDER BY total DESC")
+fun observeByEvent(eventKey: String, source: String): Flow<List<EventDistrictPointsEntity>>

-@Query("DELETE FROM event_district_points WHERE eventKey = :eventKey")
-suspend fun deleteByEvent(eventKey: String)
+@Query("DELETE FROM event_district_points WHERE eventKey = :eventKey AND source = :source")
+suspend fun deleteByEvent(eventKey: String, source: String)
```

**Mapper** (`Mappers.kt:411`): add `source` param to `toEntity`.

**Repository** (`EventRepository.kt`): district methods pass `"district"`,
regional methods pass `"regional"`. The two no longer collide; each refresh only
clears its own rows.

**Database** (`TBADatabase.kt:63`): `version = 16` → `17`.

After this, `TeamEventDetailViewModel`'s two maps become genuinely distinct, and
`EventDetailViewModel`'s unconditional dual-refresh is harmless (each endpoint
owns its own rows).

## Alternative (stopgap only)

Mirror the observe-side conditional on the refresh side in `EventDetailViewModel`
(only refresh the endpoint matching `event.district`). One-line-ish, no schema
change — but it does **not** fix `TeamEventDetailViewModel`'s conflation, and it
re-introduces the `event.district` branch in three more places. The discriminator
is the real fix; this is only worth it if we need a same-day hotfix before the
migration lands.

## Test plan

- Unit/integration test: seed district rows for a district event, call
  `refreshEventRegionalPoints` (empty response) → district rows survive.
- Emulator: open a district event's Points tab and a regional event's Points
  tab, pull-to-refresh repeatedly, confirm neither blanks.

## Scope / risk

- One PR. Touches entity, DAO, mapper, repository (4 methods), DB version, and
  the two ViewModels' point wiring.
- Risk: low. Destructive cache migration only; no user data.

## Open questions

- Should `getEventRegionalPoints` actually 404 (vs empty 200) for district
  events? If TBA 404s, the `catch` currently protects us by luck; the
  discriminator removes the reliance on that behavior either way.
