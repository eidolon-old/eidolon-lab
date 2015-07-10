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

/**
 * Parser
 *
 * @author Elliot Wright <elliot@elliotwright.co>
 */
public class Parser(val input: String) {
    private val context = ParserContext(input.toCharArray())

    private final val SOT_CHAR = '\u0002'
    private final val EOT_CHAR = '\u0003'


    fun parse() {
        read()
        skipWhitespace()

        while (!isEndOfText()) {
            readItem()
            skipWhitespace()
        }

        println("End of input")
    }

    private fun readItem() {
        val property = readProperty()

        require(':')
        require(' ')
        skipWhitespace()

        val value = readValue()

        read()
        skipWhitespace()
        // todo: require either comma or new line

        println(property)
        println(value)
    }

    private fun readProperty(): String {
        startCapture()
        read()
        while (context.next != ':') {
            if (isAlphaNumeric() || isUnderscore()) {
                read()
            } else {
                expected("an alphanumeric string, or an underscore, got '" + context.current + "'")
            }
        }
        return endCapture()
    }

    private fun readValue(): String {
        var result:String = ""

        when (context.current) {
            '"' -> {
                read()
                startCapture()
                while (context.next != '"') {
                    read()
                }
                result = endCapture()
                require('"')
            }
            '{' -> {
                read()
                startCapture()
                while (context.next != '}') {
                    read()
                }
                result = endCapture()
                require('}')
            }
            else -> {
                // throw error("unexpected character")
            }
        }

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

    private fun isNewLine(): Boolean = isNewLine(context.current)

    private fun isSpace(character: Char): Boolean {
        return character == ' '
    }

    private fun isSpace(): Boolean = isSpace(context.current)

    private fun isTab(character: Char): Boolean {
        return character == '\t'
    }

    private fun isTab(): Boolean = isTab(context.current)

    private fun isUnderscore(character: Char): Boolean {
        return character == '_'
    }

    private fun isUnderscore(): Boolean = isUnderscore(context.current)

    private fun isWhitespace(character: Char): Boolean {
        return isNewLine(character) || isSpace(character) || isTab(character)
    }

    private fun isWhitespace(): Boolean = isWhitespace(context.current)

    private fun skipWhitespace() {
        while (isWhitespace()) {
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

        if (context.capturing) {
            context.captureBuffer.append(context.current)
        }
    }

    private fun startCapture() {
        context.capturing = true
        context.captureBuffer = StringBuilder()
        context.captureBuffer.append(context.current)
    }

    private fun endCapture(): String {
        context.capturing = false

        return context.captureBuffer.toString()
    }

    private fun require(character: Char) {
        read()
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

        var capturing: Boolean = false,
        var captureBuffer: StringBuilder = StringBuilder(),

        var cursor: Int = 0,
        var savedCursor: Int = 0,

        var line: Int = 1,
        var lineCursor: Int = 0) {

        fun restoreCursor(): Unit {
            cursor = savedCursor
        }

        fun saveCursor(): Unit {
            savedCursor = cursor
        }
    }

    private class ParserException(
        message: String,
        line: Int,
        column: Int
    ) : RuntimeException(message + " @ " + line + ":" + column)
}
