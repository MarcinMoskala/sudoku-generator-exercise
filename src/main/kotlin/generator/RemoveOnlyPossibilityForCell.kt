package generator

import SudokuState

object RemoveOnlyPossibilityForCell : SudokuRemoverMethod {
    override val name: String = "RemoveOnlyPossibilityForCell"

    override fun apply(state: SudokuState): SudokuState? {
        return state.cells
            .asSequence()
            .filter { it.value is SudokuState.CellState.Filled }
            .shuffled()
            .mapNotNull { (pos, original) ->
                val stateAfterRemoval = state.withEmptyUpdatePoss(pos)
                val originalAsSet = setOf((original as SudokuState.CellState.Filled).value)
                val isOnlyPossibility = (stateAfterRemoval.cells[pos] as? SudokuState.CellState.Empty)?.possibilities == originalAsSet
                if (isOnlyPossibility) {
                    stateAfterRemoval
                } else null
            }
            .firstOrNull()
    }
}
