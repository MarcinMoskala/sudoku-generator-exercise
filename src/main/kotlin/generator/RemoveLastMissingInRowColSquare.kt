package generator

import SudokuState

object RemoveLastMissingInRowColSquare : SudokuRemoverMethod {
    override val name: String = "RemoveLastMissingInRowColSquare"
    private val allNumbers = SudokuState.ALL_NUMBERS.size

    override fun apply(state: SudokuState): SudokuState? {
        val filledCells = state.cells
            .toList()
            .filter { it.second is SudokuState.CellState.Filled }

        val rowsColsSquares = filledCells.groupBy { it.first.row }.values +
                filledCells.groupBy { it.first.col }.values +
                filledCells.groupBy { it.first.squareId }.values

        val cellToRemove: Pair<SudokuState.Position, SudokuState.CellState> = rowsColsSquares
            .filter { it.size == allNumbers }
            .randomOrNull()
            ?.random()
            ?: return null

        return state.withEmptyUpdatePoss(cellToRemove.first)
    }
}
