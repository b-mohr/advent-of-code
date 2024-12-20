import common.readLines
import common.shouldBe
import kotlin.math.abs

fun main() {
    part1(readLines("day01_test")) shouldBe 11
    part1(readLines("day01")) shouldBe 936063

    part2(readLines("day01_test")) shouldBe 31
    part2(readLines("day01")) shouldBe 23150395
}

private fun part1(input: List<String>): Int {
    val (list1, list2) = input.map { line ->
        line.split("   ").map(String::toInt).let { it[0] to it[1] }
    }.unzip()

    return (list1.sorted() zip list2.sorted()).sumOf {
        abs(it.first - it.second)
    }
}

private fun part2(input: List<String>): Int {
    val (list1, list2) = input.map { line ->
        line.split("   ").map(String::toInt).let { it[0] to it[1] }
    }.unzip()

    return list1.sumOf { current ->
        current * list2.count { current == it }
    }
}