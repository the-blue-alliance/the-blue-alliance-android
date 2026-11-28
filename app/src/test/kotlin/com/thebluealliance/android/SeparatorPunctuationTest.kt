package com.thebluealliance.android

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

/** STYLE_GUIDE.md "Dashes and separators": our own text never separates with " - " or an em dash. */
class SeparatorPunctuationTest {
    private val repoRoot: File =
        generateSequence(File("").absoluteFile) { it.parentFile }
            .first { File(it, "settings.gradle.kts").isFile && File(it, "wear").isDirectory }

    // Intentional uses, as (path suffix, literal text): keep this list short and explained.
    private val allowlist =
        listOf(
            // TV splits server event names ("District - Event") onto two lines.
            "tv/src/main/kotlin/com/thebluealliance/android/tv/ui/events/EventsScreen.kt" to " - ",
        )

    /** A string literal's text, with `${…}` templates blanked to "X", and the line it starts on. */
    data class Literal(
        val line: Int,
        val text: String,
    )

    private val hyphenSeparator = Regex("""[\s ]-[\s ]|^-[\s ]|[\s ]-$""")

    private fun offends(literal: Literal): Boolean = offends(literal.text)

    private fun offends(text: String): Boolean =
        hyphenSeparator.containsMatchIn(text) || (text.contains('—') && text != "—")

    @Test
    fun `own text uses a middle dot or en dash, not a spaced hyphen or em dash, as a separator`() {
        val violations = mutableListOf<String>()
        for (module in listOf("app", "wear", "tv")) {
            File(repoRoot, "$module/src")
                .walkTopDown()
                .onEnter { it.name !in setOf("test", "androidTest", "build") }
                .filter {
                    it.isFile &&
                        (
                            it.extension == "kt" ||
                                (it.extension == "xml" && "/res/" in it.invariantSeparatorsPath)
                        )
                }.forEach { file ->
                    val rel = file.relativeTo(repoRoot).invariantSeparatorsPath
                    val source = file.readText()
                    val literals =
                        if (file.extension ==
                            "kt"
                        ) {
                            kotlinLiterals(source)
                        } else {
                            xmlTexts(source)
                        }
                    literals
                        .filter(::offends)
                        .filterNot { lit ->
                            allowlist.any { (path, text) ->
                                rel.endsWith(path) &&
                                    lit.text == text
                            }
                        }.forEach { violations += "$rel:${it.line}: \"${it.text}\"" }
                }
        }
        assertTrue(violations.isEmpty()) {
            "Use \" · \" between pieces of info and \" – \" for ranges (STYLE_GUIDE.md):\n" +
                violations.joinToString("\n")
        }
    }

    @Test
    fun `scanner flags separators in templates, joins and nested templates`() {
        val d = "$"
        listOf(
            """val t = "$d{team.number} - $d{team.nickname}"""",
            """val t = "Week — ${d}week"""",
            """val t = list.joinToString(" — ")""",
            """val t = "$d{n}$d{nick?.let { " - ${d}it" } ?: ""}"""",
            "val t = \"a\\u00A0-\\u00A0b\"",
            "val t = \"a\\t-\\tb\"",
            "val t = \"\"\"a — b\"\"\"",
        ).forEach { src -> assertTrue(kotlinLiterals(src).any(::offends), src) }
        assertTrue(xmlTexts("<string name=\"x\">Offline\n  — %1${d}s</string>").any(::offends))
    }

    @Test
    fun `scanner ignores math, codes, the no-value mark, comments and log calls`() {
        val d = "$"
        listOf(
            """Text("Show $d{selectedYear - 1} districts")""",
            """val code = "R2-8" // a - b""",
            """const val NO_VALUE = "—"""",
            "/* a - b\n \"c - d\" */ val x = 1",
            "Log.d(\n    TAG,\n    \"updated — no event\",\n)",
            """val c = '"'; val s = "a-b"""",
        ).forEach { src -> assertTrue(kotlinLiterals(src).none(::offends), src) }
        assertTrue(xmlTexts("<!-- a — b -->\n<string name=\"x\">R2-8</string>").none(::offends))
    }

    @Test
    fun `scanner reports the line a literal starts on`() {
        assertEquals(
            listOf(
                3,
            ),
            kotlinLiterals("val a = 1\n\nval b = \"x - y\"").filter(::offends).map {
                it.line
            },
        )
    }

    /** Every string literal in Kotlin [source], skipping comments and `Log.x(…)` calls. */
    private fun kotlinLiterals(source: String): List<Literal> = KotlinScanner(source).scan()

    private class KotlinScanner(
        private val s: String,
    ) {
        private val out = mutableListOf<Literal>()
        private val newlines = s.indices.filter { s[it] == '\n' }
        private val logCall = Regex("""^Log\.(v|d|i|w|e|wtf)\(""")

        fun scan(): List<Literal> {
            var i = 0
            while (i < s.length) i = readCode(i, null, out)
            return out
        }

        private fun lineOf(pos: Int): Int =
            newlines.binarySearch(pos).let { if (it < 0) -it - 1 else it } + 1

        // Reads a string whose opening quote is at [start]; returns the index after it.
        private fun readString(
            start: Int,
            raw: Boolean,
            sink: MutableList<Literal>?,
        ): Int {
            val sb = StringBuilder()
            var j = start + if (raw) 3 else 1
            while (j < s.length) {
                when {
                    raw && s.startsWith("\"\"\"", j) -> {
                        j += 3
                        break
                    }
                    !raw && s[j] == '"' -> {
                        j++
                        break
                    }
                    !raw && s[j] == '\\' && j + 1 < s.length -> {
                        val c = s[j + 1]
                        if (c == 'u' && j + 6 <= s.length) {
                            sb.append(s.substring(j + 2, j + 6).toIntOrNull(16)?.toChar() ?: '?')
                            j += 6
                        } else {
                            sb.append(if (c in "tnr") ' ' else c)
                            j += 2
                        }
                    }
                    s.startsWith("\${", j) -> {
                        j = readCode(j + 2, '}', sink)
                        sb.append('X')
                    }
                    else -> sb.append(s[j++])
                }
            }
            sink?.add(Literal(lineOf(start), sb.toString()))
            return j
        }

        // Reads code from [start]. With a [close] bracket, stops after the matching one; otherwise
        // reads one token. Nested literals go to [sink]; a null sink drops them (log calls).
        private fun readCode(
            start: Int,
            close: Char?,
            sink: MutableList<Literal>?,
        ): Int {
            var j = start
            var depth = 0
            do {
                if (j >= s.length) return j
                val c = s[j]
                when {
                    s.startsWith("//", j) ->
                        j =
                            s.indexOf('\n', j).let { if (it < 0) s.length else it }
                    s.startsWith("/*", j) ->
                        j =
                            s.indexOf("*/", j + 2).let { if (it < 0) s.length else it + 2 }
                    s.startsWith("\"\"\"", j) -> j = readString(j, raw = true, sink = sink)
                    c == '"' -> j = readString(j, raw = false, sink = sink)
                    c == '\'' -> {
                        val end = s.indexOf('\'', j + if (s.getOrNull(j + 1) == '\\') 3 else 2)
                        j = if (end < 0) s.length else end + 1
                    }
                    c == 'L' &&
                        (j == 0 || !s[j - 1].isLetterOrDigit()) &&
                        logCall.containsMatchIn(s.substring(j, minOf(j + 8, s.length))) ->
                        j = readCode(s.indexOf('(', j) + 1, ')', null)
                    close != null && (c == '{' || c == '(') -> {
                        depth++
                        j++
                    }
                    close != null && (c == '}' || c == ')') -> {
                        if (depth == 0) return j + 1
                        depth--
                        j++
                    }
                    else -> j++
                }
            } while (close != null)
            return j
        }
    }

    /** `<string>` bodies and `android:text` values in XML [source], outside comments, with entities decoded. */
    private fun xmlTexts(source: String): List<Literal> {
        val s =
            source.replace(
                Regex("<!--.*?-->", RegexOption.DOT_MATCHES_ALL),
            ) { m -> m.value.replace(Regex("[^\n]"), " ") }
        val bodies =
            Regex(
                """<string\b[^>]*>(.*?)</string>""",
                RegexOption.DOT_MATCHES_ALL,
            ).findAll(s)
        val attrs = Regex("""android:text="([^"]*)"""").findAll(s)
        return (bodies + attrs)
            .map { m ->
                val group = m.groups[1]!!
                val text =
                    group.value.replace('\n', ' ').replace(Regex("""&#(x?)([0-9a-fA-F]+);""")) {
                        it.groupValues[2]
                            .toInt(
                                if (it.groupValues[1] ==
                                    "x"
                                ) {
                                    16
                                } else {
                                    10
                                },
                            ).toChar()
                            .toString()
                    }
                Literal(s.substring(0, group.range.first).count { it == '\n' } + 1, text)
            }.toList()
    }
}
