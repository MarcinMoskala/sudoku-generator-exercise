package solver

import SudokuState
import allPossibilities
import colRowSquares
import filterEmptyCells
import generator.SudokuRemoverMethod
import powerset

object HiddenN : SudokuSolverMethod, SudokuRemoverMethod {
    override val name: String = "HiddenN"

    override fun apply(state: SudokuState): SudokuState? {
        state.cells
            .filterEmptyCells()
            .colRowSquares()
            .forEach { cells ->
                val numbers = cells.allPossibilities()
                // Find n numbers that are the only options for n cells only
                numbers.powerset()
                    .filter { it.size > 2 } // 1 is covered by LastMissingInRowColSquare, 2 by HiddenPair
                    .sortedBy { it.size }
                    .forEach { numberCombination ->
                        val cellsThatHaveThisNumbers =
                            cells.filter { (_, state) -> numberCombination.any { it in state.possibilities } }
                        if (cellsThatHaveThisNumbers.size == numberCombination.size) {
                            val otherNumbers = cellsThatHaveThisNumbers.allPossibilities() - numberCombination
                            if (otherNumbers.isEmpty()) return@forEach
                            val hiddenPositions = cellsThatHaveThisNumbers.map { it.first }.toSet()
                            val newState = state.removePossibilities(hiddenPositions, *otherNumbers.toIntArray())
                            return newState
                        }
                    }
            }
        return null
    }
}

