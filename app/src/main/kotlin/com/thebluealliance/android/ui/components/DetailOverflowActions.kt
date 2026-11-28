package com.thebluealliance.android.ui.components

/**
 * The More menu of an Event or Team page: [search] first, since it leaves the page rather than
 * acting on it, then [share] and [pinShortcut]. A [notFound] page has nothing to pin, so it keeps
 * Search, which then shows as an icon on its own.
 */
internal fun detailOverflowActions(
    share: TopBarAction?,
    search: TopBarAction,
    pinShortcut: TopBarAction?,
    notFound: Boolean,
): List<TopBarAction> =
    buildList {
        add(search)
        share?.let(::add)
        if (!notFound) pinShortcut?.let(::add)
    }
