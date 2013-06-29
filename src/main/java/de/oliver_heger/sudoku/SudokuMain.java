package de.oliver_heger.sudoku;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
/**
 * The main class of the sudoku application. This is a frame that presents a
 * table for inserting the sudoku data. The user can fill in the presets and
 * then click the solve button.
 *
 * @author Oliver Heger
 */
public class SudokuMain extends JFrame
{
    /** Constant for the base name of the resource bundle. */
    private static final String RESOURCE_BUNDLE = "sudoku_resources";

    /** Constant for the default square size. */
    private static final short DEF_SQUARE_SIZE = 3;

    /** Stores the resource bundle. */
    private ResourceBundle bundle;

    /** Stores the model for the table. */
    private SudokuTableModel model;

    /** GUI element. */
    private JTable table;

    private JButton btnSolve;

    private JButton btnClear;

    /**
     * Stores the square size of the sudoku field
     */
    private int squareSize;

    /**
     * Creates a new instance of <code>SudokuMain</code> and initializes it
     * with the square size.
     *
     * @param squareSize the square size
     */
    public SudokuMain(int squareSize)
    {
        bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE);
        this.squareSize = squareSize;
        initGui(squareSize);
    }

    /**
     * Initializes the GUI.
     *
     * @param squareSize the square size
     */
    private void initGui(int squareSize)
    {
        setTitle(getResource("title"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        short fieldSize = (short) (squareSize * squareSize);
        model = new SudokuTableModel(fieldSize);
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        SudokuCellRenderer renderer = new SudokuCellRenderer(squareSize);
        table.setDefaultRenderer(Short.class, renderer);
        Short maxNumber = fieldSize;
        Dimension pref = renderer.getTableCellRendererComponent(table,
                maxNumber, false, false, 0, 0).getPreferredSize();
        int size = Math.max(pref.width, pref.height);
        for (int col = 0; col < model.getColumnCount(); col++)
        {
            table.getColumnModel().getColumn(col).setPreferredWidth(size);
        }
        table.setRowHeight(size);
        table.setTableHeader(null);
        table.setPreferredScrollableViewportSize(new Dimension(
                fieldSize * size, fieldSize * size));
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel pnlButtons = new JPanel();
        btnSolve = new JButton(getResource("BTN_SOLVE"));
        btnSolve.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                btnSolve.setEnabled(false);
                solve();
            }
        });
        pnlButtons.add(btnSolve);
        btnClear = new JButton(getResource("BTN_CLEAR"));
        btnClear.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                clearSudoku();
            }
        });
        pnlButtons.add(btnClear);
        getContentPane().add(pnlButtons, BorderLayout.SOUTH);
        pack();
    }

    /**
     * Returns the resource for the specified key.
     *
     * @param key the key
     * @return the resource for this key
     */
    private String getResource(String key)
    {
        return bundle.getString(key);
    }

    /**
     * Solves the sudoku.
     */
    private void solve()
    {
        table.clearSelection();
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    SudokuSolver solver = createSolver();
                    solver.solve();
                    showResult(solver);
                }
                catch (SudokuSolverException ssex)
                {
                    showSudokuError(ssex);
                }
            }
        }.start();
    }

    /**
     * Creates and initializes a sudoku solver object. The presets are already
     * initialized. If this causes an error (because the presets are invalid) an
     * exception is thrown.
     *
     * @return the initialized solver object
     * @throws SudokuSolverException if the presets for the sudoku are invalid
     */
    private SudokuSolver createSolver() throws SudokuSolverException
    {
        SudokuSolver solver = new SudokuSolver((short) squareSize);
        for (int row = 0; row < model.getRowCount(); row++)
        {
            for (int col = 0; col < model.getColumnCount(); col++)
            {
                Short value = (Short) model.getValueAt(row, col);
                if (value != null)
                {
                    solver.setCell(row, col, value.shortValue());
                }
            }
        }
        return solver;
    }

    /**
     * Displays the result of the sudoku in the main table. This method is
     * called from a separate thread.
     *
     * @param solver the solver object containing the result
     */
    private void showResult(final SudokuSolver solver)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                model.initResult(solver);
            }
        });
    }

    /**
     * Displays an error message if a sudoku error occurs. This method is called
     * from a different thread than the event dispatch thread.
     *
     * @param ex the exception
     */
    private void showSudokuError(SudokuSolverException ex)
    {
        MessageFormat fmt = new MessageFormat(getResource(ex.getState().name()));
        final String msg = fmt.format(new Object[]
        { ex.getNumber() });
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                JOptionPane.showMessageDialog(SudokuMain.this, msg,
                        getResource("ERR_TITLE"), JOptionPane.ERROR_MESSAGE);
                btnSolve.setEnabled(true);
            }
        });
    }

    /**
     * Clears the sudoku field, so that a new sudoku can be entered.
     */
    private void clearSudoku()
    {
        model.clear();
        btnSolve.setEnabled(true);
    }

    /**
     * The main method. As an optional argument the square size can be passed
     * in. If no parameter is passed, a default square size is used.
     *
     * @param args the arguments
     */
    public static void main(String[] args)
    {
        short squareSize = DEF_SQUARE_SIZE;
        if (args.length > 0)
        {
            try
            {
                squareSize = Short.parseShort(args[0]);
            }
            catch (NumberFormatException nfex)
            {
                System.out.println("Usage: SudokuMain <squareSize>");
                System.out.println("No valid square size was passed in!");
                System.exit(1);
            }
        }

        final SudokuMain frame = new SudokuMain(squareSize);
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                frame.setVisible(true);
            }
        });
    }
}
