package day19

import common.readInput
import common.shouldBe

fun main() {
    part1(readInput("day19")) shouldBe 308
    part2(readInput("day19")) shouldBe 662726441391898
}

fun part1(input: String): Int {
    val (patternsInput, designsInput) = input.split("\r\n\r\n")
    val patterns = patternsInput.split(", ")
    val designs = designsInput.lines()

    val cache = mutableMapOf<String, Boolean>()

    fun isDesignPossible(design: String): Boolean {
        if (design.isEmpty()) return true

        var result = false
        patterns.forEach {
            if (design.startsWith(it)) {
                val subDesign = design.substringAfter(it)
                if (cache.getOrPut(subDesign) { isDesignPossible(subDesign) }) {
                    result = true
                }
            }
        }
        return result
    }

    return designs.count { isDesignPossible(it) }
}

fun part2(input: String): Long {
    val (patternsInput, designsInput) = input.split("\r\n\r\n")
    val patterns = patternsInput.split(", ")
    val designs = designsInput.lines()

    val cache = mutableMapOf<String, Long>()

    fun calculatePossibilities(design: String): Long = patterns.sumOf {
        if (design.startsWith(it)) {
            val subDesign = design.substringAfter(it)

            if (subDesign.isEmpty())
                1L
            else
                cache.getOrPut(subDesign) { calculatePossibilities(subDesign) }
        } else {
            0L
        }
    }

    return designs.sumOf { calculatePossibilities(it) }
}


