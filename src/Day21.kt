package day21

import common.Position
import common.readLines
import common.shouldBe
import kotlin.math.abs

fun main() {
    solve(readLines("day21"), 2) shouldBe 278748
    solve(readLines("day21"), 25) shouldBe 337744744231414
}

fun solve(input: List<String>, robots: Int): Long {
    return input.sumOf {
        it.toList().dropLast(1).joinToString("").toLong() *
                calculateLength(it.toList(), NumericKeypad, robots + 1)
    }
}

val cache = mutableMapOf<Triple<Position, Position, Int>, Long>()

fun calculateLength(sequence: List<Char>, keypad: Keypad = NumericKeypad, robots: Int): Long {
    if (robots == 0) {
        return sequence.count().toLong()
    }
    var start = keypad.fromChar('A')

    return sequence.sumOf {
        val current = keypad.fromChar(it)
        val delta = start.position - current.position
        val corner1 = Position(start.position.x, current.position.y)
        val corner2 = Position(current.position.x, start.position.y)
        cache.getOrPut(Triple(start.position, current.position, robots)) {
            minOf(
                if (corner1 == keypad.emptyGap) Long.MAX_VALUE else calculateLength(
                    buildList {
                        repeat(abs(delta.y)) { add(if (delta.y < 0) '^' else 'v') }
                        repeat(abs(delta.x)) { add(if (delta.x < 0) '>' else '<') }
                        add('A')
                    }, DirectionalKeypad, robots - 1
                ),
                if (corner2 == keypad.emptyGap) Long.MAX_VALUE else calculateLength(
                    buildList {
                        repeat(abs(delta.x)) { add(if (delta.x < 0) '>' else '<') }
                        repeat(abs(delta.y)) { add(if (delta.y < 0) '^' else 'v') }
                        add('A')
                    }, DirectionalKeypad, robots - 1
                )
            )
        }.also {
            start = current
        }
    }
}

interface Keypad {
    val emptyGap: Position

    interface Button {
        val position: Position
    }

    fun fromChar(char: Char): Button
}

enum class DirectionalKeypad(override val position: Position) : Keypad.Button {
    Up(Position(1, 1)),
    Down(Position(1, 0)),
    Left(Position(0, 0)),
    Right(Position(2, 0)),
    A(Position(2, 1));

    companion object : Keypad {
        override val emptyGap: Position = Position(0, 1)
        override fun fromChar(char: Char) = when (char) {
            '<' -> Left
            'v' -> Down
            '>' -> Right
            '^' -> Up
            'A' -> A
            else -> error("wrong char")
        }
    }
}

enum class NumericKeypad(override val position: Position) : Keypad.Button {
    B0(Position(1, 0)),
    B1(Position(0, 1)),
    B2(Position(1, 1)),
    B3(Position(2, 1)),
    B4(Position(0, 2)),
    B5(Position(1, 2)),
    B6(Position(2, 2)),
    B7(Position(0, 3)),
    B8(Position(1, 3)),
    B9(Position(2, 3)),
    BA(Position(2, 0));

    companion object : Keypad {
        override val emptyGap: Position = Position(0, 0)
        override fun fromChar(char: Char) = when (char) {
            'A' -> BA
            '0' -> B0
            '1' -> B1
            '2' -> B2
            '3' -> B3
            '4' -> B4
            '5' -> B5
            '6' -> B6
            '7' -> B7
            '8' -> B8
            '9' -> B9
            else -> error("wrong char")
        }
    }
}