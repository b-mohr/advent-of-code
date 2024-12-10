fun main() {
    part1(readLines("day06_test")) shouldBe 41
    part1(readLines("day06")) shouldBe 4665

    part2(readLines("day06_test")) shouldBe 6
    part2(readLines("day06")) shouldBe 1688
}

private fun part1(input: List<String>): Int {
    val (lab, guard) = input.parse()
    return guard.patrol(lab)
}

private fun part2(input: List<String>): Int {
    val (lab, guard) = input.parse()
    return lab.labsWithExtraObstacle().count {
        guard.copy().patrol(it) == -1
    }
}

private fun List<String>.parse(): Pair<Lab, Guard> {
    lateinit var guard: Guard
    val cells: List<List<Cell>> = mapIndexed { x, row ->
        row.mapIndexed { y, cell ->
            when (cell) {
                '.' -> VisitableCell(false)
                '#' -> Obstruction
                else -> VisitableCell(true).also {
                    guard = Guard(x, y, Direction.fromChar(cell))
                }
            }
        }
    }
    return Lab(cells) to guard
}

private class Lab(val cells: List<List<Cell>>) {
    fun getCellOrNull(position: Position): Cell? {
        return cells.getOrNull(position.x)?.getOrNull(position.y)
    }

    fun labsWithExtraObstacle(): Sequence<Lab> = sequence {
        cells.forEachIndexed { x, row ->
            row.forEachIndexed { y, cell ->
                if (cell is VisitableCell && !cell.isStartingPosition) {
                    yield(
                        Lab(
                        cells.mapIndexed { tx, trow ->
                            trow.mapIndexed { ty, tcell ->
                                if (tx == x && ty == y) Obstruction else tcell.reset()
                            }
                        }
                    ))
                }
            }
        }
    }

    val visitedCells
        get() = cells.sumOf { it.count { cell -> cell is VisitableCell && cell.isVisited } }
}

private class Guard(x: Int, y: Int, var direction: Direction) {
    var position = Position(x, y)

    fun patrol(lab: Lab): Int {
        while (true) {
            move(lab)?.let {
                return when (it) {
                    MoveResult.OutOfBounds -> lab.visitedCells
                    MoveResult.Loop -> -1
                }
            }
        }
    }

    fun copy(): Guard = Guard(position.x, position.y, direction)

    enum class MoveResult { Loop, OutOfBounds }

    private fun move(lab: Lab): MoveResult? {
        val nextPosition = nextPosition()
        val cell = lab.getCellOrNull(nextPosition) ?: return MoveResult.OutOfBounds

        when (cell) {
            is Obstruction -> direction = nextDirection()
            is VisitableCell -> {
                if (cell.visit(direction)) {
                    position = nextPosition
                } else {
                    return MoveResult.Loop
                }
            }
        }

        return null
    }

    private fun nextDirection(): Direction = when (direction) {
        Direction.North -> Direction.East
        Direction.East -> Direction.South
        Direction.South -> Direction.West
        Direction.West -> Direction.North
    }

    private fun nextPosition(): Position = with(position) {
        when (direction) {
            Direction.North -> Position(x - 1, y)
            Direction.East -> Position(x, y + 1)
            Direction.South -> Position(x + 1, y)
            Direction.West -> Position(x, y - 1)
        }
    }

}

private data class Position(val x: Int, val y: Int)

private enum class Direction {
    North, East, South, West;

    companion object {
        fun fromChar(char: Char): Direction {
            return when (char) {
                '^' -> North
                'v' -> South
                '>' -> East
                '<' -> West
                else -> throw IllegalArgumentException()
            }
        }
    }
}

private sealed interface Cell {
    fun reset(): Cell
}

private data object Obstruction : Cell {
    override fun reset() = this
}

private class VisitableCell(val isStartingPosition: Boolean) : Cell {
    val visitedDirections = mutableSetOf<Direction>()
    val isVisited
        get() = isStartingPosition || visitedDirections.isNotEmpty()

    fun visit(direction: Direction): Boolean = visitedDirections.add(direction)

    override fun reset(): VisitableCell {
        visitedDirections.clear()
        return this
    }
}