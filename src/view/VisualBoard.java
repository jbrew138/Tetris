/*
 * TCSS 305 - Autumn 2014
 * Assignment 6 Tetris
 */

package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import model.AbstractPiece;
import model.Block;
import model.Board;

/**
 * This class has the ability to graphically display a board, as well as control the board
 * through keyboard input and timers. Also provides scoring data.
 *
 * @author James Brewer
 * @version B
 */
@SuppressWarnings("serial")
public class VisualBoard extends Observable implements Observer {
     
    /**
     * Default timing delay for activating the step method. 1000 ms = 1 s.
     */
    protected static final int DEFAULT_TIMING = 1000;
    
    /**
     * Step in milliseconds in lowering the timer speed in difficulty change.
     */
    protected static final int TIMING_STEP = 50;
    
    /**
     * The default side length of a block.
     */
    private static final int BLOCK_SIDE = 30;

    /**
     * The thickness of the grid lines.
     */
    private static final int GRID_LINE_WIDTH = 1;
    
    /**
     * Number of lines needed to be cleared to increase difficulty one step.
     */
    private static final int LEVEL_UP_AT = 5;
    
    /**
     * Default font for the pause text.
     */
    private static final Font DEFAULT_FONT = new Font("Arial", Font.BOLD, 44);
    
    /**
     * The current block size ratio, used as a double in order to handle resizing a slightly
     * more smooth. Initially set to the default value.
     */
    private double myBlockSide = BLOCK_SIDE;
    
    /**
     * The main panel for the class.
     */
    private final GraphicsPanel myPanel = new GraphicsPanel();
    
    /**
     * The board being displayed by the panel.
     */
    private final Board myBoard;
    
    /**
     * Number of blocks wide.
     */
    private int myBlocksWide;
    
    /**
     * Number of blocks high.
     */
    private int myBlocksTall;
    
    /**
     * The coordinates of the current piece.
     */
    private int[][] myCurrentCoords;
    
    /**
     * The list of blocks that have been frozen in place.
     */
    private final List<Block[]> myFrozenData;
    
    /**
     * The game timer.
     */
    private final Timer myTimer = new Timer(DEFAULT_TIMING, new TimerListener());
    
    /**
     * Sets whether the game is paused.
     */
    private boolean myPause;
    
    /**
     * A map that stores keyboard code values accessed by the name of the control.
     */
    private final Map<String, Integer> myControlMap;

    /**
     * Boolean that flags whether the grid is drawn or not.
     */
    private boolean myGridDraw;

    /**
     * A flag for the game being over or not.
     */
    private boolean myGameOver;
    
    /**
     * Number of lines cleared during play.
     */
    private int myLinesCleared;
    
    /**
     * Number of lines cleared at one time.
     */
    private int myLineClearCombo;
    
    /**
     * Constructs a new game panel, given a board.
     * 
     * @param theBoard The board the panel will display.
     * @param theControlMap 
     */
    public VisualBoard(final Board theBoard, final Map<String, Integer> theControlMap) {
        super();
        
        myBoard = theBoard;
        myBlocksWide = myBoard.getWidth();
        myBlocksTall = myBoard.getHeight();
        myControlMap = theControlMap;
        myBoard.addObserver(this);
        myCurrentCoords = getCurrentCoords(myBoard);
        myFrozenData = new LinkedList<Block[]>();
        
        setupPanel();
    }
    
    /**
     * Sets up the initial size of the panel and other details.
     */
    private void setupPanel() {
        myTimer.start();
        final Dimension startingDimension = new Dimension((int) myBlockSide
                                                          * myBlocksWide + 1,
                                                          (int) myBlockSide
                                                          * myBlocksTall);
        
        myPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
        myPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        myPanel.setPreferredSize(startingDimension);
        myPanel.setMaximumSize(startingDimension);
        
        myPanel.addKeyListener(new MyKeyListener());
    }
    
    /**
     * Returns the panel to access it as a component.
     * 
     * @return The panel.
     */
    public JPanel getPanel() {
        return myPanel;
    }
    
    /**
     * Changes the key for one of the controls.
     * 
     * @param theControl The name of the control being changed.
     * @param theKey The integer value of the key (normally passed as a KeyEvent static field).
     */
    public void setControl(final String theControl, final int theKey) {
        myControlMap.put(theControl, theKey);
    }

    
    /**
     * Draws a string in the center of the panel, with a given color.
     * 
     * @param theG2d The graphics controller
     * @param theMessage The string to be displayed
     * @param theColor The color of the words printed
     */
    private void paintWord(final Graphics2D theG2d, final String theMessage,
                           final Color theColor) {
        theG2d.setColor(theColor);
        theG2d.setFont(DEFAULT_FONT);
        final Rectangle2D stringBounds = 
                                       theG2d.getFontMetrics().getStringBounds(theMessage,
                                                                               theG2d);
        theG2d.drawString(theMessage, 
                          (int) (myPanel.getWidth() / 2 - stringBounds.getWidth() / 2),
                       (int) (myPanel.getHeight() / 2 - stringBounds.getHeight() / 2));
    }
    
    /**
     * Draws the blocks from the coordinate data.
     * 
     * @param theG2d The graphics controller
     */
    private void drawBlocks(final Graphics2D theG2d) {
        
        final int boardHeightIndex = myBlocksTall - 1;
        Color frozenColor;
        final Color currentColor =
                             ((AbstractPiece) myBoard.getCurrentPiece()).getBlock().getColor();
        final BasicStroke borderWidth = new BasicStroke(2);
        final int blkSide = (int) myBlockSide;
        
        theG2d.setStroke(borderWidth);
        
        //Draw the current piece
        for (int i = 0; i < myCurrentCoords.length; i++) {
            theG2d.setColor(currentColor);
            theG2d.fillRect(myCurrentCoords[i][0] * blkSide,
                            myCurrentCoords[i][1] * blkSide, blkSide, blkSide);
            theG2d.setColor(currentColor.darker());
            theG2d.drawRect(myCurrentCoords[i][0] * blkSide + 1,
                            myCurrentCoords[i][1] * blkSide + 1, blkSide - 1 , blkSide - 1);
        }
        
        //Draw the frozen blocks
        for (int j = 0; j < myFrozenData.size(); j++) {
            for (int k = 0; k < myBoard.getWidth(); k++) {
                frozenColor = myFrozenData.get(j)[k].getColor();
                if (!frozenColor.equals(Color.BLACK)) {
                    theG2d.setColor(frozenColor);
                    theG2d.fillRect(k * blkSide, (boardHeightIndex - j) * blkSide
                                    , blkSide, blkSide);
                    theG2d.setColor(frozenColor.darker());
                    theG2d.drawRect(k * blkSide + 1, (boardHeightIndex - j) * blkSide + 1,
                                    blkSide - 1 , blkSide - 1);
                }
            }            
        }
    }
    
    /**
     * Sets whether to draw the grid.
     *
     * @param theGrid A boolean set to determine the grid.
     */
    public void setGridDraw(final boolean theGrid) {
        myGridDraw = theGrid;
        myPanel.repaint(); //Repaints to display the grid
    }
    
    /**
     * Draws a grid based on the panel's size.
     *
     * @param theG2d The graphics component.
     */
    private void drawGrid(final Graphics2D theG2d) {
        final Dimension panelSize = myPanel.getSize();
        final int height = (int) panelSize.getHeight();
        final int width = (int) panelSize.getWidth();
        final int roundedBlock = (int) myBlockSide;
        theG2d.setStroke(new BasicStroke(GRID_LINE_WIDTH));
        theG2d.setColor(Color.GRAY);
        for (int y = 0; y <= height; y = y + roundedBlock) {
            theG2d.draw(new Line2D.Double(0, y, width, y));
        }
        for (int x = 0; x <= width; x = x + roundedBlock) {
            theG2d.draw(new Line2D.Double(x, 0, x, height));
        }
    }
    
    /**
     * Gets the current coordinates of the active piece blocks and stores them.
     * 
     * @param theBoard The active board
     * @return The coordinates.
     */
    private int[][] getCurrentCoords(final Board theBoard) {
        final int[][] pieceCoord = 
                            ((AbstractPiece) theBoard.getCurrentPiece()).getBoardCoordinates();
        final int[][] outputCoord = pieceCoord.clone();
        final int boardHeightIndex = theBoard.getHeight() - 1;
        int i = 0;
        
        for (final int[] line: pieceCoord) {
            outputCoord[i][1] = boardHeightIndex - line[1];
            i++;
        }
        return outputCoord.clone();
    }
    
    /**
     * Pauses or unpauses the game, and starts/stops the timer.
     *
     * @param theSetPause Tells whether to pause or not.
     */
    public void pause(final boolean theSetPause) {
        if (theSetPause) {
            myPause = true;
            myTimer.stop();
        } else if (!theSetPause) {
            myPause = false;
            myTimer.start();
        }
        myPanel.repaint(); //Calls repaint to display the pause text, or remove it
    }
    
    /**
     * Update the panel when the board notifies.
     * 
     * @param arg0 The object notifying.
     * @param arg1 Optional data being passed.
     */
    @Override
    public void update(final Observable arg0, final Object arg1) {
        final int linesCleared = myFrozenData.size() - myBoard.getFrozenBlocks().size();
        
        if (myBoard.isGameOver()) {
            myTimer.stop();
            myGameOver = true;
            final int choice = JOptionPane.showConfirmDialog(null, "The game has ended, would "
                                            + "you like to play again?", "GAME OVER",
                                            JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                newGame();
                pause(false);
            }
        } else if (linesCleared > 0) {
            myLinesCleared += linesCleared;
            myLineClearCombo++;
            if ((myLinesCleared % LEVEL_UP_AT) == 0) {
                modifyDifficulty();
            }
            setChanged();
            notifyObservers(myLineClearCombo + "linesCleared");
        } else {
            myLineClearCombo = 0;
            myCurrentCoords = getCurrentCoords(myBoard);
        }
        myFrozenData.clear();
        myFrozenData.addAll(myBoard.getFrozenBlocks());
        myPanel.repaint();
    }
    
    /**
     * Resizes panel proportionately.
     *
     * @param theDimension The dimension of the parent component.
     */
    public void changePanelSize(final Dimension theDimension) {
        boolean changed = false;
        if (theDimension.height / myBoard.getHeight()
                                               > theDimension.width / myBoard.getWidth()) {
            myBlockSide = theDimension.width / (double) myBoard.getWidth();
            changed = true;
        } else if (theDimension.height / myBoard.getHeight()
                                               < theDimension.width / myBoard.getWidth()) {
            myBlockSide = theDimension.height / (double) myBoard.getHeight();
            changed = true;
        } else if (theDimension.height / myBoard.getHeight()
                                               == theDimension.width / myBoard.getWidth()) {
            myBlockSide = theDimension.height / (double) myBoard.getHeight();
            changed = true;
        }
        if (changed) {
            final Dimension newDimension = new Dimension((int) myBlockSide
                                                          * myBoard.getWidth() + 1,
                                                        (int) myBlockSide
                                                        * myBoard.getHeight() + 1);
            myPanel.setSize(newDimension);
            myPanel.setMaximumSize(newDimension);
        }
        myPanel.repaint();
    }
    
    /**
     * Modifies the speed of the timer to change the difficulty.
     */
    private void modifyDifficulty() {
        myTimer.setDelay(myTimer.getDelay() - TIMING_STEP);
        setChanged();
        notifyObservers("delayShort");
    }
    
    /**
     * Ends the current game if active.
     */
    public void endGame() {
        myTimer.stop();
        myGameOver = true;
        myPanel.repaint();
    }
    
    /**
     * Creates a new game with the last board size.
     */
    public void newGame() {
        newGame(myBlocksWide, myBlocksTall);
    }
    
    /**
     * Creates a new game as long as the game is already over.
     *
     * @param theBlocksX Blocks wide
     * @param theBlocksY Blocks tall
     */
    public void newGame(final int theBlocksX, final int theBlocksY) {
        myBoard.newGame(theBlocksX, theBlocksY, null);
        myCurrentCoords = getCurrentCoords(myBoard);
        myLinesCleared = 0;
        myGameOver = false;
        setChanged();
        notifyObservers("clear");
        if (theBlocksX != myBlocksWide || theBlocksY != myBlocksTall) {
            final Dimension newDimension = new Dimension((int) myBlockSide
                                                              * theBlocksX + 1,
                                                              (int) myBlockSide
                                                              * theBlocksY);
            myPanel.setPreferredSize(newDimension);
            //myPanel.setMinimumSize(newDimension);
            myPanel.setMaximumSize(newDimension);
            myBlocksWide = theBlocksX;
            myBlocksTall = theBlocksY;
            setChanged();
            notifyObservers("gameSize");
        }
        myPanel.repaint();
        
    }
    
    /**
     * A key listener made to affect the board as keys are pressed.
     *
     * @see MyKeyEvent
     */
    private class MyKeyListener extends KeyAdapter {
        
        /**
         * Static string because pause is used twice.
         */
        private static final String PAUSE = "pause";
        
         /**
          * Handles a key being pressed.
          * 
          * @param theEvent The key pressed.
          */
        @Override
        public void keyPressed(final KeyEvent theEvent) {
            final int keyCode = theEvent.getKeyCode();
            
            if (keyCode == myControlMap.get("grid")) {
                if (myGridDraw) {
                    setGridDraw(false);
                } else {
                    setGridDraw(true);
                }
            }
            
            if (keyCode == myControlMap.get(PAUSE) && myPause && !myGameOver) {
                pause(false);
            } else if (!myPause && !myGameOver) {
                firstKeyHelper(keyCode);
            }
        }
        
        /**
         * Helps decide which action to take.
         *
         * @param theKeyCode the key code
         */
        private void firstKeyHelper(final int theKeyCode) {
            if (theKeyCode == myControlMap.get("right")) {
                myBoard.moveRight();
            } else if (theKeyCode == myControlMap.get("left")) {
                myBoard.moveLeft();
            } else if (theKeyCode == myControlMap.get("down")) {
                myBoard.moveDown();
            } else if (theKeyCode == myControlMap.get("rotate")) {
                myBoard.rotateCW();
            } else if (theKeyCode == myControlMap.get("drop")) {
                myBoard.hardDrop();
            } else if (theKeyCode == myControlMap.get(PAUSE)) {
                pause(true);
            }
        }
    }
    
    /**
     * The listener for receiving timer events.
     */
    private class TimerListener implements ActionListener {

        /**
         * Calls the step method as the timer activates.
         * 
         * @param theEvent The timer event
         */
        @Override
        public void actionPerformed(final ActionEvent theEvent) {
            myBoard.step();
        }
        
    }

    /**
     * A panel for drawing the game board.
     */
    private final class GraphicsPanel extends JPanel {
        
        /**
         * Constructs a new panel with double buffering active.
         */
        protected GraphicsPanel() {
            super(true);
        }
        
        /**
         * Draws the game on the panel.
         * 
         * @param theGraphics The graphics controller for displaying.
         */
        @Override
        public void paintComponent(final Graphics theGraphics) {
            final Graphics2D g2d = (Graphics2D) theGraphics;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
            drawBlocks(g2d);
            
            if (myGridDraw) {
                drawGrid(g2d); 
            }
            
            if (myGameOver) {
                paintWord(g2d, "Game Over", Color.RED.darker());
            } else if (myPause) {
                paintWord(g2d, "Paused", Color.GREEN.darker());
            }
        }
    }
}
