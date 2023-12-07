package solver

import SudokuState
import allPossibilities
import colRow
import colRowSquares
import filterEmptyCells
import find
import generator.SudokuRemoverMethod
import powerset
import theSameRowColumnSquare

object IndifferentAlternative : SudokuSolverMethod {
    override val name: String = "IndifferentAlternative"

    override fun apply(state: SudokuState): SudokuState? {
        val emptyCells = state.cells.filterEmptyCells()

        emptyCells
            .filter { (_, state) -> state.possibilities.size == 2 }
            .forEach { (firstCellPoss, firstCellState) ->
                val pairCellInCol = emptyCells.find { (pos, state) ->
                    pos.col == firstCellPoss.col &&
                            pos != firstCellPoss &&
                            firstCellState.possibilities == state.possibilities
                } ?: return@forEach

                val pairCellInRow = emptyCells.find { (pos, state) ->
                    pos.row == firstCellPoss.row &&
                            pos != firstCellPoss &&
                            firstCellState.possibilities == state.possibilities
                } ?: return@forEach

                val intersectionCellPos = SudokuState.Position(pairCellInCol.key.row, pairCellInRow.key.col)
                val intersectionCellState = state.cells[intersectionCellPos]
                if (intersectionCellState is SudokuState.CellState.Empty &&
                    intersectionCellState.possibilities == firstCellState.possibilities
                ) {
                    // Will not influence square possibilities
                    val positions = setOf(firstCellPoss, pairCellInCol.key, pairCellInRow.key, intersectionCellPos)
                    val squares = setOf(firstCellPoss.squareId, pairCellInCol.key.squareId, pairCellInRow.key.squareId, intersectionCellPos.squareId)
                    val notInfluancingPossibilitiesInSquare = emptyCells
                        .none { (pos, state) -> pos.squareId in squares && pos !in positions && firstCellState.possibilities.any { it in state.possibilities } }
                    if (notInfluancingPossibilitiesInSquare) {
                        val (v1, v2) = firstCellState.possibilities.shuffled()
                        val newState = state.withValueAndUpdatedPoss(firstCellPoss, v1)
                            .withValueAndUpdatedPoss(intersectionCellPos, v1)
                            .withValueAndUpdatedPoss(pairCellInCol.key, v2)
                            .withValueAndUpdatedPoss(pairCellInRow.key, v2)
                        return newState
                    }
                }
            }
        return null
    }
}

