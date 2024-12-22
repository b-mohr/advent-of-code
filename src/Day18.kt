package day18

import common.Direction
import common.Position
import common.readLines
import common.shouldBe
import java.util.PriorityQueue

fun main() {
    part1(readLines("day18")) shouldBe 416
    part2(readLines("day18")) shouldBe "50,23"
}

data class Node(val position: Position, val stepCount: Int)

fun part1(input: List<String>): Int {
    val bytes = input.take(1024).map { it.split(",") }.map { Position(it[0].toInt(), it[1].toInt()) }
    val goal = Position(70, 70)
    val visited = mutableSetOf<Position>()

    val queue = PriorityQueue<Node>(compareBy { it.stepCount })
    queue.add(Node(Position(0, 0), 0))

    fun tryAddToQueue(node: Node) {
        if (node.position in bytes) return
        if (node.position.x !in 0..goal.x) return
        if (node.position.y !in 0..goal.y) return

        queue.add(node)
    }

    while (!queue.isEmpty()) {
        val current = queue.poll()!!
        if (!visited.add(current.position)) continue

        if (current.position == goal) {
            return current.stepCount
        }

        Direction.entries.forEach { direction ->
            tryAddToQueue(Node(current.position + direction.position, current.stepCount + 1))
        }
    }
    return -1
}

fun part2(input: List<String>): String {
    val allBytes = input.map { it.split(",") }.map { Position(it[0].toInt(), it[1].toInt()) }
    val goal = Position(70, 70)

    main@ for (i in 1024 + 1..allBytes.lastIndex) {
        val bytes = allBytes.take(i)
        val visited = mutableSetOf<Position>()

        val queue = PriorityQueue<Node>(compareBy { it.stepCount })
        queue.add(Node(Position(0, 0), 0))

        fun tryAddToQueue(node: Node) {
            if (node.position in bytes) return
            if (node.position.x !in 0..goal.x) return
            if (node.position.y !in 0..goal.y) return

            queue.add(node)
        }

        while (!queue.isEmpty()) {
            val current = queue.poll()!!
            if (!visited.add(current.position)) continue

            if (current.position == goal) {
                continue@main
            }

            Direction.entries.forEach { direction ->
                tryAddToQueue(Node(current.position + direction.position, current.stepCount + 1))
            }
        }

        return allBytes[i - 1].let { (x, y) -> "$x,$y" }
    }
    return ""
}