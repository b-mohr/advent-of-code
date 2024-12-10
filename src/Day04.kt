fun main() {
    part1(readInput("day04_test").toMatrix()) shouldBe 18
    part1(readInput("day04").toMatrix()) shouldBe 2633

    part2(readInput("day04_test").toMatrix()) shouldBe 9
    part2(readInput("day04").toMatrix()) shouldBe 1936
}

private typealias Matrix = List<List<String>>

private fun String.toMatrix(): Matrix {
    return this.lines().map { it.split("").filterNot { char -> char == "" } }
}

private fun part1(input: Matrix): Int {
    var result = 0
    for (i in input.indices) {
        for (j in input[i].indices) {
            val current = input[i][j]
            if (current != "X") continue

            result += listOfNotNull(
                stringOrNull { current + input[i][j + 1] + input[i][j + 2] + input[i][j + 3] },
                stringOrNull { current + input[i][j - 1] + input[i][j - 2] + input[i][j - 3] },
                stringOrNull { current + input[i + 1][j] + input[i + 2][j] + input[i + 3][j] },
                stringOrNull { current + input[i - 1][j] + input[i - 2][j] + input[i - 3][j] },
                stringOrNull { current + input[i + 1][j + 1] + input[i + 2][j + 2] + input[i + 3][j + 3] },
                stringOrNull { current + input[i - 1][j - 1] + input[i - 2][j - 2] + input[i - 3][j - 3] },
                stringOrNull { current + input[i + 1][j - 1] + input[i + 2][j - 2] + input[i + 3][j - 3] },
                stringOrNull { current + input[i - 1][j + 1] + input[i - 2][j + 2] + input[i - 3][j + 3] },
            ).count { it == "XMAS" }
        }
    }
    return result
}

private fun part2(input: Matrix): Int {
    var result = 0
    for (i in input.indices) {
        for (j in input[i].indices) {
            val current = input[i][j]
            if (current != "A") continue

            val count = listOfNotNull(
                stringOrNull { input[i - 1][j - 1] + current + input[i + 1][j + 1] },
                stringOrNull { input[i + 1][j + 1] + current + input[i - 1][j - 1] },
                stringOrNull { input[i + 1][j - 1] + current + input[i - 1][j + 1] },
                stringOrNull { input[i - 1][j + 1] + current + input[i + 1][j - 1] },
            ).count { it == "MAS" }

            if (count >= 2) result++
        }
    }
    return result
}

private fun stringOrNull(f: () -> String): String? {
    return try {
        f()
    } catch (_: IndexOutOfBoundsException) {
        null
    }
}