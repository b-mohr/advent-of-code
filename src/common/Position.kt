package common

data class Position(val x: Int, val y: Int) {
    operator fun plus(other: Position): Position = Position(this.x + other.x, this.y + other.y)
}

enum class Direction(val position: Position) {
    Up(Position(0, -1)),
    Down(Position(0, 1)),
    Left(Position(-1, 0)),
    Right(Position(1, 0));
}

fun Direction.rotateLeft(): Direction = when (this) {
    Direction.Up -> Direction.Left
    Direction.Left -> Direction.Down
    Direction.Down -> Direction.Right
    Direction.Right -> Direction.Up
}

fun Direction.rotateRight(): Direction = when (this) {
    Direction.Up -> Direction.Right
    Direction.Right -> Direction.Down
    Direction.Down -> Direction.Left
    Direction.Left -> Direction.Up
}