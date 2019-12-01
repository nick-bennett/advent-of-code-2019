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

    private fun fuelNeeded(mass: Int): Int {
        return Math.max(0, mass / 3 - 2)
    }

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
