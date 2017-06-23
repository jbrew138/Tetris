/*
 * TCSS 305 - Autumn 2014
 * Assignment 6 Tetris
 */

package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.AbstractPiece;
import model.Board;
import model.Piece;

/**
 * @author James Brewer
 * @version A
 *
 */
@SuppressWarnings("serial")
public class NextPiecePanel extends JPanel implements Observer {
    
    /**
     * Number of horizontal grid spaces.
     */
    private static final int GRID_X = 6;
    
    /**
     * Number of vertical grid spaces.
     */
    private static final int GRID_Y = 4;
    
    /**
     * The default side length of a block.
     */
    private static final int BLOCK_SIDE = 25;
    
    /**
     * The thickness of the grid lines.
     */
    private static final int GRID_LINE_WIDTH = 1;
    
    /**
     * The panel dimension, dependent on the block size.
     */
    private final Dimension myPanelSize = new Dimension(GRID_X * BLOCK_SIDE + 1,
                                                        GRID_Y * BLOCK_SIDE + 1);
    
    /**
     * The next piece to be played.
     */
    private Piece myPiece;
    
    /**
     * Coordinate data of the next piece.
     */
    private int[][] myPieceData;
    
    /**
     * Constructs a new panel for the next piece, given a board.
     * 
     * @param theBoard The board the panel will display.
     */
    public NextPiecePanel(final Board theBoard) {
        super(true);
        
        this.setFocusable(false);
        theBoard.addObserver(this);
        try {
            myPiece = ((AbstractPiece) theBoard.getNextPiece()).clone();
            myPieceData = getPieceData(myPiece, theBoard.getHeight(), theBoard.getWidth());
        } catch (final CloneNotSupportedException e) {
            JOptionPane.showMessageDialog(null, "Error copying initial next piece", "WARNING",
                                          JOptionPane.WARNING_MESSAGE);
        }
        this.setPreferredSize(myPanelSize);
        this.setMaximumSize(myPanelSize);
        this.setBackground(Color.BLACK);
    }
    
    /**
     * Draws the next piece on the panel.
     * 
     * @param theGraphics The graphics controller for displaying.
     */
    @Override
    public void paintComponent(final Graphics theGraphics) {
        final Graphics2D g2d = (Graphics2D) theGraphics;
        g2d.clearRect(0, 0, myPanelSize.width, myPanelSize.height);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, myPanelSize.width, myPanelSize.height);
        drawGrid(g2d);

        
        g2d.setColor(((AbstractPiece) myPiece).getBlock().getColor());
        for (final int[] block: myPieceData) {
            g2d.fillRect(block[0] * BLOCK_SIDE, block[1] * BLOCK_SIDE, BLOCK_SIDE, BLOCK_SIDE);
        }
        
    }

    /**
     * Draws a grid based on the panel's size.
     *
     * @param theG2d The graphics component.
     */
    private void drawGrid(final Graphics2D theG2d) {
        final Dimension panelSize = this.getSize();
        final int height = (int) panelSize.getHeight();
        final int width = (int) panelSize.getWidth();
        theG2d.setStroke(new BasicStroke(GRID_LINE_WIDTH));
        theG2d.setColor(Color.GRAY);
        for (int y = 0; y <= height; y = y + BLOCK_SIDE) {
            theG2d.draw(new Line2D.Double(0, y, width, y));
        }
        for (int x = 0; x <= width; x = x + BLOCK_SIDE) {
            theG2d.draw(new Line2D.Double(x, 0, x, height));
        }
    }
    
    /**
     * Gets the data of the piece.
     *
     * @param thePiece the piece
     * @param theBoardY the height of the board
     * @param theBoardX the width of the board
     * @return Block coordinates of the piece
     */
    private int[][] getPieceData(final Piece thePiece, final int theBoardY,
                                                         final int theBoardX) {
        final int[][] boardCoords = ((AbstractPiece) thePiece).getBoardCoordinates();
        final int highY = theBoardY + 2;
        final int lowY = theBoardY + 1;
        final int xMargin = 6;
        final int shiftX = (theBoardX - xMargin) / 2;
        
        //Block coordinates converted into small panel coordinates
        for (int i = boardCoords.length - 1; i >= 0; i--) {
            if (boardCoords[i][1] == highY) {
                boardCoords[i][1] = 1;
            } else if (boardCoords[i][1] == lowY) {
                boardCoords[i][1] = 2;
            }
            boardCoords[i][0] = boardCoords[i][0] - shiftX;
        }
               
        return boardCoords;
    }
    

    /**
     * Update the panel when the board notifies.
     * 
     * @param theObservable The object notifying.
     * @param theData Optional data being passed.
     */
    @Override
    public void update(final Observable theObservable, final Object theData) {
        final Piece otherPiece = ((Board) theObservable).getNextPiece();
        if (!((Board) theObservable).getNextPiece().equals(myPiece)) {
            try {
                myPiece = ((AbstractPiece) otherPiece).clone();
                myPieceData = getPieceData(myPiece, ((Board) theObservable).getHeight(), 
                                           ((Board) theObservable).getWidth());
                repaint();
            } catch (final CloneNotSupportedException e) {
                JOptionPane.showMessageDialog(null, "Error copying next piece", "ERROR",
                                              JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
