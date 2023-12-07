package generator

import SudokuState
import solver.SudokuSolver
import solver.SudokuSolverMethod

class MethodRequiredFinder(
    val solver: SudokuSolver,
    val generator: SudokuGenerator,
) {
    fun findRequiredFor(method: SudokuSolverMethod): MethodRequiredFinderResult {
        require(method in solver.methods)
        while (true) {
            print(".")
            val sudoku = generator.generate(solver).sudoku
            val solverWithoutMethod = solver.withoutMethod(method)
            val resultAfterUsingOtherMethods = solverWithoutMethod.solve(sudoku)
            if (resultAfterUsingOtherMethods.isSolved) continue
            val sudokuAfterApplyingMethod = method.apply(resultAfterUsingOtherMethods.state) ?: continue
            val result = solver.solve(sudokuAfterApplyingMethod)
            if (!result.isSolved) continue
            return MethodRequiredFinderResult(
                method,
                sudoku,
                resultAfterUsingOtherMethods.state,
                sudokuAfterApplyingMethod,
                result.state
            )
        }
    }

    fun findRequiredFor(method: SudokuSolverMethod, rowSize: Int): MethodRequiredFinderResult {
        require(method in solver.methods)
        while (true) {
            print(".")
            val sudoku = generator.generate(solver).sudoku
            val solverWithoutMethod = solver.withoutMethod(method)
            val resultAfterUsingOtherMethods = solverWithoutMethod.solve(sudoku)
            if (resultAfterUsingOtherMethods.isSolved) continue
            var sudokuApplyingMethod: SudokuState? = resultAfterUsingOtherMethods.state
            repeat(rowSize) {
                sudokuApplyingMethod = sudokuApplyingMethod?.let(method::apply)
            }
            val sudokuAfterApplyingMethod = sudokuApplyingMethod ?: continue
            val result = solver.solve(sudokuAfterApplyingMethod)
            if (!result.isSolved) continue
            return MethodRequiredFinderResult(
                method,
                sudoku,
                resultAfterUsingOtherMethods.state,
                sudokuAfterApplyingMethod,
                result.state
            )
        }
    }

    data class MethodRequiredFinderResult(
        val method: SudokuSolverMethod,
        val initialSudoku: SudokuState,
        val stateWhereMethodIsRequired: SudokuState,
        val stateAfterApplyingMethod: SudokuState,
        val solvedState: SudokuState,
    ) {
        override fun toString(): String =
            "Found state where ${method.name} is required! \nState:\n$stateWhereMethodIsRequired\n\nAfter applying method:\n$stateAfterApplyingMethod"
    }
}
