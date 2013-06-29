package de.oliver_heger.sudoku;

/**
 * <p>
 * An enumeration with the possible states of a sudoku operation.
 * </p>
 * <p>
 * Whenever a number is to be placed into a cell a couple of different errors
 * can occur. This enumeration is used to specify the outcome of such an
 * operation. If the operation is allowed, the {@code OK} state is used.
 * Otherwise the state tells why the operation is not allowed.
 *
 * @author Oliver Heger
 */
public enum SudokuState {
    OK, INVALID_NUMBER, ROW_OCCUPIED, COLUMN_OCCUPIED, SQUARE_OCCUPIED
}
