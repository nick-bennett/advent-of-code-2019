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
package com.nickbenn.day1

import java.io.File

object KotlinMain {

    private const val INPUT_FILE = "day1/input.txt"
    private const val OUTPUT_FORMAT = "Part %d: %,d%n"

    @JvmStatic
    fun main(vararg args: String) {
        javaClass.classLoader.getResource(INPUT_FILE)?.toURI()?.let { uri ->
            print(OUTPUT_FORMAT.format(1, compute(File(uri), ::fuelNeeded)))
            print(OUTPUT_FORMAT.format(2, compute(File(uri), ::totalFuelNeeded)))
        }
    }

    private fun compute(file: File, transform: (Int) -> Int): Int {
        return file.useLines { sequence ->
            sequence
                .map { it.toInt() }
                .map { transform(it) }
                .sum()
        }
    }

    private fun fuelNeeded(mass: Int) = Math.max(0, mass / 3 - 2)

    private fun totalFuelNeeded(mass: Int): Int {
        return if (mass <= 0) {
            0
        } else {
            with(fuelNeeded(mass)) {
                this + totalFuelNeeded(this)
            }
        }
    }

}
