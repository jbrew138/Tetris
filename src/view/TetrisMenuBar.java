/*
 * TCSS 305 - Autumn 2014
 * Assignment
 */

package view;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * The Class TetrisMenuBar.
 *
 * @author James Brewer
 * @version B
 */
@SuppressWarnings("serial")
public class TetrisMenuBar extends JMenuBar implements Observer {
    
    /**
     * A map that stores strings for corresponding key event codes. Used to name keys based on
     * their codes, as not all key codes correspond to a character.
     */
    private static final Map<Integer, String> KEY_STRING;
    static {
        KEY_STRING = new HashMap<Integer, String>();
        KEY_STRING.put(KeyEvent.VK_LEFT,  "Left Arrow");
        KEY_STRING.put(KeyEvent.VK_RIGHT,  "Right Arrow");
        KEY_STRING.put(KeyEvent.VK_DOWN,  "Down Arrow");
        KEY_STRING.put(KeyEvent.VK_UP,  "Up Arrow");
        KEY_STRING.put(KeyEvent.VK_SPACE,  "Space");
    }
    
    /**
     * The panel displaying the board.
     */
    private final VisualBoard myPanel;
    
    /**
     * A map that stores keyboard code values accessed by the name of the control.
     */
    private final Map<String, Integer> myControlMap;
    
    /**
     * Menu item for starting new games.
     */
    private final JMenuItem myNewGame = new JMenuItem("New Game");
    
    /**
     * Menu item for ending the current game.
     */
    private final JMenuItem myEndGame = new JMenuItem("End Game");
    
    /**
     * Array of strings for the control names.
     */
    private final String[] myControlNames;
    
    /**
     * Constructs a menu bar for the Tetris game.
     *
     * @param thePanel The displayed panel.
     * @param theControlMap The map of controls
     * @param theControlNames The array of control names
     */
    public TetrisMenuBar(final VisualBoard thePanel,
                         final Map<String, Integer> theControlMap,
                         final String[] theControlNames) {
        super();
        myPanel = thePanel;
        myControlMap = theControlMap;
        myControlNames = theControlNames.clone();
        
        setupMenuBar();
    }
    
    /**
     * Menu bar setup helper method.
     */
    private void setupMenuBar() {
        

        final MenuListener pauseMenu = new MenuListener() {
            @Override
            public void menuCanceled(final MenuEvent arg0) { //Do nothing                 
            }
            @Override
            public void menuDeselected(final MenuEvent arg0) { //Do nothing 
            }
            
            /**
             * Pauses the game when the menu is selected.
             * @param theEvent Selecting the menu
             */
            @Override
            public void menuSelected(final MenuEvent theEvent) {
                myPanel.pause(true);                
            }
            
        };
        
        setupGameMenu(pauseMenu);
        setupOptionsMenu(pauseMenu);
        
    }
    
    /**
     * Sets up the first menu for ending and starting games.
     * 
     * @param theListener The listener for pausing the game
     */
    private void setupGameMenu(final MenuListener theListener) {
        final JMenu gameMenu = new JMenu("Game");
        gameMenu.addMenuListener(theListener);
        myNewGame.setEnabled(false);
        myEndGame.addActionListener(new ActionListener() {
            /** Ends the game */
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                final int choice = JOptionPane.showConfirmDialog(null, "Do you really wish to "
                                                + "end the current game?", "End game?",
                                                JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    myPanel.endGame();
                    final int newChoice = JOptionPane.showConfirmDialog(null, "Would you like "
                                                    + "to play again?",
                                                    "GAME OVER", JOptionPane.YES_NO_OPTION);
                    if (newChoice == JOptionPane.YES_OPTION) {
                        startNewGame();
                        myEndGame.setEnabled(true);
                        myNewGame.setEnabled(false);
                    }
                }
            }  
        });
        
        
        gameMenu.add(myEndGame);
        
        myNewGame.addActionListener(new ActionListener() {

            /** Starts a new game */
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                startNewGame();
                myNewGame.setEnabled(false);
                myEndGame.setEnabled(true);
            }
            
        });
        gameMenu.add(myNewGame);
        this.add(gameMenu);
    }
    
    /**
     * Helper method when selecting to start a new game.
     */
    private void startNewGame() {
        final BoardSizeDialog dialog = new BoardSizeDialog();
        dialog.showSizeDialog();
        final Dimension boardSize = dialog.getResult();
        myPanel.newGame(boardSize.width, boardSize.height);
    }
    
    
    /**
     * Sets up the options menu.
     *
     * @param theListener The listener for pausing the game
     */
    private void setupOptionsMenu(final MenuListener theListener) {
        final JMenu optionsMenu = new JMenu("Options");
        optionsMenu.addMenuListener(theListener);
        
        final JCheckBoxMenuItem gridItem = new JCheckBoxMenuItem("Grid");
        gridItem.setMnemonic(KeyEvent.VK_G);
        gridItem.addActionListener(new ActionListener() {
            /**
             * Sets the boolean in the drawing panel when the check box is changed.
             */
            public void actionPerformed(final ActionEvent theEvent) {
                myPanel.setGridDraw(gridItem.isSelected());
            }
        });
        optionsMenu.add(gridItem);
        
        final JMenuItem controlButton = new JMenuItem("Controls...");
        controlButton.addActionListener(new ActionListener() {
            /**
             * Brings up the change control dialog.
             */
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                final ControlDialog dialog = new ControlDialog();
                dialog.start();
            }
            
        });
        optionsMenu.add(controlButton);
        this.add(optionsMenu);
    }
    
    /**
     * Updates the menu bar from the visual board.
     * 
     * @param theObservable The object notifying.
     * @param theData Data being passed.
     */
    @Override
    public void update(final Observable theObservable, final Object theData) {
        if ("newGame".equals(theData)) {
            myNewGame.setEnabled(true);
            myEndGame.setEnabled(false);
        }
        
    }
    
    /**
     * Small dialog that allows for changing the controls.
     */
    protected final class ControlDialog extends JDialog {
              
        /**
         * Initializes the components.
         */
        private void start() {
            createButtons();
            this.pack();
            this.setResizable(false);
            
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();      
            this.setLocation((int) screenSize.getWidth() / 2 - this.getWidth() / 2,
                             (int) screenSize.getHeight() / 2 - this.getHeight() / 2);
            this.setVisible(true);
        }
        
        /**
         * Creates the buttons from the map of controls.
         */
        private void createButtons() {
            final Box layout = new Box(BoxLayout.Y_AXIS);
            final int vertSpace = 10;
            layout.add(Box.createVerticalStrut(vertSpace));
            final JLabel line1 = new JLabel("  Press a button to change the control.  ");
            final JLabel line2 = new JLabel("  Click the close button to cancel.  ");
            line1.setAlignmentX(CENTER_ALIGNMENT);
            line2.setAlignmentX(CENTER_ALIGNMENT);
            layout.add(line1);
            layout.add(line2);
            
            layout.add(Box.createVerticalStrut(vertSpace));
            for (final String control: myControlNames) {
                
                layout.add(newButton(control));
                layout.add(Box.createVerticalStrut(vertSpace));
            }
            
            this.add(layout);
        }
        
        /**
         * Creates a button with a specific listener to open a control change dialog.
         *
         * @param theName Button name
         * @return The button
         */
        private JButton newButton(final String theName) {
            final String nameFormatted = theName.substring(0, 1).toUpperCase()
                                            + theName.substring(1);
            final int code = myControlMap.get(theName);
            String keyName;
            if (KEY_STRING.containsKey(code)) {
                keyName = KEY_STRING.get(code);
            } else {
                keyName = Character.toString((char) code);
            }
            final JButton button = new JButton(nameFormatted + " = " + keyName);
            button.setAlignmentX(CENTER_ALIGNMENT);
            button.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent arg0) {
                    new SetKeyDialog(theName, nameFormatted);
                    ControlDialog.this.dispose();
                }
                
            });
            
            
            return button;
        }
        
    }
    
    /**
     * Creates a new smaller dialog with a key listener to change a control.
     */
    protected class SetKeyDialog extends JDialog {
        
        /**
         * The name of the control.
         */
        private final String myControlName;
        
        /**
         * Constructs a new control change dialog.
         *
         * @param theControlName The control name
         * @param theButtonName The button name
         */
        protected SetKeyDialog(final String theControlName, final String theButtonName) {
            super();
            myControlName = theControlName;
            final Box layout = new Box(BoxLayout.Y_AXIS);
            layout.add(new JLabel("Press key to change for " + theButtonName + "."));
            layout.add(new JLabel("Or press ESC or close to cancel."));
            this.add(layout);
            setupDialog();
        }
        
        /**
         * Helper method to finish setting up dialog details.
         */
        private void setupDialog() {
            this.addKeyListener(new MyKeyListener());
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.pack();
            this.setResizable(false);
            
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();      
            this.setLocation((int) screenSize.getWidth() / 2 - this.getWidth() / 2,
                             (int) screenSize.getHeight() / 2 - this.getHeight() / 2);
            
            this.setVisible(true);
        }
        
        /**
         * Listens for key press events when changing controls.
         */
        private class MyKeyListener extends KeyAdapter {
            /**
             * Handles a key being pressed.
             * 
             * @param theEvent The key pressed.
             */
            public void keyPressed(final KeyEvent theEvent) {
                final int keyPressed = theEvent.getKeyCode();
                if (keyPressed == KeyEvent.VK_ESCAPE) {
                    SetKeyDialog.this.dispose();
                } else if (!myControlMap.containsValue((Integer) theEvent.getKeyCode())) {
                    myControlMap.put(myControlName, theEvent.getKeyCode());
                    JOptionPane.showMessageDialog(null, "Key set");
                    SetKeyDialog.this.dispose();
                }
            }
        }
        
    }  
}
