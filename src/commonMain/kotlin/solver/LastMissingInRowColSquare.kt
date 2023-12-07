package solver

import SudokuState

object LastMissingInRowColSquare : SudokuSolverMethod {
    override val name: String = "LastMissingInRowColSquare"

    override fun apply(state: SudokuState): SudokuState? {
        val filledCells = state.cells
            .toList()
            .filter { it.second is SudokuState.CellState.Empty }

        val rowsColsSquares = listOf(
            filledCells.groupBy { it.first.row }.values,
            filledCells.groupBy { it.first.col }.values,
            filledCells.groupBy { it.first.squareId }.values,
        ).flatten()

        val (pos, newState) = rowsColsSquares
            .mapNotNull { it.singleOrNull() }
            .randomOrNull()
            ?: return null

        return state.withValueAndUpdatedPoss(pos, (newState as SudokuState.CellState.Empty).possibilities.single())
    }

}
