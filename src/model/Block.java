/*
 * TCSS 305 - Project Tetris
 */

package model;

import java.awt.Color;

/**
 * The different types of blocks that will be stored in a Board's grid.
 * 
 * @author Alan Fowler
 * @version Autumn 2014
 */
public enum Block {
    
    /** AN empty space in the grid. */
    EMPTY(Color.BLACK),
    /** A Block from an IPiece. */
    I(Color.CYAN),
    /** A Block from a JPiece. */
    J(Color.BLUE),
    /** A Block from an LPiece. */
    L(Color.ORANGE),
    /** A Block from an OPiece. */
    O(Color.YELLOW),
    /** A Block from an SPiece. */
    S(Color.GREEN),
    /** A Block from a TPiece. */
    T(Color.MAGENTA),
    /** A Block from a ZPiece. */
    Z(Color.RED);

    /**
     * The Color corresponding to a particular value of the enumeration.
     */
    private Color myColor;

    // Constructor

    /**
     * Constructs a new Block with the specified Color.
     * 
     * @param theColor The Color.
     */
    private Block(final Color theColor) {
        myColor = theColor;
    }

    /**
     * Returns the Color of this Block.
     * 
     * @return the Color of this Block.
     */
    public Color getColor() {
        return myColor;
    }

}
