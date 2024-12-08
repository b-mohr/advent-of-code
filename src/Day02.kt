import kotlin.math.abs

fun main() {
    part1(readLines("day02_test")) shouldBe 2
    part1(readLines("day02")) shouldBe 202

    part2(readLines("day02_test")) shouldBe 4
    part2(readLines("day02")) shouldBe 271
}

private fun part1(input: List<String>): Int = input
    .map { it.split(" ").map(String::toInt) }
    .fold(0) { result, report ->
        if (report.isSafe()) result + 1 else result
    }

private fun part2(input: List<String>): Int = input
    .map { it.split(" ").map(String::toInt) }
    .fold(0) { result, report ->
        if (report.indices.any { report.filterIndexed { index, _ -> index != it }.isSafe() })
            result + 1
        else
            result
    }

private fun List<Int>.isSafe(): Boolean {
    var isIncreasing: Boolean? = null
    windowed(2).forEach {
        val (a, b) = it
        val diff = a - b

        if (diff == 0 || abs(diff) > 3) return false

        if (isIncreasing == null) {
            isIncreasing = diff < 0
        } else {
            if (isIncreasing == diff > 0) return false
        }
    }

    return true
}
