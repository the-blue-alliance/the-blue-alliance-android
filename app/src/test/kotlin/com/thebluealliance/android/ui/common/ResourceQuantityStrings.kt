package com.thebluealliance.android.ui.common

import com.thebluealliance.android.R
import java.io.File
import java.util.Locale

/**
 * [QuantityStrings] read from the app's real `<plurals>` in strings.xml, picking items by
 * Android's English rule: `one` for exactly 1, `other` for everything else, zero included.
 */
object ResourceQuantityStrings : QuantityStrings {
    private val plurals: Map<String, Map<String, String>> by lazy {
        val xml = File("src/main/res/values/strings.xml").readText()
        val item = Regex("""<item quantity="(\w+)">(.*?)</item>""")
        Regex("""<plurals name="(\w+)">(.*?)</plurals>""", RegexOption.DOT_MATCHES_ALL)
            .findAll(xml)
            .associate { block ->
                block.groupValues[1] to
                    item.findAll(block.groupValues[2]).associate {
                        it.groupValues[1] to it.groupValues[2].replace("\\'", "'")
                    }
            }
    }

    private fun nameOf(id: Int): String =
        R.plurals::class.java.fields
            .first {
                it.getInt(null) == id
            }.name

    override fun get(
        id: Int,
        count: Int,
    ): String {
        val items = plurals.getValue(nameOf(id))
        val text = items[if (count == 1) "one" else "other"] ?: items.getValue("other")
        return String.format(Locale.ROOT, text, count)
    }
}
