import generator.SudokuGenerator
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import solver.InRowInSquare
import kotlin.test.assertEquals

class SudokuStateTest {
    private val generator = SudokuGenerator()

    @Nested
    inner class Parser {
        @Test
        fun `should deserialize solved from serialized`() {
            val sudoku = generator.generateSolved()
            assertEquals(sudoku, SudokuState.from(sudoku.toString()))
        }

        @Test
        fun `should deserialize unsolved from serialized`() {
            val sudoku = generator.generate().sudoku
            val parsed = SudokuState.from(sudoku.toString())
            assertEquals(sudoku.cells, parsed.cells)
        }
    }

    @Nested
    inner class WithValue {
        @Test
        fun `should remove value and include this value as possibility`() {
            val sudokuState = SudokuState.from(
                """
                         3         |         5         |         6         |         7         |         4         |         2         |         8         |         9         |         1         
                         9         |         1         |         7         |         8         |         6         |(5)                |         4         |         2         |         3         
                         2         |         8         |         4         |         9         |         1         |         3         |         6         |         7         |         5         
                ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                         1         |         6         |         2         |         4         |         5         |         8         |         7         |         3         |         9         
                         8         |         9         |         3         |         2         |         7         |(1)                |         5         |(6)                |         4         
                         4         |         7         |         5         |         6         |         3         |         9         |         1         |         8         |         2         
                ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                         7         |         2         |         1         |         5         |         9         |         6         |         3         |         4         |(8)                
                         6         |         3         |         9         |         1         |         8         |         4         |         2         |         5         |         7         
                         5         |         4         |         8         |         3         |         2         |         7         |         9         |         1         |         6         
            """.trimIndent()
            )

            val actualState = sudokuState
                .withEmptyUpdatePoss(SudokuState.Position(0, 0))
                .withEmptyUpdatePoss(SudokuState.Position(0, 1))
                .withEmptyUpdatePoss(SudokuState.Position(0, 2))
                .withEmptyUpdatePoss(SudokuState.Position(0, 3))
                .withEmptyUpdatePoss(SudokuState.Position(0, 4))
                .withEmptyUpdatePoss(SudokuState.Position(0, 5))
                .withEmptyUpdatePoss(SudokuState.Position(0, 6))
                .withEmptyUpdatePoss(SudokuState.Position(0, 7))
                .withEmptyUpdatePoss(SudokuState.Position(0, 8))
                .withEmptyUpdatePoss(SudokuState.Position(0, 2))
                .withEmptyUpdatePoss(SudokuState.Position(0, 2))
                .withEmptyUpdatePoss(SudokuState.Position(1, 0))
                .withEmptyUpdatePoss(SudokuState.Position(1, 1))
                .withEmptyUpdatePoss(SudokuState.Position(1, 2))
                .withEmptyUpdatePoss(SudokuState.Position(2, 0))
                .withEmptyUpdatePoss(SudokuState.Position(2, 1))
                .withEmptyUpdatePoss(SudokuState.Position(2, 2))
                .withEmptyUpdatePoss(SudokuState.Position(3, 0))
                .withEmptyUpdatePoss(SudokuState.Position(3, 1))
                .withEmptyUpdatePoss(SudokuState.Position(3, 2))
                .withEmptyUpdatePoss(SudokuState.Position(4, 0))
                .withEmptyUpdatePoss(SudokuState.Position(4, 1))
                .withEmptyUpdatePoss(SudokuState.Position(4, 2))
                .withEmptyUpdatePoss(SudokuState.Position(5, 0))
                .withEmptyUpdatePoss(SudokuState.Position(5, 1))
                .withEmptyUpdatePoss(SudokuState.Position(5, 2))
                .withEmptyUpdatePoss(SudokuState.Position(5, 7))
                .withEmptyUpdatePoss(SudokuState.Position(4, 7))
                .withEmptyUpdatePoss(SudokuState.Position(3, 7))
                .withEmptyUpdatePoss(SudokuState.Position(6, 6))
                .withEmptyUpdatePoss(SudokuState.Position(6, 5))
                .withEmptyUpdatePoss(SudokuState.Position(6, 4))
                .withEmptyUpdatePoss(SudokuState.Position(7, 7))
                .withEmptyUpdatePoss(SudokuState.Position(8, 7))
                .withEmptyUpdatePoss(SudokuState.Position(7, 8))
                .withEmptyUpdatePoss(SudokuState.Position(8, 8))

            val expectedState = SudokuState.from(
                """
                (1/2/3/4/8/9)      |(1/5/6/7/8/9)      |(2/3/4/5/6/7)      |(7)                |(4)                |(2/5)              |(8)                |(1/8/9)            |(1/8)              
                (1/9)              |(1/5/7/9)          |(5/7)              |         8         |         6         |(5)                |         4         |         2         |         3         
                (2/4/8)            |(8)                |(2/4)              |         9         |         1         |         3         |         6         |         7         |         5         
                ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                (1/2/3)            |(1/6)              |(2/3/6)            |         4         |         5         |         8         |         7         |(3/6)              |         9         
                (1/3/8/9)          |(1/6/8/9)          |(3/6)              |         2         |         7         |(1)                |         5         |(3/6/8)            |         4         
                (4/8)              |(5/7/8)            |(4/5/7)            |         6         |         3         |         9         |         1         |(8)                |         2         
                ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                         7         |         2         |         1         |         5         |(9)                |(6)                |(3/8)              |         4         |(6/8)              
                         6         |         3         |         9         |         1         |         8         |         4         |         2         |(5)                |(7)                
                         5         |         4         |         8         |         3         |         2         |         7         |         9         |(1/6)              |(1/6)              
            """.trimIndent()
            )
            assertEquals(expectedState, actualState)
        }
    }

    @Nested
    inner class PossibilitiesInLine {
        @Test
        fun `should remove value and include this value as possibility`() {
            val sudokuState = SudokuState.from(
                """
                    (3)                |(3/4)              |         6         |         2         |(1)                |         5         |         7         |         9         |(3/8)              
                             9         |         7         |(1)                |         3         |         6         |         8         |         4         |         2         |         5         
                             5         |         2         |         8         |         4         |(7/9)              |(7)                |         1         |(3)                |         6         
                    ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                             4         |         9         |         7         |         8         |         5         |         6         |(3)                |(1/3)              |         2         
                             1         |         6         |(2/3)              |(7)                |(2/7)              |         4         |         5         |(3/8)              |         9         
                             8         |(5)                |(2)                |         9         |         3         |(1)                |         6         |         7         |(4)                
                    ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                             2         |         1         |         4         |         5         |(7)                |         9         |(3/8)              |(3/6/8)            |(3/7/8)            
                             6         |(3)                |         5         |         1         |         8         |         2         |         9         |         4         |(3/7)              
                    (7)                |         8         |         9         |(6/7)              |         4         |         3         |(2)                |         5         |         1         
            """.trimIndent()
            )

            val actualState = InRowInSquare.apply(sudokuState)
            val expectedState = SudokuState.from(
                """
                    (3)                |(3/4)              |         6         |         2         |(1)                |         5         |         7         |         9         |(8)                
                             9         |         7         |(1)                |         3         |         6         |         8         |         4         |         2         |         5         
                             5         |         2         |         8         |         4         |(7/9)              |(7)                |         1         |(3)                |         6         
                    ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                             4         |         9         |         7         |         8         |         5         |         6         |(3)                |(1/3)              |         2         
                             1         |         6         |(2/3)              |(7)                |(2/7)              |         4         |         5         |(3/8)              |         9         
                             8         |(5)                |(2)                |         9         |         3         |(1)                |         6         |         7         |(4)                
                    ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                             2         |         1         |         4         |         5         |(7)                |         9         |(3/8)              |(3/6/8)            |(3/7/8)            
                             6         |(3)                |         5         |         1         |         8         |         2         |         9         |         4         |(3/7)              
                    (7)                |         8         |         9         |(6/7)              |         4         |         3         |(2)                |         5         |         1         
            """.trimIndent()
            )
            assertEquals(expectedState, actualState)
        }
    }
}
