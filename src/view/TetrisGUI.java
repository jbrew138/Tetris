/*
 * TCSS 305 - Autumn 2014
 * Assignment 6 Tetris
 */

package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import model.Board;

/**
 * This class includes the GUI window and initializes it's components.
 * 
 * @author James Brewer
 * @version B
 */
@SuppressWarnings("serial")
public class TetrisGUI extends JFrame implements Observer {
    
//    /**
//     * Minimum size of the frame based on smallest available board size.
//     */
//    private static final Dimension MIN_SIZE = new Dimension(547, 547);
    
    /**
     * Vertical space between sidebar pieces.
     */
    private static final int VERTICAL_SPACE = 100;

    /**
     * The game board.
     */
    private final Board myBoard;
    
    /**
     * A map that stores keyboard code values accessed by the name of the control.
     */
    private final Map<String, Integer> myControlMap = new HashMap<String, Integer>();
    
    /**
     * The game panel.
     */
    private final VisualBoard myBoardPanel;
    
    /**
     * The center container for the board panel.
     */
    private final Box myCenterLayout = new Box(BoxLayout.PAGE_AXIS);
    
    /**
     * Initializes the GUI.
     */
    public TetrisGUI() {
        super("TCSS 305 Tetris - JBrewer");
        final BoardSizeDialog dialog = new BoardSizeDialog();
        dialog.showSizeDialog();
        final Dimension boardSize = dialog.getResult();
        myBoard = new Board(boardSize.width, boardSize.height);
        myBoardPanel = new VisualBoard(myBoard, myControlMap);
        myBoardPanel.addObserver(this);
    }
    
    /**
     * Initializes the frame and it's components.
     */
    public void start() {
        
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setJMenuBar(new TetrisMenuBar(myBoardPanel, myControlMap, setupControls()));
        
        setupSidebar();
        
        
        myCenterLayout.setIgnoreRepaint(true);
        myCenterLayout.add(myBoardPanel.getPanel());
        myCenterLayout.addComponentListener(new BoardResizeListener());
        this.add(myCenterLayout);
        
        this.addWindowStateListener(new WindowStateListener() {
            
            /** The last dimension of the center layout before maximizing. */
            private Dimension myLastDimension;
            
            /** Helps re-center the game panel when the window is maximized and restored,
             * otherwise it is out of alignment while restoring.
             * @param theEvent The window's state is changed
             */
            @Override
            public void windowStateChanged(final WindowEvent theEvent) {
                if (TetrisGUI.this.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    myLastDimension = (Dimension) myCenterLayout.getSize().clone();
                } else if (TetrisGUI.this.getExtendedState() == JFrame.NORMAL) {
                    myCenterLayout.setSize((Dimension) myLastDimension.clone());
                    myBoardPanel.changePanelSize(myCenterLayout.getSize());
                }
            }
        });
        
        this.pack();
        this.setMinimumSize(getPreferredSize());
        
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();      
        this.setLocation((int) screenSize.getWidth() / 2 - this.getWidth() / 2,
                         (int) screenSize.getHeight() / 2 - this.getHeight() / 2);
        
        this.setVisible(true);
        myBoardPanel.getPanel().requestFocus(true);
    }
    
    /**
     * Sets up the layout of the sidebar.
     */
    private void setupSidebar() {
        final JPanel sidebar = new JPanel(true);
        sidebar.setBackground(Color.LIGHT_GRAY);
        
        final NextPiecePanel npPanel = new NextPiecePanel(myBoard);
        
        final ScorePanel scoreDisplay = new ScorePanel(myBoardPanel);
        
        final Box layout = new Box(BoxLayout.PAGE_AXIS);
        layout.add(npPanel);
        layout.add(Box.createVerticalStrut(VERTICAL_SPACE));
        layout.add(scoreDisplay);
        //layout.add(Box.createVerticalStrut(VERTICAL_SPACE));
        
        sidebar.add(layout);
        
        this.add(sidebar, BorderLayout.EAST);
    }
    
    /**
     * Setup the keyboard controls.
     * 
     * @return The array of control names.
     */
    private String[] setupControls() {
        final String[] ctrl = {"left", "right", "down", "rotate", "drop", "pause", "grid"};
        int i = 2; //Index required because of magic number warnings from 3 and above
        
        myControlMap.put(ctrl[0], KeyEvent.VK_LEFT);
        myControlMap.put(ctrl[1], KeyEvent.VK_RIGHT);
        myControlMap.put(ctrl[i], KeyEvent.VK_DOWN);
        i++;
        myControlMap.put(ctrl[i], KeyEvent.VK_UP);
        i++;
        myControlMap.put(ctrl[i], KeyEvent.VK_SPACE);
        i++;
        myControlMap.put(ctrl[i], KeyEvent.VK_P);
        i++;
        myControlMap.put(ctrl[i], KeyEvent.VK_G);
        
        return ctrl;
    }
    
    /**
     * Packs the panel when the visual board notifies of a change in size.
     * 
     * @param theObservable The object providing notification.
     * @param theData The data being passed.
     */
    @Override
    public void update(final Observable theObservable, final Object theData) {
        if ("gameSize".equals(theData)) {
            myCenterLayout.setSize(getPreferredSize());
            this.setMinimumSize(this.getPreferredSize());
            myBoardPanel.changePanelSize(myCenterLayout.getSize());
            this.pack();
        }
        
    }
    
    /**
     * The listener for receiving resize events.
     *
     * @see ResizeEvent
     */
    private class BoardResizeListener extends ComponentAdapter {
        
        /**
         * Component resized.
         *
         * @param theEvent the event
         */
        @Override
        public void componentResized(final ComponentEvent theEvent) {
            myBoardPanel.changePanelSize(theEvent.getComponent().getSize());
        }
    }
    
    
}
