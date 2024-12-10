fun main() {
    part1(readLines("day07_test")) shouldBe 3749
    part1(readLines("day07")) shouldBe 1985268524462

    part2(readLines("day07_test")) shouldBe 11387
    part2(readLines("day07")) shouldBe 150077710195188
}

private fun part1(input: List<String>): Long {
    return input.sumOf { testEquation(it, listOf(Operator.Plus, Operator.Multiply)) }
}

private fun part2(input: List<String>): Long {
    return input.sumOf { testEquation(it, listOf(Operator.Plus, Operator.Multiply, Operator.Concatenation)) }
}

private fun testEquation(input: String, operators: List<Operator>): Long {
    val testValue = input.substringBefore(":").toLong()
    val numbers = input.substringAfter(":").trim().split(" ").map(String::toLong)
    return if (step(testValue, numbers, operators)) testValue else 0
}

private fun step(expectedResult: Long, remainingNumbers: List<Long>, operators: List<Operator>): Boolean {
    val lastNumber = remainingNumbers.last()

    if (remainingNumbers.size == 1) {
        return lastNumber == expectedResult
    }

    for (op in operators) {
        val remainingResult = when (op) {
            Operator.Plus -> {
                if (lastNumber > expectedResult) continue
                expectedResult - lastNumber
            }

            Operator.Multiply -> {
                if (expectedResult % lastNumber != 0L) continue
                expectedResult / lastNumber
            }

            Operator.Concatenation -> {
                if (!expectedResult.toString().endsWith(lastNumber.toString())) continue
                expectedResult.toString().substringBeforeLast(lastNumber.toString()).toLong()
            }
        }

        if (step(remainingResult, remainingNumbers.dropLast(1), operators)) return true
    }

    return false
}

enum class Operator {
    Plus, Multiply, Concatenation
}