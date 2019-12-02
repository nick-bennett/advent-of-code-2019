/*
 *  Copyright 2019 Nicholas Bennett & Deep Dive Coding/CNM Ingenuity
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
package com.nickbenn.day2

import java.io.File

object KotlinMain {

    private const val INPUT_FILE = "day2/input.txt"
    private const val DELIMITER = "\\s*,\\s*"
    private const val NOUN = 12
    private const val VERB = 2
    private const val TARGET = 19_690_720
    private const val UPPER_BOUND = 100
    private const val FORMAT_1 = "Part 1: noun %d; verb = %d; resulting code[0] = %,d.%n"
    private const val FORMAT_2 = "Part 2: target code[0] = %,d; 100 * noun + verb = %d.%n"

    private val delimiterRegex = Regex(DELIMITER)

    @JvmStatic
    fun main(vararg args: String) {
        javaClass.classLoader.getResource(INPUT_FILE)?.toURI()?.let { uri ->
            with (parse(File(uri))) {
                print(FORMAT_1.format(NOUN, VERB, process(this, NOUN, VERB)))
                print(FORMAT_2.format(TARGET, reverse(this, TARGET)))
            }
        }
    }

    fun parse(file: File): List<Int> {
        return file.useLines { sequence ->
            sequence
                .flatMap { delimiterRegex.split(it).asSequence() }
                .map { it.toInt() }
                .toList()
        }
    }

    private fun process(code: List<Int>, noun: Int, verb: Int): Int {
        val work = code.toMutableList()
        work[1] = noun
        work[2] = verb
        loop@ for (position in work.indices step 4) {
            val (opcode, operand1, operand2, dest) = work.subList(position, position + 4)
            when (opcode) {
                1 -> work[dest] = work[operand1] + work[operand2]
                2 -> work[dest] = work[operand1] * work[operand2]
                99 -> break@loop
                else -> throw IllegalArgumentException()
            }
        }
        return work[0]
    }

    private fun reverse(code: List<Int>, target: Int): Int {
        for (noun in 0 until UPPER_BOUND) {
            for (verb in 0 until UPPER_BOUND) {
                if (process(code, noun, verb) == target) {
                    return UPPER_BOUND * noun + verb
                }
            }
        }
        throw IllegalArgumentException()
    }

}