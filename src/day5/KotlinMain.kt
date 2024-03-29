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
    private const val OUTPUT_FORMAT = "Part %d: For input = %d, diagnostic output = %d.%n"

    private val delimiterRegex = Regex(DELIMITER)

    @JvmStatic
    fun main(args: Array<String>) {
        javaClass.getResource(INPUT_FILE)?.toURI()?.let { uri ->
            with(parse(File(uri))) {
                print(OUTPUT_FORMAT.format(1, 1, process(this.clone(), 1)))
                print(OUTPUT_FORMAT.format(2, 5, process(this.clone(), 5)))
            }
        }
    }

    private fun parse(file: File): IntArray {
        return file.useLines { sequence ->
            sequence
                .flatMap {
                    delimiterRegex.split(it).asSequence()
                        .map { s -> s.toInt() }
                }
                .toList()
                .toIntArray()
        }
    }

    private fun process(instructions: IntArray, input: Int): Int {
        var result = 0
        var ip = 0

        fun consume(length: Int, quotient: Int, store: Boolean): IntArray {
            val operands = instructions.copyOfRange(ip, ip + length)
            ip += length
            var q = quotient
            for (i in 0 until (length - if (store) 1 else 0)) {
                if (q % 10 == 0) {
                    operands[i] = instructions[operands[i]]
                }
                q /= 10
            }
            return operands
        }

        while (true) {
            val operation = instructions[ip++]
            val quotient = operation / 100
            when (operation % 100) {
                1 -> {
                    val operands = consume(3, quotient, true)
                    instructions[operands[2]] = operands[0] + operands[1]
                }
                2 -> {
                    val operands = consume(3, quotient, true)
                    instructions[operands[2]] = operands[0] * operands[1]
                }
                3 -> {
                    val operands = consume(1, quotient, true)
                    instructions[operands[0]] = input
                }
                4 -> {
                    val operands = consume(1, quotient, false)
                    result = if (result != 0) {
                        throw IllegalArgumentException()
                    } else {
                        operands[0]
                    }
                }
                5 -> {
                    val operands = consume(2, quotient, false)
                    if (operands[0] != 0) {
                        ip = operands[1]
                    }
                }
                6 -> {
                    val operands = consume(2, quotient, false)
                    if (operands[0] == 0) {
                        ip = operands[1]
                    }
                }
                7 -> {
                    val operands = consume(3, quotient, true)
                    instructions[operands[2]] = if (operands[0] < operands[1]) 1 else 0
                }
                8 -> {
                    val operands = consume(3, quotient, true)
                    instructions[operands[2]] = if (operands[0] == operands[1]) 1 else 0
                }
                99 -> return result
            }
        }

    }

}