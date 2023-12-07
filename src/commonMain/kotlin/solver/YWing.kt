package solver

import SudokuState
import allPossibilities
import colRow
import colRowSquares
import filterEmptyCells
import generator.SudokuRemoverMethod
import powerset
import theSameRowColumnSquare

object YWing : SudokuSolverMethod, SudokuRemoverMethod {
    override val name: String = "YWing"

    override fun apply(state: SudokuState): SudokuState? {
        val emptyCells = state.cells.filterEmptyCells()

        emptyCells
            .filter { (_, state) -> state.possibilities.size == 2 }
            .forEach { (yCellPos, yCellState) ->
                val numbersConsideredForPair = yCellState.possibilities
                val cellsInCol = emptyCells.filter { (pos, _) -> pos.col == yCellPos.col && pos != yCellPos }
                val (cellPairInCol, numberUsed) = numbersConsideredForPair
                    .firstNotNullOfOrNull { num ->
                        cellsInCol.filter { (_, state) -> num in state.possibilities }
                            .toList()
                            .singleOrNull()
                            ?.takeIf { (_, state) -> state.possibilities.size == 2 }
                            ?.let { it to num }
                    } ?: return@forEach
                val otherNumber = (numbersConsideredForPair - numberUsed).single()
                val cellsInRow = emptyCells.filter { (pos, _) -> pos.row == yCellPos.row && pos != yCellPos }
                val cellPairInRow = cellsInRow.filter { (_, state) -> otherNumber in state.possibilities }
                    .toList()
                    .singleOrNull()
                    ?.takeIf { (_, state) -> state.possibilities.size == 2 }
                    ?: return@forEach

                val numbersToRemove = cellPairInCol.second.possibilities.toSet()
                    .intersect(cellPairInRow.second.possibilities) - setOf(numberUsed, otherNumber)
                if (numbersToRemove.isEmpty()) return@forEach

                val intersectionCellPos = SudokuState.Position(cellPairInCol.first.row, cellPairInRow.first.col)
                val intersectionCellState = state.cells[intersectionCellPos]
                if (intersectionCellState is SudokuState.CellState.Empty && intersectionCellState.possibilities.intersect(numbersToRemove).isNotEmpty()) {
                    val newState = state.removePossibilities(setOf(intersectionCellPos), *numbersToRemove.toIntArray())
                    return newState
                }
            }
        return null
    }
}

