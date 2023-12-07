@file:OptIn(ExperimentalJsExport::class)

import generator.SudokuGenerator
import solver.SudokuSolver

@JsExport
@JsName("SudokuGenerator")
class SudokuGeneratorJS {
    private val solver = SudokuSolver()
    private val generator = SudokuGenerator()

    fun generateSudoku() = generator.generate(solver, 25)
        .let {
            Sudoku(it.sudoku.toJs(), it.solved.toJs())
        }
}

@JsExport
class Sudoku(
    val sudoku: Array<Array<Int?>>,
    val solved: Array<Array<Int?>>,
)

fun SudokuState.toJs(): Array<Array<Int?>> = List(9) { row ->
    List(9) { col ->
        val cell = this.cells[SudokuState.Position(row, col)]
        when (cell) {
            is SudokuState.CellState.Filled -> cell.value
            is SudokuState.CellState.Empty, null -> null
        }
    }.toTypedArray()
}.toTypedArray()
