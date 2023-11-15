fun <T> Iterable<T>.allRepetitions(): Set<T> {
    val repetitions = mutableSetOf<T>()
    val seen = mutableSetOf<T>()
    for (element in this) {
        if (element in seen) {
            repetitions += element
        } else {
            seen += element
        }
    }
    return repetitions
}

fun <K, V> List<Pair<K, V>>.firstRepetitionOfSecond(): Pair<List<K>, V>? {
    val seen = mutableSetOf<V>()
    for (entry in this) {
        if (entry.second in seen) {
            val all = this.filter { it.second == entry.second }.map { it.first }
            return Pair(all, entry.second)
        }
        seen.add(entry.second)
    }
    return null
}

// The columns, rows and squares in random order
fun <T> Map<SudokuState.Position, T>.colRowSquares(): Sequence<List<Pair<SudokuState.Position, T>>> = sequence {
    val cells = toList().shuffled()
    val order = (0..2).shuffled()
    for (i in order) {
        when(i) {
            0 -> yieldAll(cells.groupBy { it.first.squareId }.values)
            1 -> yieldAll(cells.groupBy { it.first.row }.values)
            2 -> yieldAll(cells.groupBy { it.first.col }.values)
        }
    }
}

// The columns and rows in random order
fun <T> Map<SudokuState.Position, T>.colRow(): Sequence<List<Pair<SudokuState.Position, T>>> = sequence {
    val cells = toList().shuffled()
    val order = (0..1).shuffled()
    for (i in order) {
        when(i) {
            0 -> yieldAll(cells.groupBy { it.first.row }.values)
            1 -> yieldAll(cells.groupBy { it.first.col }.values)
        }
    }
}

fun <T> Map<SudokuState.Position, T>.filterNotPos(position: SudokuState.Position): Map<SudokuState.Position, T> =
    this - position

@Suppress("UNCHECKED_CAST")
fun Map<SudokuState.Position, SudokuState.CellState>.filterEmptyCells(): Map<SudokuState.Position, SudokuState.CellState.Empty> =
    this.filter { it.value is SudokuState.CellState.Empty } as Map<SudokuState.Position, SudokuState.CellState.Empty>

@Suppress("UNCHECKED_CAST")
fun Map<SudokuState.Position, SudokuState.CellState>.filterFilledCells(): Map<SudokuState.Position, SudokuState.CellState.Filled> =
    this.filter { it.value is SudokuState.CellState.Filled } as Map<SudokuState.Position, SudokuState.CellState.Filled>

fun Iterable<Pair<*, SudokuState.CellState.Empty>>.allPossibilities(): Set<Int> =
    fold(setOf()) { acc, e -> acc + e.second.possibilities }

fun <T> Collection<T>.powerset(): Set<Set<T>> = powerset(this, setOf(setOf()))

private tailrec fun <T> powerset(left: Collection<T>, acc: Set<Set<T>>): Set<Set<T>> = when {
    left.isEmpty() -> acc
    else ->powerset(left.drop(1), acc + acc.map { it + left.first() })
}

fun <K, V> Map<K, V>.find(predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V>? {
    for (entry in this) {
        if (predicate(entry)) {
            return entry
        }
    }
    return null
}
