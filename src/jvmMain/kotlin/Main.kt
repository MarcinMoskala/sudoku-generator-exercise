import generator.*
import solver.*


suspend fun main() {
    val solver = SudokuSolver()
    val generator = SudokuGenerator()
    val sudoku = generator.generate(solver, 25)
    println(sudoku.sudoku.toClearString())
    println(sudoku.solved.toClearString())
}
