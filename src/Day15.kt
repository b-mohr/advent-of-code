package day15

import common.*

fun main() {
    part1(readLines("day15_test")) shouldBe 10092
    part1(readLines("day15")) shouldBe 1475249
    part2(readLines("day15_test")) shouldBe 9021
    part2(readLines("day15")) shouldBe 1509724
}

interface Structure {
    data object Wall : Structure
    data object Box : Structure
    data class WideBox(val left: Position, val right: Position) : Structure
}

class Warehouse {
    private val map: MutableMap<Int, MutableMap<Int, Structure>> = mutableMapOf()
    private lateinit var robot: Position

    fun addWall(position: Position) {
        addStructure(Structure.Wall, position)
    }

    fun addBox(position: Position) {
        addStructure(Structure.Box, position)
    }

    fun addWideBox(leftPosition: Position, rightPosition: Position) {
        val box = Structure.WideBox(leftPosition, rightPosition)
        addStructure(box, leftPosition)
        addStructure(box, rightPosition)
    }

    fun setRobotPosition(position: Position) {
        robot = position
    }

    fun moveRobot(direction: Direction) {
        val nextPosition = robot + direction.position
        when (val structure = getStructure(nextPosition)) {
            null -> robot = nextPosition
            is Structure.Wall -> {}
            is Structure.Box ->
                if (tryMoveBox(nextPosition, direction)) {
                    robot = nextPosition
                }

            is Structure.WideBox ->
                if (tryMoveWideBox(structure.left, structure.right, direction)) {
                    robot = nextPosition
                }
        }
    }

    private fun tryMoveBox(position: Position, direction: Direction): Boolean {
        val nextPosition = position + direction.position
        val nextStructure = getStructure(nextPosition)

        when (nextStructure) {
            null -> {
                switchBoxPosition(position, nextPosition)
                return true
            }

            is Structure.Wall -> return false
            is Structure.Box -> {
                if (tryMoveBox(nextPosition, direction)) {
                    switchBoxPosition(position, nextPosition)
                    return true
                } else {
                    return false
                }
            }
        }
        return false
    }

    private fun switchBoxPosition(oldPosition: Position, newPosition: Position) {
        removeStructure(oldPosition)
        addStructure(Structure.Box, newPosition)
    }

    private fun tryMoveWideBox(positionLeft: Position, positionRight: Position, direction: Direction): Boolean {
        if (canMoveWideBox(positionLeft, positionRight, direction)) {
            moveWideBox(positionLeft, positionRight, direction)
            return true
        }

        return false
    }

    private fun canMoveWideBox(positionLeft: Position, positionRight: Position, direction: Direction): Boolean {
        val nextPositionLeft = positionLeft + direction.position
        val nextStructureLeft = getStructure(nextPositionLeft)

        val nextPositionRight = positionRight + direction.position
        val nextStructureRight = getStructure(nextPositionRight)

        if (direction == Direction.Up || direction == Direction.Down) {
            if (nextStructureLeft is Structure.Wall || nextStructureRight is Structure.Wall) return false

            if (nextStructureLeft is Structure.WideBox || nextStructureRight is Structure.WideBox) {
                if (nextStructureLeft == nextStructureRight) {
                    return canMoveWideBox(nextPositionLeft, nextPositionRight, direction)
                }


                if (nextStructureLeft is Structure.WideBox
                    && !canMoveWideBox(nextPositionLeft.copy(x = nextPositionLeft.x - 1), nextPositionLeft, direction)
                )
                    return false

                if (nextStructureRight is Structure.WideBox
                    && !canMoveWideBox(
                        nextPositionRight,
                        nextPositionRight.copy(x = nextPositionRight.x + 1),
                        direction
                    )
                )
                    return false
            }

        } else {
            val nextStructure = if (direction == Direction.Left) nextStructureLeft else nextStructureRight

            when (nextStructure) {
                null -> return true
                is Structure.Wall -> return false
                is Structure.WideBox -> return canMoveWideBox(nextPositionLeft, nextPositionRight, direction)
            }
        }

        return true
    }

    private fun moveWideBox(positionLeft: Position, positionRight: Position, direction: Direction) {
        val nextPositionLeft = positionLeft + direction.position
        val nextStructureLeft = getStructure(nextPositionLeft)

        val nextPositionRight = positionRight + direction.position
        val nextStructureRight = getStructure(nextPositionRight)

        if (direction == Direction.Up || direction == Direction.Down) {
            if (nextStructureLeft is Structure.WideBox || nextStructureRight is Structure.WideBox) {
                if (nextStructureLeft == nextStructureRight) {
                    moveWideBox(nextPositionLeft, nextPositionRight, direction)
                } else {
                    if (nextStructureLeft is Structure.WideBox) {
                        moveWideBox(nextPositionLeft.copy(x = nextPositionLeft.x - 1), nextPositionLeft, direction)
                    }

                    if (nextStructureRight is Structure.WideBox) {
                        moveWideBox(nextPositionRight, nextPositionRight.copy(x = nextPositionRight.x + 1), direction)
                    }
                }

            }
        } else {
            if (direction == Direction.Left && nextStructureLeft is Structure.WideBox)
                moveWideBox(nextPositionLeft.copy(x = nextPositionLeft.x - 1), nextPositionLeft, direction)
            if (direction == Direction.Right && nextStructureRight is Structure.WideBox) {
                moveWideBox(nextPositionRight, nextPositionRight.copy(x = nextPositionRight.x + 1), direction)
            }
        }

        removeStructure(positionLeft)
        removeStructure(positionRight)
        addWideBox(positionLeft + direction.position, positionRight + direction.position)
    }


    private fun getStructure(position: Position): Structure? {
        return map[position.y]?.get(position.x)
    }

    private fun addStructure(structure: Structure, position: Position) {
        map.getOrPut(position.y) { mutableMapOf() }[position.x] = structure
    }

    private fun removeStructure(position: Position) {
        map[position.y]!!.remove(position.x)
    }

    fun calculateSumOfGpsCoordinates(): Long {
        var result = 0L
        map.forEach { (y, row) ->
            row.forEach { (x, structure) ->
                if (structure == Structure.Box) {
                    result += y * 100 + x
                }

                if (structure is Structure.WideBox && structure.left.x == x) {
                    result += y * 100 + x
                }
            }
        }
        return result
    }
}

fun part1(input: List<String>): Long {
    val world = Warehouse()
    input.mapIndexed { y, line ->
        line.mapIndexed { x, char ->
            val pos = Position(x, y)
            when (char) {
                '#' -> world.addWall(pos)
                'O' -> world.addBox(pos)
                '@' -> world.setRobotPosition(pos)
                'v' -> world.moveRobot(Direction.Down)
                '>' -> world.moveRobot(Direction.Right)
                '<' -> world.moveRobot(Direction.Left)
                '^' -> world.moveRobot(Direction.Up)
            }
        }
    }

    return world.calculateSumOfGpsCoordinates()
}

fun part2(input: List<String>): Long {
    val world = Warehouse()
    input.mapIndexed { y, line ->
        line.mapIndexed { x, char ->
            val pos1 = Position(x * 2, y)
            val pos2 = pos1.copy(x = pos1.x + 1)
            when (char) {
                '#' -> {
                    world.addWall(pos1)
                    world.addWall(pos2)
                }

                'O' -> world.addWideBox(pos1, pos2)
                '@' -> world.setRobotPosition(pos1)
                'v' -> world.moveRobot(Direction.Down)
                '>' -> world.moveRobot(Direction.Right)
                '<' -> world.moveRobot(Direction.Left)
                '^' -> world.moveRobot(Direction.Up)
            }
        }
    }

    return world.calculateSumOfGpsCoordinates()
}