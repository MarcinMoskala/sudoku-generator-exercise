package solver

import SudokuState
import firstRepetitionOfSecond
import generator.SudokuRemoverMethod

object HiddenPair : SudokuSolverMethod, SudokuRemoverMethod {
    override val name: String = "HiddenPair"

    override fun apply(state: SudokuState): SudokuState? {
        val emptyCells = state.cells
            .toList()
            .filter { it.second is SudokuState.CellState.Empty }
            .shuffled()

        val rowsColsSquares = listOf(
            emptyCells.groupBy { it.first.row }.values,
            emptyCells.groupBy { it.first.col }.values,
            emptyCells.groupBy { it.first.squareId }.values,
        ).flatten()

        rowsColsSquares
            .forEach { group ->
                val possibilities: Map<SudokuState.Position, Set<Int>> =
                    group.associate { (pos, cell) -> pos to (cell as SudokuState.CellState.Empty).possibilities }
                val (numbers, positions) = SudokuState.ALL_NUMBERS
                    .mapNotNull { number ->
                        possibilities.filterValues { number in it }.keys
                            .takeIf { it.size == 2 }
                            ?.let { number to it }
                    }
                    .firstRepetitionOfSecond<Int, Set<SudokuState.Position>>() ?: return@forEach

                if (positions.flatMap { possibilities[it].orEmpty() }.toSet() == numbers.toSet()) {
                    return@forEach
                }

                val newState = state.setPossibilities(positions, numbers.toSet())
                return newState
            }
        return null
    }
}

