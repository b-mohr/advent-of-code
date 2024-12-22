package day17

import common.readInput
import common.shouldBe
import kotlin.math.pow

fun main() {
    part1(readInput("day17_test")) shouldBe "4,6,3,5,6,3,5,2,1,0"
    part1(readInput("day17")) shouldBe "2,7,2,5,1,2,7,3,7"
    part2(readInput("day17")) shouldBe 247839002892474
}

val programRegex = """Register A: (.+)\r\nRegister B: (.+)\r\nRegister C: (.+)\r\n\r\nProgram: (.+)""".toRegex()

fun part1(input: String): String {
    val (a,b,c,p) = programRegex.matchEntire(input)!!.destructured
    val computer = Computer(a.toLong(), b.toLong(), c.toLong())
    computer.runProgram(p.toList().filterNot { it == ','}.map(Char::digitToInt))
    return computer.output.joinToString(",")
}

fun part2(input: String): Long {
    val (_,b,c,p) = programRegex.matchEntire(input)!!.destructured
    val program = p.filterNot { it == ',' }.map(Char::digitToInt)

    var a = 8.0.pow(p.length / 2).toLong()

    main@ while (true) {
        val computer = Computer(a, b.toLong(), c.toLong())
        computer.runProgram(program)

        for (i in program.lastIndex downTo 0) {
            if (program[i] != computer.output[i]) {
                a += if (i == 0) 1 else 8.0.pow(i-1).toLong()
                continue@main
            }
        }
        return a
    }
}

class Computer(private var a: Long, private var b: Long, private var c: Long) {
    val output = mutableListOf<Int>()
    private var instructionPointer = 0

    fun runProgram(program: List<Int>) {
        while (true) {
            if(instructionPointer !in program.indices) break

            when (program[instructionPointer]) {
                0 -> adv(program[instructionPointer+1])
                1 -> bxl(program[instructionPointer+1])
                2 -> bst(program[instructionPointer+1])
                3 -> jnz(program[instructionPointer+1])
                4 -> bxc(program[instructionPointer+1])
                5 -> out(program[instructionPointer+1])
                6 -> bdv(program[instructionPointer+1])
                7 -> cdv(program[instructionPointer+1])
            }

            instructionPointer += 2
        }
    }

    private fun adv(operant: Int) {
        a = (a / 2.0.pow(valueOfComboOperand(operant).toDouble())).toLong()
    }

    private fun bxl(operant: Int) {
        b = b xor operant.toLong()
    }

    private fun bst(operant: Int) {
        b = valueOfComboOperand(operant) % 8
    }

    private fun jnz(operant: Int) {
        if (a == 0L) return
        instructionPointer = operant - 2
    }

    private fun bxc(operant: Int) {
        b = b xor c
    }

    private fun out(operant: Int) {
        output += (valueOfComboOperand(operant) % 8L).toInt()
    }

    private fun bdv(operant: Int) {
        b = (a / 2.0.pow(valueOfComboOperand(operant).toDouble())).toLong()
    }

    private fun cdv(operant: Int) {
        c = (a / 2.0.pow(valueOfComboOperand(operant).toDouble())).toLong()
    }

    private fun valueOfComboOperand(operand: Int): Long = when(operand) {
        0,1,2,3 -> operand.toLong()
        4 -> a
        5 -> b
        6 -> c
        else -> error("wrong operand")
    }
}