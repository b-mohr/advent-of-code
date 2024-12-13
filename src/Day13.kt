package day13

import readLines
import shouldBe
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

fun main() {
    part1(readLines("day13_test")) shouldBe 480
    part1(readLines("day13")) shouldBe 35255
    part2(readLines("day13")) shouldBe 87582154060429
}

private val buttonRegex = """Button \w: X\+(\d{2}), Y\+(\d{2})""".toRegex()
private val priceRegex = """Prize: X=(\d{3,}), Y=(\d{3,})""".toRegex()

data class Position(val x: Int, val y: Int)
data class Machine(val a: Position, val b: Position, val p: Position)

fun List<String>.toMachine(): Machine {
    fun MatchResult?.toPosition(): Position {
        val (first, second) = this!!.destructured
        return Position(first.toInt(), second.toInt())
    }

    return Machine(
        buttonRegex.matchEntire(this[0]).toPosition(),
        buttonRegex.matchEntire(this[1]).toPosition(),
        priceRegex.matchEntire(this[2]).toPosition()
    )
}

fun part1(input: List<String>): Int {
    return input.windowed(3, 4).sumOf { machine ->
        calculateMachine(machine.toMachine())
    }
}

fun part2(input: List<String>): Long {
    return input.windowed(3, 4).sumOf { machine ->
        calculateMachine2(machine.toMachine())
    }
}

fun calculateMachine(machine: Machine): Int = with(machine) {
    val g = 1f * (b.x * p.y - b.y * p.x) / (b.y * a.x - b.x * a.y)
    val h = 1f * (p.x * a.y - p.y * a.x) / (b.y * a.x - b.x * a.y)

    if (g.isWholeNumber() && h.isWholeNumber()) {
        abs((g * 3 + h).toInt())
    } else {
        0
    }
}

fun calculateMachine2(machine: Machine): Long = with(machine) {
    val g = (b.x * (p.y + 10000000000000.0) - b.y * (p.x + 10000000000000.0)) / (b.y * a.x - b.x * a.y)
    val h = ((p.x + 10000000000000.0) * a.y - (p.y + 10000000000000.0) * a.x) / (b.y * a.x - b.x * a.y)

    if (g.isWholeNumber() && h.isWholeNumber()) {
        abs((g * 3 + h).toLong())
    } else {
        0
    }
}

fun Float.isWholeNumber(): Boolean = ceil(this) == floor(this)
fun Double.isWholeNumber(): Boolean = ceil(this) == floor(this)