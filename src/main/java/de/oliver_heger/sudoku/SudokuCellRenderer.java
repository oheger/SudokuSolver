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

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * A custom renderer for the cells of the sudoku table. This renderer will
 * display preset cells in a different color. The squares of the sudoku will
 * also be marked with different background colors.
 *
 * @author Oliver Heger
 */
@SuppressWarnings("serial")
class SudokuCellRenderer extends JLabel implements TableCellRenderer
{
    /** Constant for the square 1 color. */
    private static final Color SQUARE1_COL = new Color(222, 222, 222);

    /** Constant for the square 2 color. */
    private static final Color SQUARE2_COL = Color.WHITE;

    /** Constant for the default foreground color. */
    private static final Color FG_NORMAL = Color.BLACK;

    /** Constant for the preset foreground color. */
    private static final Color FG_PRESET = Color.BLUE;

    /** Constant for an empty string. */
    private static final String EMPTY = "";

    /** Constant for the font size. */
    private static final float FONT_SIZE = 20;

    /** Stores the square size. */
    private int squareSize;

    /**
     * Creates a new instance of <code>SudokuCellRenderer</code> and sets the
     * square size.
     *
     * @param size the size of a square in the sudoku
     */
    public SudokuCellRenderer(int size)
    {
        squareSize = size;
        setFont(getFont().deriveFont(FONT_SIZE));
    }

    /**
     * Returns the renderer component.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int col)
    {
        setOpaque(true);
        setText((value != null) ? value.toString() : EMPTY);
        setHorizontalAlignment(CENTER);
        setBackground(getSquareColor(row, col));
        SudokuTableModel model = (SudokuTableModel) table.getModel();
        setForeground(model.isPreset(row, col) ? FG_PRESET : FG_NORMAL);
        if (hasFocus)
        {
            setBorder(BorderFactory.createEtchedBorder());
        }
        else
        {
            setBorder(null);
        }
        return this;
    }

    /**
     * Determines the square color for the given cell. This color will be set as
     * background color.
     *
     * @param row the row index
     * @param col the column index
     * @return the square color
     */
    private Color getSquareColor(int row, int col)
    {
        return (((row / squareSize) % 2 == 0) ^ ((col / squareSize) % 2 == 0)) ? SQUARE1_COL
                : SQUARE2_COL;
    }
}
