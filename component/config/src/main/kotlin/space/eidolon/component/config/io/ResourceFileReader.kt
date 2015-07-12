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

package space.eidolon.component.config.io

import java.io.File
import java.nio.file.Paths
import java.nio.file.Files

/**
 * FileReader
 *
 * @author Elliot Wright <elliot@elliotwright.co>
 */
public class ResourceFileReader {
    fun read(filename: String): String {
        val resource = File(javaClass.getClassLoader().getResource(filename).getFile())
        val lines = resource.readLines(Charsets.UTF_8)
        var builder = StringBuilder()

        for (line in lines) {
            builder.append(line)
            builder.append(System.lineSeparator())
        }

        return builder.toString()
    }
}
