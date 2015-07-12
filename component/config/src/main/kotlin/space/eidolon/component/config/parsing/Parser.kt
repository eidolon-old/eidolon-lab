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

package space.eidolon.component.config.parsing

import space.eidolon.component.config.ConfigObject
import space.eidolon.component.config.ConfigString
import space.eidolon.component.config.ConfigTrue
import space.eidolon.component.config.ConfigValue

/**
 * Parser
 *
 * @author Elliot Wright <elliot@elliotwright.co>
 */
public class Parser(val input: String) {
    private val context = ParserContext(input.trim().toCharArray())

    private final val SOT_CHAR = '\u0002'
    private final val EOT_CHAR = '\u0003'


    fun parse(): ConfigObject {
        read()

        return readDocument()
    }

    private fun readDocument(): ConfigObject {
        val result = ConfigObject()

        while (!isEndOfText()) {
            val kvp = readKvp()

            result.put(kvp.first, kvp.second)
        }

        require(EOT_CHAR)

        return result
    }

    private fun readProperty(): String {
        context.clearCaptureBuffer()
        skipWhitespace()

        while (context.current != ':') {
            if (isAlphaNumeric() || isUnderscore()) {
                context.captureBuffer.append(context.current)
                read()
            } else {
                expected("an alphanumeric string, or an underscore, got '" + context.current + "'")
            }
        }

        return context.flushCaptureBuffer()
    }

    private fun readKvp(): Pair<String, ConfigValue> {
        val property = readProperty()

        require(':')

        read()
        skipWhitespace()

        val value = readValue()

        read()
        skipWhitespace()

        return Pair(property, value)
    }

    private fun readValue(): ConfigValue {
        context.clearCaptureBuffer()

        return when (context.current) {
            '"' -> readString()
            '{' -> readObject()
            else -> {
                 throw error("unexpected character")
            }
        }
    }

    private fun readString(): ConfigString {
        read()

        while (context.current != '"') {
            context.captureBuffer.append(context.current)
            read()
        }

        require('"')

        return ConfigString(context.flushCaptureBuffer())
    }

    private fun readObject(): ConfigObject {
        val result = ConfigObject()

        read()
        while (context.current != '}') {
            val kvp = readKvp()

            result.put(kvp.first, kvp.second)
        }
        require('}')

        return result
    }

    private fun isEndOfText(): Boolean {
        return context.current == EOT_CHAR
    }

    private fun isAlphaNumeric(): Boolean {
        return context.current.isLetterOrDigit()
    }

    private fun isNewLine(character: Char): Boolean {
        return character == '\n' || character == '\r'
    }

    private fun isSpace(character: Char): Boolean {
        return character == ' '
    }

    private fun isTab(character: Char): Boolean {
        return character == '\t'
    }

    private fun isUnderscore(character: Char): Boolean {
        return character == '_'
    }

    private fun isUnderscore(): Boolean = isUnderscore(context.current)

    private fun isWhitespace(character: Char): Boolean {
        return isNewLine(character) || isSpace(character) || isTab(character)
    }

    private fun skipWhitespace() {
        while (isWhitespace(context.current)) {
            read()
        }
    }

    private fun read() {
        if (isEndOfText()) {
            return
        }

        if (context.cursor >= context.buffer.count()) {
            context.current = EOT_CHAR
            return
        }

        if ((context.cursor - 1) >= 0) {
            context.prev = context.buffer.get(context.cursor - 1)
        } else {
            context.prev = SOT_CHAR
        }

        if ((context.cursor + 1) < context.buffer.count()) {
            context.next = context.buffer.get(context.cursor + 1)
        } else {
            context.next = EOT_CHAR
        }

        if (isNewLine(context.prev)) {
            context.line++
            context.lineCursor = 0
        }

        context.current = context.buffer.get(context.cursor)
        context.cursor++
        context.lineCursor++
    }

    private fun require(character: Char) {
        if (context.current != character) {
            expected(character.toString())
        }
    }

    private fun expected(message: String) {
        throw error("Expected " + message)
    }

    private fun error(message: String): ParserException {
        return ParserException(
            message,
            context.line,
            context.lineCursor
        )
    }


    private data inner class ParserContext(
        val buffer: CharArray,

        var prev: Char = SOT_CHAR,
        var current: Char = SOT_CHAR,
        var next: Char = EOT_CHAR,

        var captureBuffer: StringBuilder = StringBuilder(),

        var cursor: Int = 0,

        var line: Int = 1,
        var lineCursor: Int = 0) {

        fun clearCaptureBuffer() {
            captureBuffer.setLength(0)
        }

        fun flushCaptureBuffer(): String {
            val buffer = captureBuffer.toString()
            clearCaptureBuffer()
            return buffer
        }
    }

    private class ParserException(
        message: String,
        line: Int,
        column: Int
    ) : RuntimeException(message + " @ " + line + ":" + column)
}
