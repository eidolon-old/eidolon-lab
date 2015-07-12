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

import java.math.BigDecimal
import java.util.*

interface ConfigValue

class ConfigArray() : ArrayList<ConfigValue>(), ConfigValue

class ConfigObject() : HashMap<String, ConfigValue>(), ConfigValue

class ConfigString(val value: String) : ConfigValue

class ConfigNumber(val value: BigDecimal) : ConfigValue

class ConfigNull() : ConfigValue

open class ConfigBoolean(val value: Boolean) : ConfigValue

class ConfigTrue : ConfigBoolean(true)

class ConfigFalse : ConfigBoolean(false)
