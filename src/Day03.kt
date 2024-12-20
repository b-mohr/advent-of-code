import common.readInput
import common.shouldBe

fun main() {
    part1(readInput("day03_test")) shouldBe 161
    part1(readInput("day03")) shouldBe 155955228

    part2(readInput("day03_test")) shouldBe 48
    part2(readInput("day03")) shouldBe 100189366
}

private const val mulRegex = """mul\((\d{1,3}),(\d{1,3})\)"""
private const val doRegex = """do\(\)"""
private const val dontRegex = """don't\(\)"""

private fun part1(input: String): Int =
    mulRegex.toRegex().findAll(input).sumOf(MatchResult::handleMultiply)

private fun part2(input: String): Int {
    var enabled = true
    var sum = 0
    "$mulRegex|$doRegex|$dontRegex".toRegex().findAll(input).forEach { match ->
        when (match.value) {
            "do()" -> enabled = true
            "don't()" -> enabled = false
            else -> if (enabled) sum += match.handleMultiply()
        }
    }
    return sum
}

private fun MatchResult.handleMultiply(): Int = groupValues.drop(1).map(String::toInt).let {
    it.component1() * it.component2()
}