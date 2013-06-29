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

import javax.swing.table.AbstractTableModel;

/**
 * A table model class for storing the content of a sudoku.
 *
 * @author Oliver Heger
 */
@SuppressWarnings("serial")
class SudokuTableModel extends AbstractTableModel
{
    /** Stores the content of the sudoku. */
    private Short[][] data;

    /** Stores information, which of the table's field have been preset. */
    private boolean[][] preset;

    /** Stores the field size of the sudoku. */
    private int fieldSize;

    /**
     * Creates a new instance of {@code SudokuTableModel}.
     *
     * @param size the size of the sudoku field
     */
    public SudokuTableModel(int size)
    {
        fieldSize = size;
        data = new Short[size][size];
    }

    /**
     * Returns the number of columns.
     *
     * @return the number of columns
     */
    @Override
    public int getColumnCount()
    {
        return fieldSize;
    }

    /**
     * Returns the number of rows.
     *
     * @return the number of rows
     */
    @Override
    public int getRowCount()
    {
        return fieldSize;
    }

    /**
     * Returns the value of the specified cell.
     *
     * @param row the row index
     * @param col the column index
     * @return the value of this cell
     */
    @Override
    public Object getValueAt(int row, int col)
    {
        return data[row][col];
    }

    /**
     * Returns the class of the specified column. This model operates always on
     * short numbers.
     *
     * @param the column index
     * @return the column class
     */
    @Override
    public Class<?> getColumnClass(int col)
    {
        return Short.class;
    }

    /**
     * Returns a flag whether the specified cell is editable. Here all cells are
     * editable.
     *
     * @param row the row index
     * @param col the column index
     * @return a flag whether this cell is editable
     */
    @Override
    public boolean isCellEditable(int row, int col)
    {
        return true;
    }

    /**
     * Sets the value of the specified cell.
     *
     * @param value the new value
     * @param row the row index
     * @param col the column index
     */
    @Override
    public void setValueAt(Object value, int row, int col)
    {
        data[row][col] = (Short) value;
    }

    /**
     * Initializes this table model with the result of the sudoku. The sudoku
     * field is copied into the own data array. Then an update event is fired.
     *
     * @param solver the solver object
     */
    public void initResult(SudokuSolver solver)
    {
        assert solver.getFieldSize() == getColumnCount() : "Sudoku field does not match table size";
        initPresets();
        for (int row = 0; row < getRowCount(); row++)
        {
            for (int col = 0; col < getColumnCount(); col++)
            {
                setValueAt(solver.getCell(row, col), row, col);
            }
        }
        fireTableDataChanged();
    }

    /**
     * Clears the internal data. All internal fields are reset. This method can
     * be used to let the user enter a new sudoku.
     */
    public void clear()
    {
        data = new Short[getRowCount()][getColumnCount()];
        preset = null;
        fireTableDataChanged();
    }

    /**
     * Checks whether the specified cell contains a preset value.
     *
     * @param row the row index
     * @param col the column index
     * @return a flag whether this cell contains a preset value
     */
    public boolean isPreset(int row, int col)
    {
        return (preset == null) ? false : preset[row][col];
    }

    /**
     * Initializes the data field for the cells that have already been set as
     * presets.
     */
    private void initPresets()
    {
        preset = new boolean[getRowCount()][getColumnCount()];
        for (int row = 0; row < getRowCount(); row++)
        {
            for (int col = 0; col < getColumnCount(); col++)
            {
                preset[row][col] = getValueAt(row, col) != null;
            }
        }
    }
}
