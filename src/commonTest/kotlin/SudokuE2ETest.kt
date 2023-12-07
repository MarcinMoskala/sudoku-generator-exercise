import generator.RemoveLastMissingInRowColSquare
import generator.RemoveOnlyPossibilityForCell
import generator.RemoveOnlyPossibleForColRowSquare
import generator.SudokuGenerator
import solver.LastMissingInRowColSquare
import solver.OnlyPossibilityForCell
import solver.OnlyPossibleForColRowSquare
import solver.SudokuSolver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SudokuE2ETest {
    private val solver = SudokuSolver(
        LastMissingInRowColSquare,
        OnlyPossibleForColRowSquare,
        OnlyPossibilityForCell,
    )
    private val generator = SudokuGenerator(
        RemoveLastMissingInRowColSquare,
        RemoveOnlyPossibilityForCell,
        RemoveOnlyPossibleForColRowSquare,
    )

    @Test
    fun generated_sudoku_solution_is_correct() {
        repeat(10) {
            val generationResult = generator.generate()
            assertTrue(generationResult.solved.isSolved())
        }
    }

    @Test
    fun generated_and_solved_are_the_same() {
        repeat(10) {
            val generationResult = generator.generate()
            val solvedResult = solver.solve(generationResult.sudoku)
            assertEquals(generationResult.solved, solvedResult.state)
        }
    }
}
