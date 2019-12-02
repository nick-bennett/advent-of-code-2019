package com.nickbenn.day2

import java.io.File

object KotlinMain {

    private const val INPUT_FILE = "day2/input.txt"
    private const val DELIMITER = "\\s*,\\s*"
    private const val NOUN = 12
    private const val VERB = 2
    private const val TARGET = 19690720
    private const val PART_1_FORMAT =
        "Part 1: noun %d; verb = %d; resulting code[0] = %,d.%n"
    private const val PART_2_FORMAT =
        "Part 2: target code[0] = %,d; 100 * noun + verb = %d.%n"

    @JvmStatic
    fun main(vararg args: String) {
        javaClass.classLoader.getResource(INPUT_FILE)?.toURI()?.let { uri ->
            val code: IntArray = parse(File(uri))
            print(PART_1_FORMAT.format(NOUN, VERB, process(code, NOUN, VERB)))
            print(PART_2_FORMAT.format(TARGET, reverse(code, TARGET)))
        }
    }

    fun parse(file: File): IntArray {
        val delimiterRegex = Regex(DELIMITER)
        return file.useLines { sequence ->
            sequence
                .flatMap { delimiterRegex.split(it).asSequence() }
                .map { it.toInt() }
                .toList()
                .toIntArray()
        }
    }

    private fun process(code: IntArray, noun: Int, verb: Int): Int {
        val work = code.copyOf()
        work[1] = noun
        work[2] = verb
        loop@ for (position in 0 until work.size step 4) {
            val operator = work[position]
            val operand1 = work[position + 1]
            val operand2 = work[position + 2]
            val dest = work[position + 3]
            when (operator) {
                1 -> work[dest] = work[operand1] + work[operand2]
                2 -> work[dest] = work[operand1] * work[operand2]
                99 -> break@loop
                else -> throw IllegalArgumentException()
            }
        }
        return work[0]
    }

    private fun reverse(code: IntArray, target: Int): Int {
        for (noun in 0..99) {
            for (verb in 0..99) {
                if (process(code, noun, verb) == target) {
                    return 100 * noun + verb
                }
            }
        }
        throw IllegalArgumentException()
    }

}