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
package day6

import java.io.File
import java.io.IOException

object KotlinMain {

    private const val INPUT_FILE = "input.txt"
    private const val DELIMITER = "\\)"
    private const val ROOT = "COM"
    private const val START = "YOU"
    private const val FINISH = "SAN"
    private const val FORMAT_1 = "Part 1: Total direct & indirect orbits = %d.%n"
    private const val FORMAT_2 = "Part 2: Orbital transfers required from %s to %s = %d.%n"

    private val delimiterRegex = Regex(DELIMITER)

    @JvmStatic
    fun main(args: Array<String>) {
        javaClass.getResource(INPUT_FILE)?.toURI()?.let { uri ->
            with(parse(File(uri))) {
                print(FORMAT_1.format(totalPathLength(this, ROOT)))
                print(FORMAT_2.format(START, FINISH, familyDistance(this, ROOT, START, FINISH)))
            }
        }
    }

    @Throws(IOException::class)
    fun parse(file: File): Map<String, String> {
        return file.useLines { sequence ->
            sequence
                .flatMap { s: String ->
                    s.split(delimiterRegex)
                        .asReversed()
                        .asSequence()
                }
                .zipWithNext()
                .toMap()
        }
    }

    fun totalPathLength(
        links: Map<String, String>,
        root: String
    ): Int {
        var total = 0
        for (key in links.keys) {
            total += generationDistance(links, root, key)
        }
        return total
    }

    private fun generationDistance(
        links: Map<String, String>,
        ancestor: String, descendant: String
    ): Int {
        var steps = 1
        var parent = links[descendant]
        while (parent != ROOT) {
            steps++
            parent = links[parent]
        }
        return steps
    }

    private fun familyDistance(
        links: Map<String, String>,
        root: String, relative1: String, relative2: String
    ): Int {
        val generation1 = generationDistance(links, root, relative1)
        val generation2 = generationDistance(links, root, relative2)
        var ancestor1 =
            ancestor(links, relative1, Math.max(generation1 - generation2, 0))
        var ancestor2 =
            ancestor(links, relative2, Math.max(generation2 - generation1, 0))
        var distance = Math.abs(generation1 - generation2)
        while (ancestor1 != ancestor2) {
            distance += 2
            ancestor1 = links[ancestor1]
            ancestor2 = links[ancestor2]
        }
        return distance - 2
    }

    private fun ancestor(
        links: Map<String, String>,
        descendant: String,
        generations: Int
    ): String? {
        var ancestor: String? = descendant
        for (i in generations downTo 1) {
            ancestor = links[ancestor]
        }
        return ancestor
    }

}