package solver

import SudokuState
import generator.SudokuRemoverMethod

object InRowInSquare : SudokuSolverMethod, SudokuRemoverMethod {
    override val name: String = "InRowInSquare"

    override fun apply(state: SudokuState): SudokuState? {
        val emptyCells = state.cells
            .toList()
            .filter { it.second is SudokuState.CellState.Empty }

        val squares = emptyCells.groupBy { it.first.squareId }.values
            .shuffled()

        squares
            .filter { it.size >= 2 }
            .forEach { square ->
                SudokuState.ALL_NUMBERS.shuffled().forEach {  number ->
                    val cellsWithNumber = square.filter { (_, cell) -> number in (cell as SudokuState.CellState.Empty).possibilities }
                    if (cellsWithNumber.size < 2) return@forEach
                    val singleRow = cellsWithNumber.map { it.first.row }.toSet().singleOrNull()
                    val singleCol = cellsWithNumber.map { it.first.col }.toSet().singleOrNull()
                    if (singleRow != null) {
                        val otherPositionsInRow = ((0..8).toSet() - square.filter { it.first.row == singleRow }.map { it.first.col }.toSet())
                            .map { SudokuState.Position(singleRow, it) }
                            .toSet()
                        if (!otherPositionsInRow.map { state.cells[it] }.any { (it is SudokuState.CellState.Empty) && number in it.possibilities }) {
                            return@forEach
                        }

                        val newState = state.removePossibilities(otherPositionsInRow, number)
                        return newState
                    }
                    if (singleCol != null) {
                        val otherPositionsInCol = ((0..8).toSet() - square.filter { it.first.col == singleCol }.map { it.first.row }.toSet())
                            .map { SudokuState.Position(it, singleCol) }
                            .toSet()
                        if (!otherPositionsInCol.map { state.cells[it] }.any { (it is SudokuState.CellState.Empty) && number in it.possibilities }) {
                            return@forEach
                        }

                        val newState = state.removePossibilities(otherPositionsInCol, number)
                        return newState
                    }
                }
            }
        return null
    }
}
