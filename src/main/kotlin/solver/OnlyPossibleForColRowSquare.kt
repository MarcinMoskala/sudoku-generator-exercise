package solver

import SudokuState
import colRowSquares
import filterEmptyCells

object OnlyPossibleForColRowSquare : SudokuSolverMethod {
    override val name: String = "OnlyPossibleForColRowSquare"

    override fun apply(state: SudokuState): SudokuState? {
        val emptyColRowSquares = state.cells
            .filterEmptyCells()
            .colRowSquares()

        emptyColRowSquares.forEach { cells ->
            val singleNumberInRowColSquare: Int = cells
                .fold(listOf<Int>()) { acc, elem -> acc + elem.second.possibilities }
                .groupingBy { it }
                .eachCount()
                .firstNotNullOfOrNull { (num, counter) -> num.takeIf { counter == 1 } }
                ?: return@forEach

            val positionOfNumber: SudokuState.Position =
                cells.first { singleNumberInRowColSquare in it.second.possibilities }
                    .first

            return state.withValueAndUpdatedPoss(positionOfNumber, singleNumberInRowColSquare)
        }
        return null
    }
}
