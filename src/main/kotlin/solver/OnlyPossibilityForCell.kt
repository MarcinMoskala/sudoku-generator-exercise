package solver

import SudokuState

object OnlyPossibilityForCell : SudokuSolverMethod {
    override val name: String = "OnlyPossibilityForCell"

    override fun apply(state: SudokuState): SudokuState? {
        return state.cells
            .asSequence()
            .filter { it.value is SudokuState.CellState.Empty }
            .shuffled()
            .mapNotNull { (pos, value) ->
                if (value as? SudokuState.CellState.Empty == null) return@mapNotNull null
                val onlyCorrectValue = value.possibilities.takeIf { it.size == 1 }?.single() ?: return@mapNotNull null
                state.withValueAndUpdatedPoss(pos, onlyCorrectValue)
            }
            .firstOrNull()
    }
}
