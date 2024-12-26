package day25

import common.readInput
import common.shouldBe

fun main() {
    solve(readInput("day25_test")) shouldBe 3
    solve(readInput("day25")) shouldBe 3065
}

const val maxHeight = 7

fun solve(input: String): Int {
    val (locksInput, keysInput) = input.split("\r\n\r\n")
        .groupBy { it.startsWith("#####") }.values.sortedBy { it.first() }.toList()

    val locks: List<Map<Int, Int>> = locksInput.map {
        buildMap {
            it.lines().forEachIndexed { height, line ->
                line.toList().forEachIndexed { position, char ->
                    if (char == '.' && !this.containsKey(position)) {
                        this[position] = maxHeight - height
                    }
                }

            }
        }
    }

    val keys: List<Map<Int, Int>> = keysInput.map {
        buildMap {
            it.lines().forEachIndexed { height, line ->
                line.toList().forEachIndexed { position, char ->
                    if (char == '#' && !this.containsKey(position)) {
                        this[position] = maxHeight - height
                    }
                }

            }
        }
    }

    return keys.sumOf { key -> locks.count { lock -> canUnlock(key, lock) } }
}

fun canUnlock(key: Map<Int, Int>, lock: Map<Int, Int>): Boolean {
    for (keyEntry in key) {
        if (lock[keyEntry.key]!! < keyEntry.value)
            return false
    }
    return true
}