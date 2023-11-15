package generator

import SudokuState
import filterEmptyCells
import filterFilledCells
import filterNotPos

object RemoveOnlyPossibleForColRowSquare : SudokuRemoverMethod {
    override val name: String = "RemoveOnlyPossibleForColRowSquare"

    // Should remove an element, that is the only possibility in a square, row or column,
    // because this value can't be in any other cell in this square, row or column
    override fun apply(state: SudokuState): SudokuState? = state.cells
        .filterFilledCells()
        .asSequence()
        .shuffled()
        .mapNotNull { (pos, original) ->
            val stateAfterRemoval = state.withEmptyUpdatePoss(pos)
            stateAfterRemoval.takeIf {
                inNotPossibleInAnyRowColSquare(stateAfterRemoval, pos, original.value)
            }
        }
        .firstOrNull()

    private fun inNotPossibleInAnyRowColSquare(state: SudokuState, pos: SudokuState.Position, number: Int): Boolean {
        val emptyCells = state.cells
            .filterEmptyCells()
            .filterNotPos(pos)
            .asSequence()

        val isOnlyInCol = emptyCells.filter { it.key.col == pos.col }
            .none { (_, state) -> number in state.possibilities }
        val isOnlyInRow = emptyCells.filter { it.key.row == pos.row }
            .none { (_, state) -> number in state.possibilities }
        val isOnlyInSquare = emptyCells.filter { it.key.squareId == pos.squareId }
            .none { (_, state) -> number in state.possibilities }

        return isOnlyInCol || isOnlyInRow || isOnlyInSquare
    }
}
