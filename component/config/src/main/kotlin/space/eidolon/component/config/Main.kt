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

package space.eidolon.component.config

import space.eidolon.component.config.io.ResourceFileReader
import space.eidolon.component.config.parsing.Parser
import space.eidolon.component.config.util.stripMargin

/**
 * Main
 *
 * @author Elliot Wright <elliot@elliotwright.co>
 */
fun main(args: Array<String>) {
    val reader = ResourceFileReader()

    val c1 = reader.read("config/config.cs")
    val c1Parser = Parser(c1)
    val c1Result = c1Parser.parse()

    println("Done")
}
