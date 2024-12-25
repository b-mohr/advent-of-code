package day23

import common.readLines
import common.shouldBe

fun main() {
    part1(readLines("day23_test")) shouldBe 7
    part1(readLines("day23")) shouldBe 1170
    part2(readLines("day23_test")) shouldBe "co,de,ka,ta"
    part2(readLines("day23")) shouldBe "bo,dd,eq,ik,lo,lu,ph,ro,rr,rw,uo,wx,yg"
}

fun part1(input: List<String>): Int {
    val connections = mutableMapOf<String, MutableSet<String>>()

    input.map { it.split("-") }.forEach { (first,second)  ->
        connections.getOrPut(first) { mutableSetOf() }.add(second)
        connections.getOrPut(second) { mutableSetOf() }.add(first)
    }

    val setsOfThrees = mutableSetOf<Set<String>>()
    connections.forEach { (node, neighbors) ->
        if (node.startsWith("t")) {
            neighbors.forEach { neighbor ->
                (neighbors.intersect(connections[neighbor]!!)).forEach { sharedNeighbor ->
                    setsOfThrees.add(setOf(node, neighbor, sharedNeighbor))
                }
            }
        }
    }

    return setsOfThrees.count()
}

fun part2(input: List<String>): String {
    val connections = HashMap<String, MutableSet<String>>()

    input.map { it.split("-") }.forEach { (first,second)  ->
        connections.getOrPut(first) { mutableSetOf() }.add(second)
        connections.getOrPut(second) { mutableSetOf() }.add(first)
    }

    fun checkConnections(nodes: Set<String>): Boolean {
        if (nodes.size <= 1) return true

        val first = nodes.first()
        val otherNodes = nodes - first
        return connections[first]!!.containsAll(otherNodes) && checkConnections(otherNodes)
    }

    fun dropAndCheck(nodes: Set<String>, dropCount: Int): Set<String>? {
        if (dropCount > 0) {
            for (node in nodes) {
                val result = dropAndCheck(nodes - node, dropCount -1)
                if (result != null) return result
            }
        } else {
            if (checkConnections(nodes)) {
                return nodes
            }
        }
        return null
    }

    for (i in 0 .. connections.values.maxOf { it.size }) {
        connections.forEach { (node, neighbors) ->
            dropAndCheck(neighbors, i)?.let {
                return (it + node).sorted().joinToString(",")
            }
        }
    }

    return ""
}