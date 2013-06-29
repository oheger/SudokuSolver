/**
 * Copyright 2009-2013 The JGUIraffe Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.oliver_heger.sudoku;

/**
 * A class for solving sudokus.
 *
 * @author Oliver Heger
 */
public class SudokuSolver
{
    /** An array for the field to be filled. */
    private short[][] field;

    /**
     * An array for storing information about the so far filled rows. The first
     * index represents a number, the second index a row index. A value of true
     * means that this number has already been placed in the row with the given
     * index.
     */
    private boolean[][] filledRows;

    /**
     * An array for storing information about the so far filled columns. The
     * first index represents a number, the second index a column index. A value
     * of true means that this number has already been placed in the column with
     * the given index.
     */
    private boolean[][] filledColumns;

    /**
     * An array for storing information about the so far filled (sub) squares.
     * The first index represents a number, the second index a square. A value
     * of true means that this number has already been placed into this square.
     */
    private boolean[][] filledSquares;

    /** Stores the size of the sub squares. */
    private short squareSize;

    /**
     * Creates a new instance of <code>SudokuSolver</code> and initializes it
     * with the square size. The square size determines the total size of the
     * field in the following way: If it is set to <em>n</em>, a sub square
     * consists of n * n sub fields. The total field contains n * n sub squares.
     * So the numbers from 1 to (n * n) must be filled once in each row, column,
     * and sub square. A square size of 3 will result in a sudoku of the default
     * size that must be filled with the numbers from 1 to 9.
     *
     * @param sqrSz the square size
     * @throws IllegalArgumentException if the size is invalid
     */
    public SudokuSolver(short sqrSz)
    {
        if (sqrSz < 2)
        {
            throw new IllegalArgumentException(
                    "Square size must be greater or equal 2!");
        }
        squareSize = sqrSz;
        initField();
    }

    /**
     * Returns the square size. The size of the total field is the square of the
     * size returned here.
     *
     * @return the size of a (sub) square
     */
    public short getSquareSize()
    {
        return squareSize;
    }

    /**
     * Returns the size of the total field. The value returned by this method is
     * the number of rows and columns of the total field. It also corresponds to
     * the maximum number to be filled in the field.
     *
     * @return the (total) field size
     */
    public short getFieldSize()
    {
        return (short) (getSquareSize() * getSquareSize());
    }

    /**
     * Returns the value of the cell at the specified position.
     *
     * @param row the row index
     * @param col the column index
     * @return the value of this cell (0 means that no number has been filled
     * into this cell yet)
     */
    public short getCell(int row, int col)
    {
        return field[row][col];
    }

    /**
     * Writes a value into the specified cell. This method is used for filling
     * the presets. It immediately checks whether the sudoku rules are not
     * violated by this operation and throws an exception if necessary.
     *
     * @param row the row index
     * @param col the column index
     * @param value the value for the cell
     * @throws SudokuSolverException if this set operation is not allowed
     */
    public void setCell(int row, int col, short value)
            throws SudokuSolverException
    {
        if (value < 1 || value > getFieldSize())
        {
            throw new SudokuSolverException(SudokuState.INVALID_NUMBER, value);
        }
        SudokuState state = checkAllowed(value, row, col);
        if (state != SudokuState.OK)
        {
            throw new SudokuSolverException(state, value);
        }
        writeCell(row, col, value);
    }

    /**
     * Removes the value from the specified cell. This method can be used to
     * clear a preset that was written using <code>setCell()</code>. The
     * specified cell must contain a valid value, otherwise an exception is
     * thrown.
     *
     * @param row the row index
     * @param col the column index
     * @throws IllegalArgumentException if the cell is empty
     */
    public void clearCell(int row, int col)
    {
        if (getCell(row, col) == 0)
        {
            throw new IllegalArgumentException("The cell has not been filled!");
        }
        writeCell(row, col, (short) 0);
    }

    /**
     * Tries to solve the sudoku. Fills all the missing fields with valid
     * values. Typically an instance of <code>SudokuSolver</code> is created,
     * then <code>setCell()</code> is used for filling in the presets, and
     * finally this method can be invoked to generate a solution.
     *
     * @return a flag whether a solution could be found
     */
    public boolean solve()
    {
        return solveColumn((short) 1, (short) 0);
    }

    /**
     * Returns the index of the square that belongs to the given coordinates.
     *
     * @param row the row index
     * @param col the column index
     * @return the index of the corresponding square
     */
    short getSquareIndex(int row, int col)
    {
        return (short) ((row / getSquareSize()) * getSquareSize() + col
                / getSquareSize());
    }

    /**
     * Returns a flag whether the specified row is already used for the given
     * number. Then this number must not be placed again in this row.
     *
     * @param number the number
     * @param row the row index
     * @return a flag whether the number is already present in this row
     */
    boolean isRowUsed(short number, int row)
    {
        return filledRows[number - 1][row];
    }

    /**
     * Returns a flag whether the specified column is already used for the given
     * number. Then this number must not be placed in this column again.
     *
     * @param number the number
     * @param col the column index
     * @return a flag whether the number is already present in this column
     */
    boolean isColumnUsed(short number, int col)
    {
        return filledColumns[number - 1][col];
    }

    /**
     * Returns a flag whether the specified number has already been placed into
     * the given square. In this case the number must not be put again into this
     * square.
     *
     * @param number the number to check
     * @param index the index of the affected square
     * @return a flag whether the number is already present in this square
     */
    boolean isSquareUsed(short number, int index)
    {
        return filledSquares[number - 1][index];
    }

    /**
     * Checks whether it is allowed to place the given number into the specified
     * cell.
     *
     * @param number the number to be placed
     * @param row the row index
     * @param col the column index
     * @return a state flag; <code>OK</code> if the operation is allowed,
     * otherwise the cause why it is not allowed
     */
    SudokuState checkAllowed(short number, int row, int col)
    {
        if (isRowUsed(number, row))
        {
            return SudokuState.ROW_OCCUPIED;
        }
        else if (isColumnUsed(number, col))
        {
            return SudokuState.COLUMN_OCCUPIED;
        }
        else if (isSquareUsed(number, getSquareIndex(row, col)))
        {
            return SudokuState.SQUARE_OCCUPIED;
        }
        else
        {
            return SudokuState.OK;
        }
    }

    /**
     * Initializes the internal data structures for storing the information
     * about the sudoku field.
     */
    private void initField()
    {
        field = new short[getFieldSize()][getFieldSize()];
        filledRows = new boolean[getFieldSize()][getFieldSize()];
        filledColumns = new boolean[getFieldSize()][getFieldSize()];
        filledSquares = new boolean[getFieldSize()][getFieldSize()];
    }

    /**
     * Writes a value into a cell. This method does not perform any checks; it
     * directly writes the value and updates the used flags.
     *
     * @param row the row
     * @param col the column
     * @param value the value to write
     */
    private void writeCell(int row, int col, short value)
    {
        if (value == 0)
        {
            markUsed(getCell(row, col), row, col, false);
        }
        else
        {
            markUsed(value, row, col, true);
        }
        field[row][col] = value;
    }

    /**
     * Sets the used flag for a number and a position.
     *
     * @param number the number
     * @param row the row
     * @param col the column
     * @param used the used flag
     */
    private void markUsed(short number, int row, int col, boolean used)
    {
        short nIdx = (short) (number - 1);
        filledRows[nIdx][row] = used;
        filledColumns[nIdx][col] = used;
        filledSquares[nIdx][getSquareIndex(row, col)] = used;
    }

    /**
     * Tries to fill a column with the given number. This recursive method
     * performs the main action when solving a sudoku. It creates all possible
     * combinations of numbers in the single columns.
     *
     * @param number the number to be placed in a column
     * @param row the index of the current row
     * @param col the index of the current column
     * @return a flag whether the number could be placed
     */
    private boolean solveColumn(short number, short col)
    {
        if (number > getFieldSize())
        {
            // successful end of recursion?
            return true;
        }

        if (col >= getFieldSize())
        {
            // All columns for this number have been processed => next number
            return solveColumn((short) (number + 1), (short) 0);
        }

        if (isColumnUsed(number, col))
        {
            // This number is fixed placed in this column.
            return solveColumn(number, (short) (col + 1));
        }

        // Try all combinations for this number in this column
        for (short row = 0; row < getFieldSize(); row++)
        {
            if (getCell(row, col) == 0
                    && checkAllowed(number, row, col) == SudokuState.OK)
            {
                writeCell(row, col, number);
                if (solveColumn(number, (short) (col + 1)))
                {
                    return true;
                }
                clearCell(row, col);
            }
        }
        return false;
    }
}
