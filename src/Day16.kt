package day16

import common.*
import java.util.PriorityQueue

fun main() {
    part1(readLines("day16_test")) shouldBe 11048
    part1(readLines("day16")) shouldBe 99448
    part2(readLines("day16_test")) shouldBe 64
    part2(readLines("day16")) shouldBe 498
}

data class Node(val position: Position, val direction: Direction, val cost: Int)

fun part1(input: List<String>): Int {
    lateinit var start: Position
    lateinit var end: Position
    val walls = mutableSetOf<Position>()
    input.forEachIndexed { y, row ->
        row.mapIndexed { x, char ->
            when (char) {
                '#' -> walls.add(Position(x, y))
                'S' -> start = Position(x, y)
                'E' -> end = Position(x, y)
                else -> {}
            }
        }
    }

    val queue = PriorityQueue<Node> { n1, n2 -> n1.cost - n2.cost }
    queue.add(Node(start, Direction.Right, 0))

    fun tryAddToQueue(position: Position, direction: Direction, cost: Int) {
        if (position in walls) return
        queue.add(Node(position, direction, cost))
    }

    val visited = mutableSetOf<Pair<Position, Direction>>()
    while (queue.isNotEmpty()) {
        val current = queue.remove()!!

        if (current.position == end) return current.cost

        if (!visited.add(current.position to current.direction)) {
            continue
        }

        tryAddToQueue(current.position + current.direction.position, current.direction, current.cost + 1)
        tryAddToQueue(current.position, current.direction.rotateLeft(), current.cost + 1000)
        tryAddToQueue(current.position, current.direction.rotateRight(), current.cost + 1000)
    }

    return -1
}

data class NodeWithRoute(val position: Position, val direction: Direction, val cost: Int, val route: List<Position>)

fun part2(input: List<String>): Int {
    val bestPathCost = part1(input)

    lateinit var start: Position
    lateinit var end: Position
    val walls = mutableSetOf<Position>()
    input.forEachIndexed { y, row ->
        row.mapIndexed { x, char ->
            when (char) {
                '#' -> walls.add(Position(x, y))
                'S' -> start = Position(x, y)
                'E' -> end = Position(x, y)
                else -> {}
            }
        }
    }

    val queue = PriorityQueue<NodeWithRoute> { n1, n2 -> n1.cost - n2.cost }
    queue.add(NodeWithRoute(start, Direction.Right, 0, listOf(start)))

    fun tryAddToQueue(position: Position, direction: Direction, cost: Int, route: List<Position>) {
        if (position in walls) return
        queue.add(NodeWithRoute(position, direction, cost, route))
    }

    val bestPathNodes = mutableSetOf<Position>()
    val visited = mutableMapOf<Pair<Position, Direction>, Int>()
    while (queue.isNotEmpty()) {
        val current = queue.remove()!!

        if (current.position == end && bestPathCost == current.cost) {
            bestPathNodes += current.route
            continue
        }

        val lastVisitCost = visited[current.position to current.direction]
        if (lastVisitCost != null && lastVisitCost < current.cost) {
            continue
        }
        visited[current.position to current.direction] = current.cost

        tryAddToQueue(
            current.position + current.direction.position,
            current.direction,
            current.cost + 1,
            current.route + listOf(current.position + current.direction.position)
        )
        tryAddToQueue(current.position, current.direction.rotateLeft(), current.cost + 1000, current.route)
        tryAddToQueue(current.position, current.direction.rotateRight(), current.cost + 1000, current.route)
    }

    return bestPathNodes.size
}
