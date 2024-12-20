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