package solver

import SudokuState
import allRepetitions
import colRowSquares
import filterEmptyCells
import generator.SudokuRemoverMethod

object NakedPair : SudokuSolverMethod, SudokuRemoverMethod {
    override val name: String = "NakedPair"

    override fun apply(state: SudokuState): SudokuState? {
        state.cells
            .filterEmptyCells()
            .colRowSquares()
            .forEach { group ->
                group.map { (_, cell) -> cell.possibilities }
                    .filter { it.size == 2 }
                    .allRepetitions()
                    .forEach { repeatingPair ->
                        val otherPositions = group
                            .filter {
                                it.second.possibilities != repeatingPair && it.second.possibilities.any { it in repeatingPair }
                            }
                            .map { it.first }
                            .toSet()
                        if (otherPositions.isEmpty()) return@forEach
                        val newState = state.removePossibilities(otherPositions, *repeatingPair.toIntArray())
                        return newState
                    }
            }
        return null
    }
}

