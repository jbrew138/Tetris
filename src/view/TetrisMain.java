/*
 * TCSS 305 - Autumn 2014
 * Assignment 6 Tetris
 */

package view;

import java.awt.EventQueue;

/**
 * @author James Brewer
 * @version A
 */
public final class TetrisMain {
    
    /**
     * Private constructor for non-instantiation.
     */
    private TetrisMain() {
        
    }

    /**
     * Starts the program and opens the GUI.
     * 
     * @param theArgs Command line parameters
     */
    public static void main(final String[] theArgs) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                final TetrisGUI view = new TetrisGUI();
                view.start();
            }
        });
    }

}
