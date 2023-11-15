data class SudokuState(
    val cells: Map<Position, CellState>,
) {
    fun isSolved(): Boolean = cells.all { it.value is CellState.Filled } &&
            allRowsColsSquares().all { it.map { (it.second as CellState.Filled).value }.toSet().size == 9 }

    fun allRowsColsSquares(): Sequence<List<Pair<Position, CellState>>> = sequenceOf(
        cells.toList().groupBy { it.first.row }.values,
        cells.toList().groupBy { it.first.col }.values,
        cells.toList().groupBy { it.first.squareId }.values,
    ).flatten()

    data class Position(val row: Int, val col: Int) {
        override fun toString(): String = "($row, $col)"
        val squareId get() = (row / 3) * 10 + (col / 3)
    }

    fun countFilled(): Int = cells.count { it.value is CellState.Filled }

    override fun toString(): String = cells
        .toList()
        .groupBy { it.first.row }
        .toList()
        .sortedBy { it.first }
        .mapIndexed { rowNumber, (_, row) ->
            row.sortedBy { it.first.col }
                .joinToString(separator = "|") { cell ->
                    when (val value = cell.second) {
                        is CellState.Empty -> value.possibilities.joinToString("/", prefix = "(", postfix = ")")
                            .padEnd(19, ' ')

                        is CellState.Filled -> "         " + value.value.toString() + "         "
                    }
                }
                .let { if (rowNumber in listOf(2, 5)) it + "\n" + "-".repeat(20 * 9) else it }

        }
        .joinToString(separator = "\n")

    fun toClearString(): String = cells
        .toList()
        .groupBy { it.first.row }
        .toList()
        .sortedBy { it.first }
        .map { (_, row) ->
            row.sortedBy { it.first.col }
                .joinToString(separator = " ") { cell ->
                    when (val value = cell.second) {
                        is CellState.Empty -> " "
                        is CellState.Filled -> value.value.toString()
                    }
                }

        }
        .joinToString(separator = "\n")

    sealed interface CellState {
        data class Empty(val possibilities: Set<Int> = ALL_NUMBERS) : CellState
        data class Filled(val value: Int) : CellState
    }

    fun withEmptyUpdatePoss(pos: Position): SudokuState {
        val previousState = cells[pos]
        if (previousState is CellState.Empty) return this
        val withValueRemoved = SudokuState(cells + (pos to CellState.Empty()))
        return SudokuState(withValueRemoved
            .cells
            .mapValues { (currPos, currState) ->
                if (currState is CellState.Empty && (theSameRowColumnSquare(pos, currPos) || currPos == pos)) {
                    currState.copy(possibilities = withValueRemoved.possibilitiesFor(currPos))
                    // TODO Adding it not correct, because there might be other reasons why this value is not possible
//                    currState.copy(possibilities = currState.possibilities + removedValue)
                } else {
                    currState
                }
            }
        )
    }

    fun withValueAndUpdatedPoss(pos: Position, value: Int) =
        copy(cells = (cells + (pos to CellState.Filled(value)))
            .mapValues { (currPos, currState) ->
                if (currState is CellState.Empty && theSameRowColumnSquare(pos, currPos)) {
                    currState.copy(possibilities = currState.possibilities - value)
                } else {
                    currState
                }
            }
        )

    fun fillWithRandomUpdatePoss(pos: Position): SudokuState =
        when (val cellState = cells.toList().find { it.first.row == pos.row && it.first.col == pos.col }?.second) {
            is CellState.Empty -> {
                val number = cellState.possibilities.random()
                withValueAndUpdatedPoss(pos, number)
            }

            null, is CellState.Filled -> this
        }

    fun isFilled() = cells.all { it.value is CellState.Filled }

    fun toOutputString(): String = cells
        .toList()
        .groupBy { it.first.row }
        .map { (_, row) ->
            row.joinToString(separator = "") { cell ->
                when (val value = cell.second) {
                    is CellState.Empty -> "0"
                    is CellState.Filled -> value.value.toString()
                }
            }
        }
        .joinToString(separator = ",")

    fun withUpdatedPoss(): SudokuState = copy(
        cells = cells.mapValues { (pos, cellState) ->
            if (cellState is CellState.Empty) {
                CellState.Empty(possibilitiesFor(pos))
            } else {
                cellState
            }
        }
    )

    private fun possibilitiesFor(pos: Position) = ALL_NUMBERS - cells.asSequence()
        .filter { (innerPos, _) -> theSameRowColumnSquare(pos, innerPos) }
        .mapNotNull { (_, value) -> (value as? CellState.Filled)?.value }
        .toSet()

    fun removePossibilities(positions: Set<Position>, vararg number: Int): SudokuState = SudokuState(
        cells.mapValues { (pos, state) ->
            if (pos in positions && state is CellState.Empty) {
                state.copy(possibilities = state.possibilities - number.toSet())
            } else {
                state
            }
        }
    )

    fun setPossibilities(positions: Set<Position>, possibilities: Set<Int>): SudokuState = SudokuState(
        cells + positions.associateWith { CellState.Empty(possibilities) }
    )

    companion object {
        val ALL_NUMBERS = (1..9).toSet()

        fun empty() = SudokuState(
            (0..8).flatMap { row ->
                (0..8).map { col ->
                    Position(row, col) to CellState.Empty()
                }
            }.toMap()
        )

        fun from(string: String) = SudokuState(string
            .split("\n")
            .filter { !it.startsWith("------") }
            .flatMapIndexed { rowNum, row ->
                row.split("|")
                    .mapIndexed { colNum, cell ->
                        val cellTrimmed = cell.trim()
                        Position(rowNum, colNum) to (
                                when {
                                    cellTrimmed.startsWith("(") -> {
                                        val possibilities = cell.substringAfter("(")
                                            .substringBefore(")")
                                            .split("/")
                                            .mapNotNull { it.toIntOrNull() }
                                            .toSet()
                                        CellState.Empty(possibilities)
                                    }

                                    else -> {
                                        val value =
                                            cellTrimmed.toIntOrNull() ?: error("\"$cellTrimmed\" is not a number")
                                        CellState.Filled(value)
                                    }
                                }
                                )
                    }
            }
            .toMap()
        )

        fun fromRaw(string: String) = SudokuState(string
            .split("\n")
            .flatMapIndexed { rowNum, row ->
                row.toList()
                    .filterIndexed { index, c -> index % 2 == 0 }
                    .mapIndexed { colNum, c ->
                        Position(rowNum, colNum) to if (c.isWhitespace()) CellState.Empty(setOf()) else CellState.Filled(c.digitToInt())
                    }
            }
            .toMap()
        )

    }

}

fun theSameRowColumnSquare(pos1: SudokuState.Position, pos2: SudokuState.Position) =
    pos1 != pos2 && (pos1.col == pos2.col || pos1.row == pos2.row || pos1.squareId == pos2.squareId)
