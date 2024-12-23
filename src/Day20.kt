package day20

import common.Direction
import common.Position
import common.readLines
import common.shouldBe
import java.util.PriorityQueue
import kotlin.math.abs

fun main() {
    solve(readLines("day20"), 2) shouldBe 1346
    solve(readLines("day20"), 20) shouldBe 985482
}

fun solve(input: List<String>, cheatDuration: Int, threshold: Int = 100): Int {
    val (start, end, walls) = parseInput(input)
    val fastestTimesMap = calculateFastestTimes(start, end, walls)
    val fastestTimeFromStart = fastestTimesMap[start]!!

    fun getCheatEndPositions(start: Position, cheatDuration: Int): List<Pair<Position, Int>> {
        return fastestTimesMap.keys.mapNotNull {
            val distance = abs(it.x - start.x) + abs(it.y - start.y)
            if (distance in 1..cheatDuration) it to distance else null
        }
    }

    val queue = PriorityQueue<Node>(compareBy { it.time })
    fun tryAddToQueue(node: Node) {
        if (node.position in walls) return
        queue.add(node)
    }
    queue.add(Node(start, 0))

    val visited = mutableSetOf<Position>()
    val result = mutableSetOf<Pair<Position, Position>>()

    while (queue.isNotEmpty()) {
        val current = queue.poll()!!

        if (!visited.add(current.position)) continue

        getCheatEndPositions(current.position, cheatDuration).forEach { (endPoint, distance) ->
            fastestTimesMap[endPoint]?.let {
                if (current.time + it + distance <= fastestTimeFromStart - threshold) {
                    result.add(current.position to endPoint)
                }
            }
        }

        if (current.position == end) {
            break
        }

        Direction.entries.forEach { direction ->
            val nextPosition = current.position + direction.position
            tryAddToQueue(Node(nextPosition, current.time + 1))
        }
    }
    return result.size
}

data class Node(val position: Position, val time: Int)

fun calculateFastestTimes(start: Position, end: Position, walls: Set<Position>): Map<Position, Int> {
    val queue = PriorityQueue<Node>(compareBy { it.time })

    fun tryAddToQueue(node: Node) {
        if (node.position in walls) return
        queue.add(node)
    }

    queue.add(Node(start, 0))

    val visited = mutableMapOf<Position, Int>()
    var bestTime = -1

    while (queue.isNotEmpty()) {
        val current = queue.poll()!!

        if (visited.containsKey(current.position))
            continue
        visited[current.position] = current.time

        if (current.position == end) {
            bestTime = current.time
            break
        }

        Direction.entries.forEach { direction ->
            tryAddToQueue(Node(current.position + direction.position, current.time + 1))
        }
    }

    visited.replaceAll { _, value ->
        bestTime - value
    }

    return visited
}


fun parseInput(input: List<String>): Triple<Position, Position, Set<Position>> {
    lateinit var start: Position
    lateinit var end: Position
    val walls = mutableSetOf<Position>()

    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, cell ->
            when (cell) {
                '#' -> walls.add(Position(x, y))
                'S' -> start = Position(x, y)
                'E' -> end = Position(x, y)
            }
        }
    }

    return Triple(start, end, walls)
}