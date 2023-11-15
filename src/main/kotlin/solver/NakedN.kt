package solver

import SudokuState
import colRowSquares
import filterEmptyCells
import generator.SudokuRemoverMethod
import powerset

object NakedN : SudokuSolverMethod, SudokuRemoverMethod {
    override val name: String = "NakedN"

    override fun apply(state: SudokuState): SudokuState? {
        state.cells
            .filterEmptyCells()
            .colRowSquares()
            .forEach { cells ->
                val numbers = cells.fold(setOf<Int>()) { acc, e -> acc + e.second.possibilities }
                // Find n numbers that are the only options for n cells only
                val combinations = numbers.powerset()
                    .filter { it.size > 2 } // 1 is covered by OnlyPossibility, 2 by NakedPair
                    .sortedBy { it.size }

                for (numberCombination in combinations) {
                    val cellsWithOnlyThisNumbers = cells.filter { (_, state) -> state.possibilities.all { it in numberCombination } }
                    if (cellsWithOnlyThisNumbers.size == numberCombination.size) {
                        val nakedPositions = cellsWithOnlyThisNumbers.map { it.first }

                        val otherPositions = cells
                            .filter { (pos, state) ->
                                pos !in nakedPositions && state.possibilities.any { it in numberCombination }
                            }
                            .map { it.first }
                            .toSet()
                        if (otherPositions.isEmpty()) return@forEach
                        val newState = state.removePossibilities(otherPositions, *numberCombination.toIntArray())
                        return newState
                    }
                }
            }
        return null
    }
}

