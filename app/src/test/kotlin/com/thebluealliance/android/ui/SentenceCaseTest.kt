package com.thebluealliance.android.ui

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Enforces STYLE_GUIDE.md's sentence case on UI text in the app, wear and tv modules.
 *
 * It reads every `src/main/res/values/strings.xml` (English only), and the string literals at Kotlin UI call
 * sites in `src/main/kotlin`: the first string argument of a label-taking call ([UI_CALL]), a
 * named UI argument ([UI_PARAM]), an enum entry's label lambda, both strings of a
 * `"key" to "Label"` pair, and both branches of an `if` in any of those. A capital on any word
 * but a sentence's first fails unless the word is in `sentence-case-allowlist.txt` (test
 * resources), which mirrors STYLE_GUIDE.md's proper-noun list; a `#` in an allowlist phrase
 * stands for a number or placeholder ("Week #"). Words with two or more capitals (OPR, myTBA,
 * YouTube) and words with digits (Q42, R2) count as acronyms. A line holding
 * `sentence-case-exempt` is skipped: use it only for copies of server text or names built
 * from it.
 */
class SentenceCaseTest {
    @Test
    fun `ui strings use sentence case`() {
        val root = projectRoot()
        val allowlist = Allowlist.load()
        val violations =
            MODULES.flatMap { module ->
                val src = File(root, "$module/src/main")
                val xml =
                    src
                        .resolve("res")
                        .walkTopDown()
                        .filter {
                            it.name == "strings.xml" && it.parentFile.name == "values"
                        }.flatMap { stringsXmlEntries(it) }
                        .toList()
                val kotlin =
                    src
                        .resolve("kotlin")
                        .walkTopDown()
                        .filter { it.extension == "kt" }
                        .flatMap { uiLiterals(it) }
                        .toList()
                check(xml.size + kotlin.size > 0) { "No UI strings found in $src" }
                (xml + kotlin).flatMap { entry ->
                    titleCaseWords(entry.text, allowlist).map { word ->
                        val text = entry.text.replace(PLACEHOLDER, '…')
                        "${entry.file.relativeTo(root)}:${entry.line}: \"$word\" in \"$text\""
                    }
                }
            }
        if (violations.isNotEmpty()) {
            fail<Unit>(
                "${violations.size} UI strings break sentence case (STYLE_GUIDE.md). " +
                    "Lowercase the word, or add a proper noun to " +
                    "sentence-case-allowlist.txt and STYLE_GUIDE.md:\n" +
                    violations.joinToString("\n"),
            )
        }
    }

    @Test
    fun `flags a capital after the first word`() {
        assertEquals(listOf("Breakdown"), titleCaseWords("Score Breakdown", Allowlist.load()))
    }

    @Test
    fun `allows sentence case, acronyms and placeholders`() {
        val allowlist = Allowlist.load()
        listOf(
            "Score breakdown",
            "Learn more about OPR",
            "Component OPRs",
            "Watch on YouTube",
            "Team %1\$s not found",
            "Offline. Updates when connected.",
            "Regionals · Happening now",
            "Advances to R2-7",
            "SAMPLE DATA",
            "Upcoming this week — Week 3",
            "via Week \u0000 pool",
            "I'm not sure, I've checked",
            "Open in Google's app",
        ).forEach { assertEquals(emptyList<String>(), titleCaseWords(it, allowlist), it) }
    }

    @Test
    fun `allows listed proper nouns and phrases`() {
        val allowlist = Allowlist.load()
        listOf(
            "Sign in with Google",
            "Red alliance",
            "Qualified via Regional Pool",
            "Thanks to the Blue Alliance",
            "Won the FIRST Impact Award",
            "Team @ Event",
        ).forEach { assertEquals(emptyList<String>(), titleCaseWords(it, allowlist), it) }
        assertEquals(listOf("Pool"), titleCaseWords("Week 3 Pool", allowlist))
    }

    @Test
    fun `flags capitals next to numbers, percents and placeholders`() {
        val allowlist = Allowlist.load()
        assertEquals(listOf("Win"), titleCaseWords("100% Win rate", allowlist))
        assertEquals(listOf("Title"), titleCaseWords("\u0000Title", allowlist))
        assertEquals(listOf("Regional"), titleCaseWords("2026 Regional", allowlist))
        assertEquals(listOf("Week"), titleCaseWords("Upcoming this Week", allowlist))
        assertEquals(listOf("Maps"), titleCaseWords("Pit Maps", allowlist))
    }

    @Test
    fun `finds literals at ui call sites only`() {
        val source =
            """
            Text("Match Video")
            SectionHeader(label = if (x) "Recent Matches" else "Other Matches")
            val key = "Not A Label"
            Log.d(TAG, "Not UI Either")
            NotificationChannel(
                CHANNEL,
                "Match Alerts",
            )
            "autoPoints" to "Auto Points"
            title = "Server Copy" // sentence-case-exempt
            // Text("In A Comment")
            Text("${'$'}{team.name} Pit Map")
            "Upper Bracket" to "Lower Bracket"
            TopBarTitle(
                if (title.isNotEmpty()) {
                    "Event Pit Map"
                } else {
                    "Pit Map"
                },
            )
            POINTS({ if (it != null) "District Points" else "Regional Points" }),
            text = if (x) "Teams: ${'$'}{list.joinToString(", ")}" else "No Teams Found"
            label = if (a) "One A" else if (b) "Two B" else "Three C"
            """.trimIndent()
        val file = File.createTempFile("ui-literals", ".kt").apply { writeText(source) }
        try {
            assertEquals(
                listOf(
                    1 to "Match Video",
                    2 to "Recent Matches",
                    2 to "Other Matches",
                    7 to "Match Alerts",
                    9 to "autoPoints",
                    9 to "Auto Points",
                    12 to "\u0000 Pit Map",
                    13 to "Upper Bracket",
                    13 to "Lower Bracket",
                    16 to "Event Pit Map",
                    18 to "Pit Map",
                    21 to "District Points",
                    21 to "Regional Points",
                    22 to "Teams: \u0000",
                    22 to "No Teams Found",
                    23 to "One A",
                    23 to "Two B",
                    23 to "Three C",
                ),
                uiLiterals(file).map { it.line to it.text }.toList(),
            )
        } finally {
            file.delete()
        }
    }

    private data class Entry(
        val file: File,
        val line: Int,
        val text: String,
    )

    private class Allowlist(
        val words: Set<String>,
        val phrases: List<List<String>>,
    ) {
        companion object {
            fun load(): Allowlist {
                val lines =
                    checkNotNull(
                        SentenceCaseTest::class.java.getResource("/sentence-case-allowlist.txt"),
                    ).readText()
                        .lines()
                        .map { it.trim() }
                        .filter { it.isNotEmpty() && !it.startsWith("# ") && it != "#" }
                val (phrases, words) = lines.partition { ' ' in it }
                return Allowlist(words.toSet(), phrases.map { it.split(' ') })
            }
        }
    }

    private companion object {
        val MODULES = listOf("app", "wear", "tv")

        /** An optional `if (…) {` before a branch's string; conditions nest parentheses one deep. */
        const val IF = "(if\\s*\\([^()]*(\\([^()]*\\)[^()]*)*\\)\\s*\\{?\\s*)?$"

        /** Calls whose first string argument (after at most one plain argument) is UI text. */
        val UI_CALL =
            Regex(
                "\\b(Text|TopBarTitle|TooltipIconButton|SectionHeader|SummarySection|MoreItem|" +
                    "headerWords|EventSubSection|NotificationChannel|showSnackbar|makeText)" +
                    "\\s*\\(\\s*" +
                    "([A-Za-z_][\\w.]*\\s*,\\s*)?$IF",
            )
        val UI_PARAM =
            Regex(
                "\\b(text|title|label|contentDescription|onClickLabel|description|" +
                    "stateDescription|placeholder|message)\\s*=\\s*\\{?\\s*$IF",
            )

        /** An enum entry whose label lambda is its argument, e.g. `INFO({ "Info" })`. */
        val ENUM_LABEL = Regex("\\b[A-Z][A-Z0-9_]+\\s*\\(\\s*\\{\\s*$IF")
        val PAIR_VALUE = Regex("\"\\s+to\\s+$")
        val PAIR_KEY = Regex("^\"\\s+to\\s+")
        val ELSE_BRANCH = Regex("\"\\s*\\}?\\s*else\\s*\\{?\\s*$IF")
        const val EXEMPT = "sentence-case-exempt"

        const val PLACEHOLDER = '\u0000'
        val FORMAT_ARG = Regex("%(\\d+\\$)?[-#+0,(]*\\d*(\\.\\d+)?[sSdfxXoeEgGcbBhHn%]")
        val URL = Regex("https?://\\S+")
        val TOKEN =
            Regex("\u0000|[0-9][A-Za-z0-9'’]*|[A-Za-z][A-Za-z0-9'’]*|[@&]|[.!?·—–|:\\n\\t]")
        val CONTRACTION = Regex("I['’][a-z]+")
        val POSSESSIVE = Regex("['’]s$")
        const val SENTENCE_BREAKS = ".!?·—–|:\n\t"

        fun projectRoot(): File {
            var dir: File? = File(System.getProperty("user.dir")).absoluteFile
            while (dir != null && !File(dir, "settings.gradle.kts").exists()) dir = dir.parentFile
            return checkNotNull(
                dir,
            ) { "settings.gradle.kts not found above ${System.getProperty("user.dir")}" }
        }

        /** The words in [text] that break sentence case. */
        fun titleCaseWords(
            text: String,
            allowlist: Allowlist,
        ): List<String> {
            val cleaned = URL.replace(FORMAT_ARG.replace(text, "$PLACEHOLDER"), " ")
            val tokens =
                TOKEN
                    .findAll(cleaned.replace("$PLACEHOLDER", " $PLACEHOLDER "))
                    .map { it.value.replace(POSSESSIVE, "") }
                    .toList()
            val exempt = BooleanArray(tokens.size)

            fun matches(
                token: String,
                word: String,
            ) = token == word || (word == "#" && (token[0].isDigit() || token[0] == PLACEHOLDER))
            allowlist.phrases.forEach { phrase ->
                for (i in 0..tokens.size - phrase.size) {
                    if (phrase.indices.all { matches(tokens[i + it], phrase[it]) }) {
                        phrase.indices.forEach { exempt[i + it] = true }
                    }
                }
            }
            val flagged = mutableListOf<String>()
            var sentenceStart = true
            tokens.forEachIndexed { i, token ->
                if (token.length == 1 && token[0] in SENTENCE_BREAKS) {
                    sentenceStart = true
                    return@forEachIndexed
                }
                val first = sentenceStart
                sentenceStart = false
                val acronym = token.count { it.isUpperCase() } > 1 || token.any { it.isDigit() }
                val ok =
                    first ||
                        exempt[i] ||
                        !token[0].isUpperCase() ||
                        token.length == 1 ||
                        acronym ||
                        CONTRACTION.matches(token) ||
                        token in allowlist.words
                if (!ok) flagged += token
            }
            return flagged
        }

        fun stringsXmlEntries(file: File): Sequence<Entry> {
            val lines = file.readLines()
            val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

            fun lineOf(name: String) = lines.indexOfFirst { "name=\"$name\"" in it } + 1
            val strings =
                doc.getElementsByTagName("string").let { nodes ->
                    (0 until nodes.length).map { nodes.item(it) as Element }
                }
            val items =
                doc.getElementsByTagName("item").let { nodes ->
                    (0 until nodes.length).map { nodes.item(it) as Element }
                }
            return (strings + items)
                .filter {
                    it.getAttribute("translatable") != "false" &&
                        (it.parentNode as? Element)?.getAttribute("translatable") != "false"
                }.map { element ->
                    val name =
                        element
                            .getAttribute(
                                "name",
                            ).ifEmpty { (element.parentNode as Element).getAttribute("name") }
                    Entry(
                        file,
                        lineOf(name),
                        element.textContent
                            .replace(Regex("\\s+"), " ")
                            .replace("\\'", "'")
                            .replace("\\n", "\n"),
                    )
                }.asSequence()
        }

        /** String literals at UI call sites in a Kotlin file, with templates as [PLACEHOLDER]. */
        fun uiLiterals(file: File): Sequence<Entry> {
            val src = file.readText()
            val lexed = KotlinStrings(src).lex()
            val lines = src.lines()
            var previousWasUi = false
            return lexed.literals
                .mapNotNull { literal ->
                    val context =
                        lexed.masked.substring(
                            maxOf(0, literal.start - 300),
                            literal.start,
                        )
                    val ui =
                        UI_CALL.containsMatchIn(context) ||
                            UI_PARAM.containsMatchIn(context) ||
                            ENUM_LABEL.containsMatchIn(context) ||
                            PAIR_VALUE.containsMatchIn(context) ||
                            PAIR_KEY.containsMatchIn(
                                lexed.masked.substring(
                                    literal.end - 1,
                                    minOf(
                                        src.length,
                                        literal.end + 40,
                                    ),
                                ),
                            ) ||
                            (
                                !literal.nested &&
                                    previousWasUi &&
                                    ELSE_BRANCH.containsMatchIn(context)
                            )
                    if (!literal.nested) previousWasUi = ui
                    val line = src.substring(0, literal.start).count { it == '\n' } + 1
                    if (ui && EXEMPT !in lines[line - 1]) Entry(file, line, literal.value) else null
                }.asSequence()
        }
    }

    /** A small Kotlin lexer: string literals, plus the source with literals and comments blanked. */
    private class KotlinStrings(
        private val src: String,
    ) {
        data class Literal(
            val start: Int,
            val end: Int,
            val value: String,
            val nested: Boolean,
        )

        class Result(
            val literals: List<Literal>,
            val masked: String,
        )

        private val masked = StringBuilder(src)
        private val literals = mutableListOf<Literal>()

        fun lex(): Result {
            code(0, nesting = 0)
            return Result(literals.sortedBy { it.start }, masked.toString())
        }

        /** Scans code from [from]; inside a template, stops after the closing brace. */
        private fun code(
            from: Int,
            nesting: Int,
        ): Int {
            var i = from
            var depth = 0
            while (i < src.length) {
                val c = src[i]
                when {
                    src.startsWith("//", i) -> {
                        val end = src.indexOf('\n', i).let { if (it < 0) src.length else it }
                        blank(i, end)
                        i = end
                    }
                    src.startsWith("/*", i) -> {
                        val end =
                            src.indexOf("*/", i + 2).let {
                                if (it <
                                    0
                                ) {
                                    src.length
                                } else {
                                    it + 2
                                }
                            }
                        blank(i, end)
                        i = end
                    }
                    src.startsWith("\"\"\"", i) -> i = string(i, raw = true, nesting)
                    c == '"' -> i = string(i, raw = false, nesting)
                    c == '\'' -> {
                        val end =
                            if (src.getOrNull(i + 1) ==
                                '\\'
                            ) {
                                src.indexOf('\'', i + 3)
                            } else {
                                i + 2
                            }
                        i = (if (end < 0) src.length else end) + 1
                    }
                    c == '{' -> {
                        depth++
                        i++
                    }
                    c == '}' -> {
                        i++
                        if (nesting > 0 && depth == 0) return i
                        depth--
                    }
                    else -> i++
                }
            }
            return i
        }

        private fun string(
            start: Int,
            raw: Boolean,
            nesting: Int,
        ): Int {
            val quote = if (raw) "\"\"\"" else "\""
            var i = start + quote.length
            var textStart = i
            val value = StringBuilder()
            while (i < src.length && !src.startsWith(quote, i)) {
                val c = src[i]
                when {
                    !raw && c == '\\' -> {
                        val next = src.getOrElse(i + 1) { ' ' }
                        if (next == 'u') {
                            value.append(src.substring(i + 2, i + 6).toInt(16).toChar())
                            i += 6
                        } else {
                            value.append(
                                when (next) {
                                    'n' -> '\n'
                                    't' -> '\t'
                                    else -> next
                                },
                            )
                            i += 2
                        }
                    }
                    src.startsWith("\${", i) -> {
                        value.append(PLACEHOLDER)
                        blank(textStart, i + 2)
                        i = code(i + 2, nesting + 1)
                        blank(i - 1, i)
                        textStart = i
                    }
                    c == '$' &&
                        src
                            .getOrNull(
                                i + 1,
                            )?.let { it.isLetter() || it == '_' } == true -> {
                        value.append(PLACEHOLDER)
                        i++
                        while (i < src.length && (src[i].isLetterOrDigit() || src[i] == '_')) i++
                    }
                    else -> {
                        value.append(c)
                        i++
                    }
                }
            }
            val end = minOf(src.length, i + quote.length)
            blank(textStart, i)
            literals += Literal(start, end, value.toString(), nested = nesting > 0)
            return end
        }

        private fun blank(
            from: Int,
            to: Int,
        ) {
            for (j in from until to) if (masked[j] != '\n') masked.setCharAt(j, ' ')
        }
    }
}
