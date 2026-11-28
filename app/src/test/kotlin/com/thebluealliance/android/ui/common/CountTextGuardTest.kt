package com.thebluealliance.android.ui.common

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

/** Count text goes through quantity strings, so "1 points" can't come back. */
class CountTextGuardTest {
    // Unit tests run with the app module as the working directory.
    private val roots = listOf("src/main", "../wear/src/main", "../tv/src/main").map(::File)

    private val nouns =
        "teams|matches|events|awards|results|favorites|points|pts|wins|losses|ties|" +
            "contributions|contributors|sponsors|districts|photos|videos|webcasts|alliances|" +
            "picks|notifications|subscriptions|seconds|minutes|mins|hours|hrs|days|weeks"

    // An interpolated value, up to one word, then a plural noun: "$n teams", "$n total points".
    private val countTemplate = Regex("""\$(\{[^}]*\}|\w+) (?:\w+ )?($nouns)\b""")

    // An expression `if (n == 1) a else b`, or `n == "1"`: English plurals by hand.
    private val handRolled = Regex("""\bif \([\w.]+ == 1\) [^{]|[\w.]+ == "1"""")

    private val formatCount = Regex("""%(\d+\$)?d (?:\w+ )?($nouns)\b""")

    private val stringElement = Regex("""<string\b[^>]*>(.*?)</string>""")

    // File name to a snippet of a line that is not count text.
    private val allowed =
        listOf(
            // A log line: "favorites=" names a field.
            "MyTBARepository.kt" to "favorites=\${response.favorites.size}",
        )

    private fun File.kotlinSources(): Sequence<File> =
        walkTopDown().filter { it.isFile && it.extension == "kt" && "/build/" !in it.path }

    private fun isComment(line: String): Boolean {
        val trimmed = line.trimStart()
        return trimmed.startsWith("//") || trimmed.startsWith("*")
    }

    // "Show $year events" names a year, not a count.
    private fun hasCountTemplate(line: String): Boolean =
        countTemplate.findAll(line).any { !it.groupValues[1].contains("year", ignoreCase = true) }

    private fun isCountText(line: String): Boolean =
        hasCountTemplate(line) || handRolled.containsMatchIn(line)

    private fun isAllowed(
        file: File,
        line: String,
    ): Boolean = allowed.any { (name, snippet) -> file.name == name && snippet in line }

    private fun hits(
        files: Sequence<File>,
        bad: (File, String) -> Boolean,
    ): List<String> =
        files.toList().flatMap { file ->
            file.readLines().mapIndexedNotNull { i, line ->
                "${file.path}:${i + 1}: ${line.trim()}".takeIf { bad(file, line) }
            }
        }

    @Test
    fun `kotlin sources build no count text by hand`() {
        val found =
            hits(roots.asSequence().flatMap { it.kotlinSources() }) { file, line ->
                !isComment(line) && !isAllowed(file, line) && isCountText(line)
            }
        assertTrue(found.isEmpty(), "Use <plurals> for count text:\n" + found.joinToString("\n"))
    }

    @Test
    fun `string resources keep counts in plurals`() {
        val files =
            roots.asSequence().flatMap { root ->
                root.walkTopDown().filter { it.isFile && it.name == "strings.xml" }
            }
        val found =
            hits(files) { _, line ->
                stringElement.findAll(line).any { formatCount.containsMatchIn(it.groupValues[1]) }
            }
        assertTrue(
            found.isEmpty(),
            "Move counted strings into <plurals>:\n" + found.joinToString("\n"),
        )
    }

    @Test
    fun `patterns flag counted nouns and skip years`() {
        assertTrue(isCountText("\"\$n teams\""))
        assertTrue(isCountText("\"\${points.total} pts\""))
        assertTrue(isCountText("\"\$total total points\""))
        assertTrue(isCountText("if (count == 1) \"match\" else \"matches\""))
        assertFalse(isCountText("\"Show \$year events\""))
        assertFalse(isCountText("\"Show \${selectedYear - 1} districts\""))
        assertFalse(isCountText("if (size == 1) {"))
        assertTrue(formatCount.containsMatchIn("%1\$d teams"))
        assertFalse(formatCount.containsMatchIn("Day %1\$d of %2\$d"))
    }

    @Test
    fun `guard sees every module's sources`() {
        roots.forEach { assertTrue(it.kotlinSources().any(), "No Kotlin sources under $it") }
    }
}
