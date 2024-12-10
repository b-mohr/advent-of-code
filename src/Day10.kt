package day10

import readLines
import shouldBe

fun main() {
    part1(readLines("day10_test")) shouldBe 36
    part1(readLines("day10")) shouldBe 517
    part2(readLines("day10_test")) shouldBe 81
    part2(readLines("day10")) shouldBe 1116
}

fun part1(input: List<String>): Int {
    val map = Map.fromInput(input)

    return map.getTrailheads().sumOf {
        calculateTrailheadScore(it, map)
    }
}

fun part2(input: List<String>): Int {
    val map = Map.fromInput(input)

    return map.getTrailheads().sumOf {
        calculateTrailheadRating(it, map)
    }
}

data class Position(val x: Int, val y: Int)
data class Node(val position: Position, val height: Int) {
    val x = position.x
    val y = position.y

    constructor(x: Int, y: Int, height: Int) : this(Position(x, y), height)
}

class Map(private val internalMap: List<List<Node>>) {

    fun getTrailheads(): Sequence<Node> {
        return nodes().filter { it.height == 0 }
    }

    private fun nodes(): Sequence<Node> = sequence {
        internalMap.forEach { col ->
            col.forEach { node ->
                yield(node)
            }
        }
    }

    fun getReachableNeighbors(node: Node): List<Node> {
        return listOfNotNull(
            getOrNull(node.x, node.y + 1),
            getOrNull(node.x, node.y - 1),
            getOrNull(node.x + 1, node.y),
            getOrNull(node.x - 1, node.y)
        ).filter { it.height == node.height + 1 }
    }

    private fun getOrNull(x: Int, y: Int): Node? = internalMap.getOrNull(y)?.getOrNull(x)

    companion object {
        fun fromInput(input: List<String>): Map = Map(
            input.mapIndexed { y, col ->
                col.mapIndexed { x, height ->
                    Node(x, y, height.digitToInt())
                }
            }
        )
    }
}

fun calculateTrailheadScore(trailhead: Node, map: Map): Int {
    return calculateReachableNodes(trailhead, map).size
}

fun calculateReachableNodes(node: Node, map: Map): Set<Node> {
    if (node.height == 9) return setOf(node)
    val reachableNeighbors = map.getReachableNeighbors(node)

    if (reachableNeighbors.isEmpty()) return emptySet()

    return reachableNeighbors.flatMap {
        calculateReachableNodes(it, map)
    }.toSet()
}


fun calculateTrailheadRating(trailhead: Node, map: Map): Int {
    if (trailhead.height == 9) return 1
    val reachableNeighbors = map.getReachableNeighbors(trailhead)

    if (reachableNeighbors.isEmpty()) return 0

    return reachableNeighbors.sumOf {
        calculateTrailheadRating(it, map)
    }
}