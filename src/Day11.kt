package day11

import common.readInput
import common.shouldBe

fun main() {
    solve(readInput("day11_test"), 6) shouldBe 22
    solve(readInput("day11_test"), 25) shouldBe 55312
    solve(readInput("day11"), 25) shouldBe 211306
    solve(readInput("day11"), 75) shouldBe 250783680217283
}

fun solve(input: String, blinkCount: Int): Long {
    val stones = input.split(" ").map(String::toLong)
    val solver = Solver()
    return stones.sumOf { stone ->
        solver.blink(stone, blinkCount)
    }
}

class Solver {
    private val cachedResults = mutableMapOf<Long, MutableMap<Int, Long>>()

    private fun getOrPut(stone: Long, blinkCount: Int, calc: () -> Long): Long {
        return cachedResults.getOrPut(stone) {
            mutableMapOf(blinkCount to calc())
        }.getOrPut(blinkCount) {
            calc()
        }
    }

    fun blink(stone: Long, blinkCount: Int): Long {
        return getOrPut(stone, blinkCount) {
            calculateBlink(stone, blinkCount)
        }
    }

    private fun calculateBlink(stone: Long, blinkCount: Int): Long {
        if (blinkCount == 0) return 1
        return when {
            stone == 0L -> blink(1L, blinkCount - 1)
            stone.toString().length % 2 == 0 -> {
                val text = stone.toString()
                blink(text.substring(0, text.length / 2).toLong(), blinkCount - 1) +
                        blink(text.substring(text.length / 2).toLong(), blinkCount - 1)
            }

            else -> blink(stone * 2024, blinkCount - 1)
        }
    }
}