/**
 * This file is part of the "eidolon-lab" project.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the LICENSE is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package space.eidolon.component.config.util

/**
 * Allows multi-line strings to contain a margin character that is treated as
 * the start of the line and stripped from the string.
 *
 * Example usage:
 *
 * val c1 = """
 *     |string: "is a string"
 *     |object: {
 *     |    key: "value"
 *     |    int: 1234567
 *     |}""".trim().stripMargin()
 *
 * @param marginChar The character to act as margin
 * @return The string stripped of whitespace before the margin, and the margin itself
 */
fun String.stripMargin(marginChar: Char): String {
    val buffer = StringBuilder()
    val lines = this.lines()

    for (lineNo in lines.indices) {
        val line = lines[lineNo]
        val length = line.length()
        var index = 0
        var result = ""

        while (index < length && line.charAt(index) <= ' ') {
            index++
        }

        if (index < length && line.charAt(index) == marginChar) {
            result += line.substring(index + 1)
        } else {
            result += line
        }

        if ((lineNo + 1) != lines.count()) {
            result += '\n'
        }

        buffer.append(result)
    }

    return buffer.toString()
}

/**
 * Convenience method to use default margin character of '|' as margin
 *
 * @return The string stripped of whitespace before the margin, and the margin itself
 */
fun String.stripMargin(): String {
    return this.stripMargin('|')
}
