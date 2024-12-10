package day09

import readInput
import shouldBe

fun main() {
    part1(readInput("day09_test")) shouldBe 1928
    part1(readInput("day09")) shouldBe 6259790630969
    part2(readInput("day09_test")) shouldBe 2858
    part2(readInput("day09")) shouldBe 6289564433984
}

fun part1(input: String): Long {
    val diskMap = input.toList().map(Char::digitToInt)

    var currentId = -1
    val blocks = diskMap.flatMapIndexed { index, number ->
        val isFile = index % 2 == 0

        if (isFile) currentId++

        List(number) {
            if (isFile) {
                currentId
            } else {
                -1
            }
        }
    }

    var alreadyProcessed = -1
    return blocks.mapIndexed { index, number ->
        if (index > blocks.size - alreadyProcessed - 2) return@mapIndexed 0

        if (number == -1) {
            var latestNotProcessedNumber: Int = -1
            while (latestNotProcessedNumber == -1) {
                alreadyProcessed++
                if (blocks.lastIndex - alreadyProcessed <= index) return@mapIndexed 0
                latestNotProcessedNumber = blocks[blocks.lastIndex - alreadyProcessed]
            }

            1L * index * latestNotProcessedNumber
        } else {
            1L * index * number
        }
    }.sum()
}

fun part2(input: String): Long {
    val diskMap = input.toList().map(Char::digitToInt)

    var currentId = -1
    val blocks: List<DiskBlockGroup> = diskMap.mapIndexed { index, number ->
        val isFile = index % 2 == 0

        if (isFile) {
            currentId++
            File(currentId, number)
        } else {
            FreeSpace(number)
        }
    }

    val files = blocks.filterIsInstance<File>().reversed().toMutableList()

    var result = 0L
    var position = 0
    blocks.forEach { blockGroup ->
        when (blockGroup) {
            is File -> {
                if (files.contains(blockGroup)) {
                    repeat(blockGroup.length) {
                        result += position * blockGroup.id
                        position++
                    }
                    files.remove(blockGroup)
                } else {
                    position += blockGroup.length
                }
            }

            is FreeSpace -> {
                var remainingSpace = blockGroup.length

                while (remainingSpace > 0) {
                    val file = files.firstOrNull { it.length <= remainingSpace } ?: break
                    repeat(file.length) {
                        result += position * file.id
                        position++
                    }
                    remainingSpace -= file.length

                    files.remove(file)
                }

                position += remainingSpace
            }
        }
    }
    return result
}

sealed interface DiskBlockGroup

data class File(val id: Int, val length: Int) : DiskBlockGroup
data class FreeSpace(val length: Int) : DiskBlockGroup
