import generator.*
import kotlinx.coroutines.*
import solver.*


suspend fun main() = withContext(Dispatchers.Default) {
    val solver = SudokuSolver(
        LastMissingInRowColSquare,
        OnlyPossibleForColRowSquare,
        OnlyPossibilityForCell,
        InRowInSquare,
        HiddenPair,
        NakedPair,
        NakedN,
        HiddenN,
        YWing,
        IndifferentAlternative,
    )
    val generator = SudokuGenerator(
        RemoveLastMissingInRowColSquare,
        RemoveOnlyPossibleForColRowSquare,
        RemoveOnlyPossibilityForCell,
        InRowInSquare,
        HiddenPair,
        NakedPair,
        NakedN,
        HiddenN,
        YWing,
    )

    val sudoku = generator.generate(solver, 25)
    println(sudoku.sudoku.toClearString())
    println(sudoku.solved.toClearString())
}

