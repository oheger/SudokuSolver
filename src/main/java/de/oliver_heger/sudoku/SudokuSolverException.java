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
