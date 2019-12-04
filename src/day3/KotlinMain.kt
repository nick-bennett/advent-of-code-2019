package day3

import java.io.File
import kotlin.math.abs

object KotlinMain {

    private const val INPUT_FILE = "input.txt"
    private const val DELIMITER = "\\s*,\\s*"
    private const val CELL_FORMAT = "%1\$s(row=%2\$d, col=%3\$d)"
    private const val LEG_FORMAT = "%s %d"
    private const val FORMAT_1 =
        "Part 1: Minimum Manhattan distance from origin to intersection is %,d.%n"
    private const val FORMAT_2 =
        "Part 2: Minimum combined travel distance from origin to intersection is %,d.%n"

    private val delimiterRegex = Regex(DELIMITER)

    @JvmStatic
    fun main(args: Array<String>) {
        javaClass.getResource(INPUT_FILE)?.toURI()?.let { uri ->
            with(parse(File(uri))) {
                print(FORMAT_1.format(process(this) { cell -> cell.manhattan }))
                print(FORMAT_2.format(process(this) { cell -> cell.travel }))
            }
        }
    }

    private fun parse(file: File): List<Wire> {
        return file.useLines { sequence ->
            sequence
                .map { Wire.parse(it) }
                .toList()
        }
    }

    private fun process(wires: List<Wire>, metric: (Cell) -> Int): Int {
        val traces: MutableMap<Cell, Cell> = HashMap()
        var best: Cell? = null
        var bestMeasure = Int.MAX_VALUE
        for (wire in wires) {
            var row = 0
            var column = 0
            var travel = 0
            for (leg in wire.legs) {
                val direction = leg.direction
                val length = leg.length
                val rowOffset = direction.rowOffset
                val columnOffset = direction.columnOffset
                for (step in 0 until length) {
                    row += rowOffset
                    column += columnOffset
                    val cell = Cell(wire, row, column, ++travel)
                    val previous = traces[cell]
                    if (previous == null) {
                        traces[cell] = cell
                    } else if (previous.wire != wire) {
                        val augmented = Cell(wire, row, column, travel + previous.travel)
                        val test = metric(augmented)
                        if (best == null || bestMeasure > test) {
                            best = augmented
                            bestMeasure = test
                        }
                    }
                }
            }
        }
        return bestMeasure
    }

    private enum class Direction(
        val rowOffset: Int,
        val columnOffset: Int
    ) {

        UP(-1, 0),
        RIGHT(0, 1),
        DOWN(1, 0),
        LEFT(0, -1);

        companion object {

            fun fromCode(code: Char): Direction? {
                return when (code) {
                    'U' -> UP
                    'R' -> RIGHT
                    'D' -> DOWN
                    'L' -> LEFT
                    else -> null
                }
            }

        }

    }

    private class Leg private constructor(val direction: Direction, val length: Int) {

        private val str: String = String.format(LEG_FORMAT, direction, length)

        override fun toString(): String {
            return str
        }

        companion object {

            fun parse(input: String): Leg {
                val direction = Direction.fromCode(input[0])
                val length = input.substring(1).toInt()
                return Leg(direction!!, length)
            }

        }

    }

    private class Wire private constructor(_legs: List<Leg>) {

        val legs: List<Leg> = _legs

        companion object {

            fun parse(input: String?): Wire {
                return Wire(
                    delimiterRegex.split(input ?: "").asSequence()
                        .map { Leg.parse(it) }
                        .toList()
                )
            }

        }

    }

    private class Cell(
        val wire: Wire,
        val row: Int,
        val column: Int,
        val travel: Int
    ) {

        val manhattan: Int = abs(row) + abs(column)
        private val hash: Int = 31 * row.hashCode() + column.hashCode()
        private val str: String = CELL_FORMAT.format(javaClass.simpleName, row, column)

        override fun hashCode(): Int {
            return hash
        }

        override fun equals(other: Any?): Boolean {
            return (other === this || (other is Cell && other.row == row && other.column == column))
        }

        override fun toString(): String {
            return str
        }

    }

}