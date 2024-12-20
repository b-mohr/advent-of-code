package day12

import common.readLines
import common.shouldBe

fun main() {
    part1(readLines("day12_test")) shouldBe 1930
    part1(readLines("day12")) shouldBe 1433460
    part2(readLines("day12_test")) shouldBe 1206
    part2(readLines("day12")) shouldBe 855082
}

fun part1(input: List<String>): Int = Solver(input).solve(Solver.RegionCalculator::calculateResult1)
fun part2(input: List<String>): Int = Solver(input).solve(Solver.RegionCalculator::calculateResult2)

data class Position(val x: Int, val y: Int)
data class Plot(val pos: Position, val plant: Char) {
    val x = pos.x
    val y = pos.y

    constructor(x: Int, y: Int, plant: Char) : this(Position(x, y), plant)
}

enum class Direction(val x: Int, val y: Int) {
    NORTH(0, -1), SOUTH(0, 1), EAST(1, 0), WEST(-1, 0)
}

class Solver(private val input: List<String>) {
    var processedPlots = mutableListOf<Plot>()

    fun solve(calculateRegion: RegionCalculator.() -> Int): Int {
        val garden = Garden(input.mapIndexed { y, row ->
            row.mapIndexed { x, plot ->
                Plot(x, y, plot)
            }
        })

        return garden.sumOf { plot ->
            if (plot in processedPlots) return@sumOf 0

            val region = RegionCalculator()
            region.processPlot(garden, plot)
            return@sumOf region.calculateRegion()
        }
    }

    inner class RegionCalculator {
        private var plotCount = 0

        fun calculateResult1(): Int {
            return sides.values.sumOf { side -> side.values.sumOf { it.size } } * plotCount
        }

        fun calculateResult2(): Int {
            return calculateUniqueSides() * plotCount
        }

        private fun calculateUniqueSides(): Int {
            var result = 0
            sides.values.forEach { direction ->
                direction.values.forEach { list ->
                    result++
                    list.sorted().windowed(2).forEach { (first, second) ->
                        if (second - first > 1) {
                            result++
                        }
                    }
                }
            }
            return result
        }

        private var sides = mapOf<Direction, MutableMap<Int, MutableList<Int>>>(
            Direction.NORTH to mutableMapOf(),
            Direction.EAST to mutableMapOf(),
            Direction.SOUTH to mutableMapOf(),
            Direction.WEST to mutableMapOf(),
        )

        fun processPlot(garden: Garden, plot: Plot) {
            if (plot in processedPlots) return

            processedPlots += plot
            plotCount++

            Direction.entries.forEach { direction ->
                if (!garden.hasNeighbor(plot, direction)) {
                    when (direction) {
                        Direction.NORTH, Direction.SOUTH -> sides[direction]!!.getOrPut(plot.y) { mutableListOf() }
                            .add(plot.x)

                        Direction.EAST, Direction.WEST -> sides[direction]!!.getOrPut(plot.x) { mutableListOf() }
                            .add(plot.y)
                    }
                }

                garden.getNeighborOrNull(plot, direction)?.let {
                    processPlot(garden, it)
                }
            }
        }

    }
}

class Garden(private val internalMap: List<List<Plot>>) : Iterable<Plot> {

    fun hasNeighbor(plot: Plot, direction: Direction): Boolean {
        return internalMap.getOrNull(plot.y + direction.y)?.getOrNull(plot.x + direction.x)?.plant == plot.plant
    }

    fun getNeighborOrNull(plot: Plot, direction: Direction): Plot? {
        return internalMap.getOrNull(plot.y + direction.y)?.getOrNull(plot.x + direction.x).let {
            if (it?.plant == plot.plant) it else null
        }
    }

    override fun iterator(): Iterator<Plot> {
        return MapIterator(internalMap)
    }

    class MapIterator(private val plots: List<List<Plot>>) : Iterator<Plot> {
        private var x = -1
        private var y = 0

        override fun hasNext(): Boolean = y < plots.lastIndex || x < plots[y].lastIndex

        override fun next(): Plot {
            if (x < plots[y].lastIndex) {
                x++
            } else {
                y++
                x = 0
            }
            return plots[y][x]
        }
    }
}