import generator.*
import kotlinx.coroutines.*
import solver.*


suspend fun main() = withContext(Dispatchers.Default) {
    val solver = SudokuSolver(
//        LastMissingInRowColSquare,
//        OnlyPossibleForColRowSquare,
        OnlyPossibilityForCell,
//        InRowInSquare,
//        HiddenPair,
//        NakedPair,
//        NakedN,
//        HiddenN,
//        YWing,
//        IndifferentAlternative,
    )
    val generator = SudokuGenerator(
        RemoveLastMissingInRowColSquare,
//        RemoveOnlyPossibleForColRowSquare,
        RemoveOnlyPossibilityForCell,
//        InRowInSquare,
//        HiddenPair,
//        NakedPair,
//        NakedN,
//        HiddenN,
//        YWing,
    )

//    val finder = MethodRequiredFinder(solver, generator)
//    repeat(1) {
//        println(finder.findRequiredFor(OnlyPossibilityForCell, 5))
//    }

    List(1) {
        async {
            val sudoku = generator.generate(solver, 35)
            val solvedResult = solver.solve(sudoku)
            print(".")
            sudoku to solvedResult
        }
    }.awaitAll()
        .sortedByDescending {
            -it.first.countFilled()
//            (it.second.methodsUsedCounter[IndifferentAlternative.name] ?: 0)
        }
        .take(5)
        .forEach { (sudoku, solvedResult) ->
            println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------")
            println("-- Next one ------------------------------------------------------------------------------------------------------------------------------------------------------------------------")
            println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------")
            println(sudoku.toClearString())
            println(sudoku)
            println(sudoku.toOutputString())
            println("Filled: " + sudoku.countFilled())
            println(solvedResult.methodsUsedCounter)
            val result = solver.solve(sudoku)
            println("Confirmed, that can be solved: ${result.isSolved}")
            println(solvedResult.state)
            println(solvedResult.state.toOutputString())
        }
}

