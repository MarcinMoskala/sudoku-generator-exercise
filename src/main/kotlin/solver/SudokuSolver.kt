package solver

import SudokuState

class SudokuSolver(
    val methods: List<SudokuSolverMethod> = listOf(
        LastMissingInRowColSquare,
        OnlyPossibilityForCell,
        OnlyPossibleForColRowSquare,
    )
) {
    constructor(vararg methods: SudokuSolverMethod) : this(methods.toList())

    fun solve(sudokuState: SudokuState): Result {
        var state = sudokuState
        val methodsUsedCounter = mutableMapOf<String, Int>()
        while (true) {
            val (newState, method) = makeStep(state) ?: return Result(state, state.isFilled(), methodsUsedCounter)
            state = newState
            methodsUsedCounter[method.name] = (methodsUsedCounter[method.name] ?: 0) + 1
            if (state.isFilled()) {
                return Result(state, true, methodsUsedCounter)
            }
        }
    }

    fun makeStep(state: SudokuState): Pair<SudokuState, SudokuSolverMethod>? = methods.firstNotNullOfOrNull { method -> // TODO: Remove
        method.apply(state)?.let { it to method }
    }

    fun withoutMethod(method: SudokuSolverMethod) = SudokuSolver(methods - method)

    class Result(
        val state: SudokuState,
        val isSolved: Boolean,
        val methodsUsedCounter: Map<String, Int>
    )
}
