import {Nullable, SudokuGenerator} from 'sudoku-generator'
import {useMemo} from "react";

const App = () => {
  const solver = useMemo(() => new SudokuGenerator(), [])
  const result = useMemo(() => solver.generateSudoku(), [])
  
  return <div>
    <Sudoku sudoku={result.sudoku} />
    <Sudoku sudoku={result.solved} />
  </div>
}

function Sudoku({sudoku}: {sudoku: Array<Array<Nullable<number>>>}) {
  
  return <table>
    {sudoku.map(row =>
        <tr>
          {row.map(col => 
              <td>{col}</td>
          )}
        </tr>
    )}
  </table>
}

export default App
