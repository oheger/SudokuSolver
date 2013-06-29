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
