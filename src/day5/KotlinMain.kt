/*
 *  Copyright 2019 Nicholas Bennett
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package day5

import java.io.File

object KotlinMain {

    private const val INPUT_FILE = "input.txt"
    private const val DELIMITER = "\\s*,\\s*"

    private val delimiterRegex = Regex(DELIMITER)

    @JvmStatic
    fun main(args: Array<String>) {
        javaClass.getResource(INPUT_FILE)?.toURI()?.let { uri ->
            with(parse(File(uri))) {
                print(process(this))
            }
        }
    }

    private fun parse(file: File): List<List<String>> {
        return file.useLines { sequence ->
            sequence
                .map { delimiterRegex.split(it) }
                .toList()
        }
    }

    private fun process(data: List<List<String>>): Int {
        return 0
    }

}