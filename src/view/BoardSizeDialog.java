/*
 * TCSS 305 - Autumn 2014
 * Assignment 6 Tetris
 */

package view;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * A dialog for choosing the size of the board.
 * 
 * @author James Brewer
 * @version B
 *
 */
@SuppressWarnings("serial")
public class BoardSizeDialog extends JDialog {
    
    /**
     * An array of possible values to pick from.
     */
    private static final String[] VALUE_LIST = {"10", "11", "12", "13", "14", "15", "16",
        "17", "18", "19", "20", "21", "22", "23", "24", "25"};
    
    /**
     * Vertical strut space.
     */
    private static final int STRUT = 20;
    
    /**
     * The resulting board size of the choice.
     */
    private Dimension myResult;
    
    /**
     * Shows the size dialog and returns a dimension.
     */
    public void showSizeDialog() {
        
        this.setModal(true);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        final Box layout = new Box(BoxLayout.Y_AXIS);
        layout.add(Box.createVerticalStrut(STRUT));
        final JLabel msgLabel = new JLabel("    Please choose the size of the board.    ");
        msgLabel.setAlignmentX(CENTER_ALIGNMENT);
        layout.add(msgLabel);
        layout.add(Box.createVerticalStrut(STRUT));
        
        final Box xLayout = new Box(BoxLayout.X_AXIS);
        final JComboBox<String> xList = new JComboBox<String>(VALUE_LIST);
        xLayout.add(Box.createHorizontalStrut(STRUT));
        xLayout.add(xList);
        xLayout.add(new JLabel("  by  "));
        final JComboBox<String> yList = new JComboBox<String>(VALUE_LIST);
        final int defaultY = 10;
        yList.setSelectedItem(VALUE_LIST[defaultY]);
        xLayout.add(yList);
        xLayout.add(Box.createHorizontalStrut(STRUT));
        layout.add(xLayout);
        layout.add(Box.createVerticalStrut(STRUT));

        final JButton okayBtn = new JButton("Okay");
        okayBtn.setAlignmentX(CENTER_ALIGNMENT);
        layout.add(okayBtn);
        okayBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                myResult = new Dimension(
                                       Integer.parseInt((String) xList.getSelectedItem()),
                                       Integer.parseInt((String) yList.getSelectedItem()));
                dispose();
            }
        });
        
        this.add(layout);
        this.pack();
        
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();      
        this.setLocation((int) screenSize.getWidth() / 2 - this.getWidth() / 2,
                         (int) screenSize.getHeight() / 2 - this.getHeight() / 2);
        this.setVisible(true);
    }
    
    /**
     * Gets resulting dimension.
     *
     * @return result
     */
    public Dimension getResult() {
        return (Dimension) myResult.clone();
    }
    
}
