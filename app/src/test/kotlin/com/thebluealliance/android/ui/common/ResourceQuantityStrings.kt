package com.thebluealliance.android.ui.common

import com.thebluealliance.android.R
import org.w3c.dom.Element
import java.io.File
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.math.abs

/**
 * [QuantityStrings] read from the app's real `<plurals>` in strings.xml, picking items by
 * Android's English rule: `one` for 1 (or -1), `other` for everything else, zero included.
 */
object ResourceQuantityStrings : QuantityStrings {
    private val plurals: Map<String, Map<String, String>> by lazy {
        val doc =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                File("src/main/res/values/strings.xml"),
            )
        val blocks = doc.getElementsByTagName("plurals")
        (0 until blocks.length).associate { i ->
            val block = blocks.item(i) as Element
            val items = block.getElementsByTagName("item")
            block.getAttribute("name") to
                (0 until items.length).associate { j ->
                    val item = items.item(j) as Element
                    item.getAttribute("quantity") to item.textContent.trim().replace("\\'", "'")
                }
        }
    }

    /** The parsed `quantity` to text items of the `<plurals>` named [name]. */
    fun itemsOf(name: String): Map<String, String> = plurals.getValue(name)

    private fun nameOf(id: Int): String =
        R.plurals::class.java.fields
            .first {
                it.getInt(null) == id
            }.name

    override fun get(
        id: Int,
        count: Int,
    ): String {
        val items = itemsOf(nameOf(id))
        return String.format(
            Locale.ROOT,
            items.getValue(
                if (abs(count) ==
                    1
                ) {
                    "one"
                } else {
                    "other"
                },
            ),
            count,
        )
    }
}
