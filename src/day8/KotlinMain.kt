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
package day8

import java.io.File

object KotlinMain {

    private const val INPUT_FILE = "input.txt"
    private const val WIDTH = 25
    private const val HEIGHT = 6
    private const val FORMAT_1 =
        "Part 1: (count of 1s) * (count of 2s) in layer with fewest 0s = %d.%n"
    private const val FORMAT_2 = "Part 2: Decoded image:"

    @JvmStatic
    fun main(args: Array<String>) {
        javaClass.getResource(INPUT_FILE)?.toURI()?.let { uri ->
            with(parse(File(uri))) {
                val matrix = structure(this, HEIGHT, WIDTH)
                print(FORMAT_1.format(count(matrix)))
                val image = decode(matrix, HEIGHT, WIDTH)
                println(FORMAT_2)
                render(image)
            }
        }
    }

    private fun parse(file: File): CharArray {
        return file.useLines { sequence ->
            sequence
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .joinToString { it }
                .toCharArray()
        }
    }

    private fun structure(
        digits: CharArray,
        rows: Int,
        columns: Int
    ): Array<CharArray> {
        val layerSize = rows * columns
        val depth = digits.size / columns / rows
        return Array(depth) { layer ->
            digits.copyOfRange(layer * layerSize, (layer + 1) * layerSize)
        }
    }

    private fun count(matrix: Array<CharArray>): Long {
        return matrix.asSequence()
            .map { layer: CharArray ->
                String(layer).toCharArray().asSequence()
                    .groupBy { it }
            }
            .minBy { it['0']?.size ?: 0 }?.run {
                ((this['1']?.size ?: 0) * (this['2']?.size ?: 0)).toLong()
            } ?: 0
    }

    private fun decode(
        matrix: Array<CharArray>,
        rows: Int,
        columns: Int
    ): Array<CharArray> {
        val decoded = CharArray(rows * columns)
        decoded.fill('2', 0, decoded.size)
        for (layer in matrix.indices) {
            for (i in matrix[layer].indices) {
                if (decoded[i] == '2') {
                    decoded[i] = matrix[layer][i]
                }
            }
        }
        val image = Array(rows) { row ->
            decoded.copyOfRange(row * columns, (row + 1) * columns)
        }
        return image
    }

    private fun render(image: Array<CharArray>) {
        image.asSequence()
            .map { String(it).replace('0', ' ').replace('1', '\u2022') }
            .forEach { println(it) }
    }

}
