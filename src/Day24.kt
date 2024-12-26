package day24

import common.readInput
import common.shouldBe
import java.util.*
import kotlin.math.pow

fun main() {
    part1(readInput("day24")) shouldBe 51745744348272
    part2(readInput("day24")) shouldBe "bfq,bng,fjp,hkh,hmt,z18,z27,z31"
}

val gateRegex = """(.+) (\w+) (.+) -> (.+)""".toRegex()

data class Gate(val left: String, val right: String, val out: String, val operation: Operation)

interface Operation {
    fun calculate(left: Boolean, right: Boolean): Boolean

    data object AND : Operation {
        override fun calculate(left: Boolean, right: Boolean) = left && right
    }

    data object XOR : Operation {
        override fun calculate(left: Boolean, right: Boolean) = left xor right
    }

    data object OR : Operation {
        override fun calculate(left: Boolean, right: Boolean) = left || right
    }

    companion object {
        fun fromString(string: String): Operation = when (string) {
            "AND" -> AND
            "OR" -> OR
            "XOR" -> XOR
            else -> error("wrong string")
        }
    }
}

fun part1(input: String): Long {
    val (initialValues, gates) = input.split("\r\n\r\n")
    val wires = initialValues.lines().associate {
        val key = it.substringBefore(":")
        val value = it.substringAfter(": ") == "1"
        key to value
    }.toMutableMap()

    val queue = ArrayDeque<Gate>()
    gates.lines().forEach {
        val (left, operation, right, out) = gateRegex.matchEntire(it)!!.destructured
        queue.add(Gate(left, right, out, Operation.fromString(operation)))
    }

    while (queue.isNotEmpty()) {
        val current = queue.poll()
        with(current) {
            if (wires.containsKey(left) && wires.containsKey(right))
                wires[out] = operation.calculate(wires[left]!!, wires[right]!!)
            else
                queue.add(current)
        }
    }

    return wires.filterKeys { it.startsWith("z") }.toSortedMap().values.foldIndexed(0) { i, acc, current ->
        if (current) acc + 2.0.pow(i.toDouble()).toLong() else acc
    }
}

fun part2(input: String): String {
    val (initialValues, unparsedGates) = input.split("\r\n\r\n")
    val wires = initialValues.lines().associate {
        val key = it.substringBefore(":")
        val value = it.substringAfter(": ") == "1"
        key to value
    }

    val gates = unparsedGates.lines().map {
        val (left, operation, right, out) = gateRegex.matchEntire(it)!!.destructured
        Gate(left, right, out, Operation.fromString(operation))
    }

    val bitCount = wires.keys.filter { it.startsWith("x") }.maxOf { it }.substringAfter("x").toInt() + 1

    val wrongGates = mutableSetOf<Gate>()
    gates.forEach { current ->
        if (current.out.startsWith("z") && current.operation != Operation.XOR &&
            !current.out.endsWith("z$bitCount")
        ) {
            wrongGates.add(current)
        }

        if (current.operation == Operation.AND && !(current.left.startsWith(("x00")) ||
                    current.left.startsWith(("y00"))) &&
            gates.any { (current.out == it.left || current.out == it.right) && it.operation != Operation.OR }
        ) {
            wrongGates.add(current)
        }

        if (current.operation == Operation.XOR && !current.out.startsWith("z") &&
            gates.any { (current.out == it.left || current.out == it.right) && it.operation == Operation.OR }
        ) {
            wrongGates.add(current)
        }


        if (!current.out.startsWith("z") && current.operation == Operation.XOR &&
            !(current.left.startsWith(("x")) || current.left.startsWith(("y")))
        ) {
            wrongGates.add(current)
        }
    }

    return wrongGates.map { it.out }.sorted().joinToString(",")
}
