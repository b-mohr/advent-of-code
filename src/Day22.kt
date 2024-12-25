package day22

import common.readLines
import common.shouldBe

fun main() {
    part1(readLines("day22_test")) shouldBe 37327623
    part1(readLines("day22")) shouldBe 14180628689
    part2(readLines("day22")) shouldBe 1690
}

fun part1(input: List<String>): Long {
    return input.sumOf { generateSecretNumbers(it.toLong(), 2000).last() }
}

fun generateSecretNumbers(secretNumber: Long, iterations: Int): Sequence<Long> = sequence {
    var current = secretNumber
    repeat(iterations) {
        current = (current xor (current * 64)) % 16777216
        current = (current xor (current / 32)) % 16777216
        current = (current xor (current * 2048)) % 16777216
        yield(current)
    }
}

fun part2(input: List<String>): Long {
    val priceLists = buildList {
        input.forEach { buyer ->
            add(
                buildList {
                    var lastPrice = buyer.toLong() % 10
                    generateSecretNumbers(buyer.toLong(), 2000).forEach { secretNumber ->
                        val currentPrice = secretNumber % 10
                        add(currentPrice - lastPrice to currentPrice)
                        lastPrice = currentPrice
                    }
                }
            )
        }
    }

    val sequenceMaps = buildList {
        priceLists.forEach { priceList ->
            val sequenceMap = mutableMapOf<List<Long>, Long>()
            priceList.windowed(4, 1) { current ->
                val changes = current.map { it.first }
                if (!sequenceMap.containsKey(changes)) {
                    sequenceMap[changes] = current.last().second
                }
            }
            add(sequenceMap)
        }
    }

    val mergedMap = sequenceMaps.fold(mutableMapOf<List<Long>, Long>()) { mergedMap, current ->
        current.forEach { (changes, price) ->
            mergedMap[changes] = mergedMap.getOrPut(changes) { 0L } + price
        }
        mergedMap
    }

    return mergedMap.values.max()
}