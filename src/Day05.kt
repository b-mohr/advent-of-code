fun main() {
    part1(readLines("day05_test")) shouldBe 143
    part1(readLines("day05")) shouldBe 7307

    part2(readLines("day05_test")) shouldBe 123
    part2(readLines("day05")) shouldBe 4713
}

typealias Update = List<Int>
typealias Rules = Map<Int, List<Int>>

private fun List<String>.parse(): Pair<Rules, List<Update>> {
    return partition { it.contains("|") }
        .let { (rules, updates) ->
            Pair(
                rules.map { it.split("|").map(String::toInt) }.groupBy({it[0]}, { it[1] }),
                updates.drop(1).map { it.split(",").map(String::toInt) }
            )
        }
}

private fun part1(input: List<String>): Int {
    val (rules, updates) = input.parse()
    return updates.sumOf { it.isCorrect(rules) }
}

private fun Update.isCorrect(rules: Rules): Int {
    drop(1).forEachIndexed { index, page ->
        val subList = subList(0, index+1)
        rules[page]?.let { rules ->
            if (rules.any { subList.contains(it) }) return 0
        }
    }
    return this[size / 2]
}

private fun part2(input: List<String>): Int {
    val (rules, updates) = input.parse()
    return updates.sumOf {
        if (it.isCorrect(rules) != 0) 0
        else it.fixUpdate(rules)
    }
}

private fun Update.fixUpdate(rules: Rules): Int {
    var currentUpdate = this
        do {
            currentUpdate = currentUpdate.tryFix(rules)
        } while(currentUpdate.isCorrect(rules) == 0)

    return currentUpdate[size / 2]
}

private fun Update.tryFix(rules: Rules): Update {
    drop(1).forEachIndexed { index, page ->
        val subList = subList(0, index+1)
        rules[page]?.let { rules ->
            if (rules.any { subList.contains(it) }) {
                return swapPages(index, index+1)
            }
        }
    }

    return this
}

private fun Update.swapPages(firstIndex: Int, secondIndex: Int): Update {
    val result = this.toMutableList()
    result[firstIndex] = this[secondIndex]
    result[secondIndex] = this[firstIndex]
    return result
}