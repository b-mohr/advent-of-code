package day08

import common.readLines
import common.shouldBe

fun main() {
    part1(readLines("day08_test")) shouldBe 14
    part1(readLines("day08")) shouldBe 269

    part2(readLines("day08_test")) shouldBe 34
    part2(readLines("day08")) shouldBe 949
}

fun part1(input: List<String>): Int = solve(input, ::calculateAntinodes1)
fun part2(input: List<String>): Int = solve(input, ::calculateAntinodes2)

data class Position(val x: Int, val y: Int)

fun List<String>.parse(): Map<Char, List<Position>> {
    val frequencyMap = mutableMapOf<Char, MutableList<Position>>()
    forEachIndexed { y, row ->
        row.forEachIndexed { x, char ->
            if (char != '.') {
                frequencyMap.getOrPut(char) { mutableListOf() }.add(Position(x + 1, y + 1))
            }
        }
    }
    return frequencyMap
}

fun solve(input: List<String>, calculateAntinodes: (Position, Position, Position) -> Set<Position>): Int {
    val frequencyMap = input.parse()
    val mapBounds = Position(input[0].length, input.size)

    val antinodes = mutableSetOf<Position>()

    frequencyMap.values.forEach { antennas ->
        antennas.forEachIndexed { index, first ->
            antennas.drop(index + 1).forEach { second ->
                antinodes += calculateAntinodes(first, second, mapBounds)
            }
        }
    }

    return antinodes.size
}

fun calculateAntinodes1(firstAntenna: Position, secondAntenna: Position, mapBounds: Position): Set<Position> {
    val result = mutableSetOf<Position>()

    val firstAntinode = Position(2 * firstAntenna.x - secondAntenna.x, 2 * firstAntenna.y - secondAntenna.y)
    if (firstAntinode.isInBounds(mapBounds)) {
        result += firstAntinode
    }

    val secondAntinode = Position(2 * secondAntenna.x - firstAntenna.x, 2 * secondAntenna.y - firstAntenna.y)
    if (secondAntinode.isInBounds(mapBounds))
        result += secondAntinode

    return result
}

fun calculateAntinodes2(firstAntenna: Position, secondAntenna: Position, mapBounds: Position): Set<Position> {
    val result = mutableSetOf(firstAntenna, secondAntenna)

    var i = 1
    while (true) {
        val antinode = Position(
            firstAntenna.x + i * (firstAntenna.x - secondAntenna.x),
            firstAntenna.y + i * (firstAntenna.y - secondAntenna.y)
        )

        if (antinode.isInBounds(mapBounds)) {
            result += antinode
            i++
        } else {
            break
        }
    }

    i = 1
    while (true) {
        val antinode = Position(
            secondAntenna.x + i * (secondAntenna.x - firstAntenna.x),
            secondAntenna.y + i * (secondAntenna.y - firstAntenna.y)
        )

        if (antinode.isInBounds(mapBounds)) {
            result += antinode
            i++
        } else {
            break
        }
    }

    return result
}

fun Position.isInBounds(bounds: Position): Boolean {
    return x in 1..bounds.x && y in 1..bounds.y
}
