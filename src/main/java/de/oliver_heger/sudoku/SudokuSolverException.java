package de.oliver_heger.sudoku;

/**
 * An exception class for reporting errors of the <code>SudokuSolver</code>
 * class.
 *
 * @author Oliver Heger
 */
public class SudokuSolverException extends Exception
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 9221924185578447031L;

    /** Stores the state of this exception.*/
    private SudokuState state;

    /** Stores the number that caused this exception.*/
    private short number;

    /**
     * Creates a new instance of <code>SudokuSolverException</code> and
     * initializes it.
     * @param msg an error message
     * @param t the type of this exception
     * @param n the responsible number
     */
    public SudokuSolverException(SudokuState st, short n)
    {
        super(st.toString());
        state = st;
        number = n;
    }

    /**
     * Returns the number that caused this exception.
     * @return the number
     */
    public short getNumber()
    {
        return number;
    }

    /**
     * Returns the state of this exception.
     * @return the state
     */
    public SudokuState getState()
    {
        return state;
    }
}
