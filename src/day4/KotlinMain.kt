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
package day4

object KotlinMain {

    private const val MIN_VALUE = 193651
    private const val MAX_VALUE = 649729
    private const val HEADER =
        "Potential code values between %d and %d, with non-descending digits, and with ...%n"
    private const val FORMAT_1 =
        "(Part 1) ... at least 1 run of 2+ repeating digits = %d.%n"
    private const val FORMAT_2 =
        "(Part 2) ... at least 1 run of exactly 2 repeating digits = %d.%n"

    @JvmStatic
    fun main(vararg args: String) {
        print(HEADER.format(MIN_VALUE, MAX_VALUE))
        print(FORMAT_1.format(countValid(MIN_VALUE, MAX_VALUE) { entry -> entry.value.size >= 2}))
        print(FORMAT_2.format(countValid(MIN_VALUE, MAX_VALUE) { entry -> entry.value.size == 2}))
    }

    private fun countValid(
        minimum: Int,
        maximum: Int,
        predicate: (Map.Entry<Char, List<Char>>) -> Boolean
    ): Int {
        return build(0, minimum, maximum).asSequence()
            .map { value: Int ->
                value.toString().asSequence()
                    .groupBy { d -> d }
                    .asSequence()
                    .any(predicate)
            }
            .filter { b -> b }
            .count().toInt()
    }

    private fun build(
        seed: Int,
        minimum: Int,
        maximum: Int
    ): List<Int> {
        val candidates: MutableList<Int> = ArrayList()
        val lastDigit = if (seed > 0) seed % 10 else 1
        val newSeed = seed * 10
        if (newSeed <= maximum) {
            for (i in newSeed + lastDigit until newSeed + 10) {
                if (i <= maximum) {
                    if (i >= minimum) {
                        candidates.add(i)
                    }
                    candidates.addAll(build(i, minimum, maximum))
                }
            }
        }
        return candidates
    }

}