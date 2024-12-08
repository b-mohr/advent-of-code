import kotlin.io.path.Path
import kotlin.io.path.readText

fun readInput(name: String) = Path("input/$name.txt").readText().trim()
fun readLines(name: String) = Path("input/$name.txt").readText().trim().lines()

infix fun <T> T.shouldBe(expected: T) {
    check(this == expected) {
        "expected: $expected but was: $this"
    }
}