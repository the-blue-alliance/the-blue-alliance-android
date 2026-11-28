package com.thebluealliance.android

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

/** STYLE_GUIDE.md "Dashes and separators": our own text never separates with " - " or an em dash. */
class SeparatorPunctuationTest {
    private val repoRoot: File =
        generateSequence(File("").absoluteFile) { it.parentFile }
            .first { File(it, "settings.gradle.kts").isFile && File(it, "wear").isDirectory }

    // Intentional uses, as (path suffix, line fragment): keep this list short and explained.
    private val allowlist =
        listOf(
            // TV splits server event names ("District - Event") onto two lines.
            "tv/src/main/kotlin/com/thebluealliance/android/tv/ui/events/EventsScreen.kt" to
                "displayName.replace(\" - \"",
        )

    private val hyphenSeparator = Regex("""\s-\s|^-\s|\s-$""")

    private fun offends(text: String): Boolean =
        hyphenSeparator.containsMatchIn(text) || (text.contains('—') && text.trim() != "—")

    @Test
    fun `own text uses a middle dot or en dash, not a spaced hyphen or em dash, as a separator`() {
        val violations = mutableListOf<String>()
        for (module in listOf("app", "wear", "tv")) {
            val src = File(repoRoot, "$module/src")
            src
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
                    val isKotlin = file.extension == "kt"
                    file.readLines().forEachIndexed { index, line ->
                        val texts = if (isKotlin) kotlinLiterals(line) else xmlTexts(line)
                        val allowed =
                            allowlist.any { (path, fragment) ->
                                rel.endsWith(path) && line.contains(fragment)
                            }
                        if (texts.any(::offends) && !allowed) {
                            violations += "$rel:${index + 1}: ${line.trim()}"
                        }
                    }
                }
        }
        assertTrue(violations.isEmpty()) {
            "Use \" · \" between pieces of info and \" – \" for ranges (STYLE_GUIDE.md):\n" +
                violations.joinToString("\n")
        }
    }

    @Test
    fun `scanner flags separators and ignores math, hyphenated tokens and the no-value mark`() {
        assertTrue(
            kotlinLiterals(
                """text = "${'$'}{team.number} - ${'$'}{team.nickname}",""",
            ).any(::offends),
        )
        assertTrue(kotlinLiterals("""label = "Week — ${'$'}week"""").any(::offends))
        assertTrue(xmlTexts("""<string name="x">Offline — %1${'$'}s</string>""").any(::offends))
        assertTrue(
            kotlinLiterals("""Text("Show ${'$'}{selectedYear - 1} districts")""").none(::offends),
        )
        assertTrue(kotlinLiterals("""val code = "R2-8" // a - b""").none(::offends))
        assertTrue(kotlinLiterals("""const val NO_VALUE = "—"""").none(::offends))
        assertTrue(kotlinLiterals("""Log.d(TAG, "updated — no event")""").none(::offends))
    }

    /** The literal text of each string on a Kotlin line, with `${…}` templates blanked out and escapes decoded. */
    private fun kotlinLiterals(line: String): List<String> {
        val trimmed = line.trimStart()
        if (trimmed.startsWith("//") ||
            trimmed.startsWith("*") ||
            trimmed.startsWith("/*")
        ) {
            return emptyList()
        }
        if (Regex("""\bLog\.[a-z]\(""").containsMatchIn(line)) return emptyList()
        val out = mutableListOf<String>()
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                c == '/' && line.startsWith("//", i) -> return out
                c == '\'' -> {
                    val from = if (line.getOrNull(i + 1) == '\\') i + 3 else i + 2
                    i = line.indexOf('\'', from).let { if (it < 0) line.length else it }
                }
                c == '"' -> {
                    val sb = StringBuilder()
                    i++
                    while (i < line.length && line[i] != '"') {
                        when {
                            line[i] == '\\' && i + 1 < line.length -> {
                                if (line[i + 1] == 'u' && i + 6 <= line.length) {
                                    sb.append(
                                        line.substring(i + 2, i + 6).toIntOrNull(16)?.toChar()
                                            ?: '?',
                                    )
                                    i += 6
                                } else {
                                    sb.append(line[i + 1])
                                    i += 2
                                }
                            }
                            line.startsWith("\${", i) -> {
                                var depth = 0
                                while (i < line.length) {
                                    if (line[i] == '{') depth++
                                    if (line[i] == '}' && --depth == 0) break
                                    i++
                                }
                                sb.append('X')
                                i++
                            }
                            else -> sb.append(line[i++])
                        }
                    }
                    out += sb.toString()
                }
            }
            i++
        }
        return out
    }

    /** `<string>` bodies and `android:text` values on an XML line, with entities decoded. */
    private fun xmlTexts(line: String): List<String> {
        if (line.contains("<!--")) return emptyList()
        val bodies =
            Regex(
                """<string[^>]*>(.*?)</string>""",
            ).findAll(line).map { it.groupValues[1] }
        val attrs = Regex("""android:text="([^"]*)"""").findAll(line).map { it.groupValues[1] }
        return (bodies + attrs)
            .map { text ->
                text.replace(Regex("""&#(x?)([0-9a-fA-F]+);""")) {
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
            }.toList()
    }
}
