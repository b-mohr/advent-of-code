package day14

import common.readLines
import common.shouldBe

fun main() {
    part1(readLines("day14_test")) shouldBe 12
    part1(readLines("day14"), Position(101, 103)) shouldBe 230686500
    println(part2(readLines("day14")))
}

val robotRegex = """p=(.+),(.+) v=(.+),(.+)""".toRegex()

fun List<String>.parse(): List<Robot> = this.map { robotString ->
    val (px, py, vx, vy) = robotRegex.matchEntire(robotString)!!.destructured
    Robot(Position(px.toInt(), py.toInt()), Position(vx.toInt(), vy.toInt()))
}

data class Position(val x: Int, val y: Int)
data class Robot(var position: Position, val velocity: Position)

fun Robot.move(gridSize: Position) {
    val newX = (position.x + velocity.x).teleport(gridSize.x)
    val newY = (position.y + velocity.y).teleport(gridSize.y)
    position = Position(newX, newY)
}

fun Int.teleport(max: Int): Int {
    if (this < 0) return max + this
    if (this >= max) return this - max
    return this
}

fun calculateSafetyFactor(robots: List<Robot>, gridSize: Position): Int {
    val lowerMid = Position(gridSize.x / 2 - 1, gridSize.y / 2 - 1)
    val upperMid = Position(lowerMid.x + 2, lowerMid.y + 2)
    return countRobots(robots, start = Position(0, 0), end = lowerMid) *
            countRobots(robots, start = upperMid, end = Position(gridSize.x - 1, gridSize.y - 1)) *
            countRobots(robots, start = Position(0, upperMid.y), end = Position(lowerMid.x, gridSize.y - 1)) *
            countRobots(robots, start = Position(upperMid.x, 0), end = Position(gridSize.x - 1, lowerMid.y))
}

fun countRobots(robots: List<Robot>, start: Position, end: Position): Int {
    val result = robots.count {
        it.position.x in start.x..end.x && it.position.y in start.y..end.y
    }
    return result
}

fun part1(input: List<String>, gridSize: Position = Position(11, 7)): Int {
    val robots = input.parse()

    robots.forEach { robot ->
        repeat(100) {
            robot.move(gridSize)
        }
    }

    return calculateSafetyFactor(robots, gridSize)
}

fun part2(input: List<String>) {
    val gridSize = Position(101, 103)
    val robots = input.parse()

    var lowestSafetyFactor = 230686500
    var count = 0

    while (true) {
        count++
        robots.forEach { it.move(gridSize) }

        val currentSafetyFactor = calculateSafetyFactor(robots, gridSize)

        if (currentSafetyFactor < lowestSafetyFactor) {
            lowestSafetyFactor = currentSafetyFactor

            println("Lower safety factor found after $count iterations:")
            printGrid(robots, gridSize)
        }
    }
}

fun printGrid(robots: List<Robot>, gridSize: Position) {
    val tree = List(gridSize.y) { MutableList(gridSize.x) { "." } }

    robots.forEach { robot ->
        tree[robot.position.y][robot.position.x] = "#"
    }

    tree.forEach {
        println(it.fold("") { r, t -> r + t })
    }
}
