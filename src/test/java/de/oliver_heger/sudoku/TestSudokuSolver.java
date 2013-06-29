package de.oliver_heger.sudoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SudokuSolver.
 *
 * @author Oliver Heger
 * @version $Id: TestSudokuSolver.java 66 2007-04-08 18:22:37Z hacker $
 */
public class TestSudokuSolver {
    /** Constant for the square size. */
    private static final short SQUARE_SIZE = 3;

    /** Constant for the field size. */
    private static final short FIELD_SIZE = 9;

    /** Stores the object to be tested. */
    private SudokuSolver solver;

    @Before
    public void setUp() throws Exception {
        solver = new SudokuSolver(SQUARE_SIZE);
    }

    @Test
    /**
     * Tests a newly initialized solver.
     */
    public void testInit() {
        for (short number = 1; number <= solver.getFieldSize(); number++) {
            for (short idx = 0; idx < solver.getFieldSize(); idx++) {
                assertFalse("Number already used in row",
                        solver.isRowUsed(number, idx));
                assertFalse("Number already used in column",
                        solver.isColumnUsed(number, idx));
                assertFalse("Number already used in square",
                        solver.isSquareUsed(number, idx));
            }
        }

        for (short row = 0; row < solver.getFieldSize(); row++) {
            for (short col = 0; col < solver.getFieldSize(); col++) {
                assertEquals("Wrong init value of cell", (short) 0,
                        solver.getCell(row, col));
            }
        }
    }

    @Test
    /**
     * Tests obtaining the field size.
     */
    public void testGetFieldSize() {
        assertEquals("Wrong field size", FIELD_SIZE, solver.getFieldSize());
    }

    @Test
    /**
     * Tests obtaining the index of a square.
     */
    public void testGetSquareIndex() {
        assertEquals((short) 0, solver.getSquareIndex(0, 0));
        assertEquals((short) 0, solver.getSquareIndex(0, SQUARE_SIZE - 1));
        assertEquals((short) 0, solver.getSquareIndex(SQUARE_SIZE - 1, 0));
        assertEquals((short) 1, solver.getSquareIndex(1, SQUARE_SIZE));
        assertEquals(SQUARE_SIZE, solver.getSquareIndex(SQUARE_SIZE, 1));
        assertEquals((short) (SQUARE_SIZE + 1),
                solver.getSquareIndex(SQUARE_SIZE, SQUARE_SIZE));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    /**
     * Tests accessing a cell with an invalid index.
     */
    public void testGetCellInvalidIndex() {
        solver.getCell(solver.getFieldSize(), 0);
    }

    @Test
    /**
     * Tests setting a valid value into a cell.
     */
    public void testSetCell() throws SudokuSolverException {
        final short val = 1;
        solver.setCell(0, 0, val);
        assertEquals("Wrong number in field", val, solver.getCell(0, 0));
        assertTrue("Row not used", solver.isRowUsed(val, 0));
        assertTrue("Column not used", solver.isColumnUsed(val, 0));
        assertTrue("Square not used", solver.isSquareUsed(val, 0));
    }

    @Test
    /**
     * Tests clearing a cell after it has been set.
     */
    public void testClearCell() throws SudokuSolverException {
        final short val = 1;
        solver.setCell(0, 0, val);
        solver.clearCell(0, 0);
        assertEquals("Wrong number in field", (short) 0, solver.getCell(0, 0));
        assertFalse("Row not used", solver.isRowUsed(val, 0));
        assertFalse("Column not used", solver.isColumnUsed(val, 0));
        assertFalse("Square not used", solver.isSquareUsed(val, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    /**
     * Tests clearing a cell that has not been written before. This should cause
     * an exception.
     */
    public void testClearCellEmpty() {
        solver.clearCell(0, 0);
    }

    @Test
    /**
     * Tests setting an invalid number into a cell.
     */
    public void testSetCellInvalidNumber() throws SudokuSolverException {
        try {
            solver.setCell(0, 0, (short) (solver.getFieldSize() + 1));
            fail("Could set invalid number into cell!");
        } catch (SudokuSolverException ssex) {
            assertEquals("Wrong number in exception",
                    (short) (solver.getFieldSize() + 1), ssex.getNumber());
        }
    }

    @Test
    /**
     * Tests setting a value into a cell when the row is already used by this
     * number.
     */
    public void testSetCellDuplicateRow() throws SudokuSolverException {
        solver.setCell(0, 0, (short) 1);
        try {
            solver.setCell(0, 1, (short) 1);
            fail("Could put value in same row!");
        } catch (SudokuSolverException ssex) {
            assertEquals("Wrong exception state", SudokuState.ROW_OCCUPIED,
                    ssex.getState());
        }
    }

    @Test
    /**
     * Tests setting a value into a cell when the column is already used by this
     * number.
     */
    public void testSetCellDuplicateColumn() throws SudokuSolverException {
        solver.setCell(0, 0, (short) 1);
        try {
            solver.setCell(1, 0, (short) 1);
            fail("Could put value in same column!");
        } catch (SudokuSolverException ssex) {
            assertEquals("Wrong exception state", SudokuState.COLUMN_OCCUPIED,
                    ssex.getState());
        }
    }

    @Test
    /**
     * Tests setting a value into a cell when the square is already used by this
     * number.
     */
    public void testSetCellDuplicateSquare() throws SudokuSolverException {
        solver.setCell(0, 0, (short) 1);
        try {
            solver.setCell(1, 1, (short) 1);
            fail("Could put value in same square!");
        } catch (SudokuSolverException ssex) {
            assertEquals("Wrong exception state", SudokuState.SQUARE_OCCUPIED,
                    ssex.getState());
        }
    }

    @Test
    /**
     * Tests solving a quite trivial sudoku.
     */
    public void testSolve() throws SudokuSolverException {
        solver.setCell(0, 0, (short) 1);
        solver.setCell(1, 1, (short) 2);
        assertTrue("Sudoku could not be solved", solver.solve());
        boolean[] check = new boolean[solver.getFieldSize()];
        for (int row = 0; row < check.length; row++) {
            Arrays.fill(check, false);
            for (int col = 0; col < check.length; col++) {
                int n = solver.getCell(row, col) - 1;
                assertTrue("Wrong number in (" + row + "," + col + "): " + n,
                        n >= 0 && n < FIELD_SIZE);
                assertFalse("Duplicate number in row", check[n]);
                check[n] = true;
            }
        }
        for (int col = 0; col < check.length; col++) {
            Arrays.fill(check, false);
            for (int row = 0; row < check.length; row++) {
                int n = solver.getCell(row, col) - 1;
                assertFalse("Duplicate number in row", check[n]);
                check[n] = true;
            }
        }
        for (int x = 0; x < FIELD_SIZE; x += SQUARE_SIZE) {
            for (int y = 0; y < FIELD_SIZE; y += SQUARE_SIZE) {
                Arrays.fill(check, false);
                for (int col = 0; col < SQUARE_SIZE; col++) {
                    for (int row = 0; row < SQUARE_SIZE; row++) {
                        int n = solver.getCell(y + row, x + col) - 1;
                        assertFalse("Duplicate number in row", check[n]);
                        check[n] = true;
                    }
                }
            }
        }
        assertEquals("Wrong first value", (short) 1, solver.getCell(0, 0));
        assertEquals("Wrong second value", (short) 2, solver.getCell(1, 1));
    }
}
