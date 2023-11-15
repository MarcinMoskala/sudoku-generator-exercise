package generator

import SudokuState

interface SudokuRemoverMethod {
    val name: String
    fun apply(state: SudokuState): SudokuState?
}
