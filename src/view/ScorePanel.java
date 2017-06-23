/*
 * TCSS 305 - Autumn 2014
 * Assignment 6 Tetris
 */

package view;

import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This handles a panel for scoring the Tetris game.
 * 
 * @author James Brewer
 * @version B
 */
@SuppressWarnings("serial")
public class ScorePanel extends JPanel implements Observer {
      
    /**
     * Scale to display the delay in seconds rather than milliseconds.
     */
    private static final int TIME_SCALE = 1000;
    
    /**
     * Number of points per single line cleared. Multiplied in combos.
     */
    private static final int LINE_SCORE = 50;

    /**
     * The bonus given to the score when leveling up.
     */
    private static final int LEVEL_BONUS = 500;
    
    /**
     * Vertical space value between labels.
     */
    private static final int VERT_SPACE = 20;
    
    /**
     * Smaller vertical space value between labels.
     */
    private static final int SMALL_VERT_SPACE = 5;
    
    /**
     * Default font for labels.
     */
    private static final Font LABEL_FONT = new Font("helvetica", Font.PLAIN, 14);
    
    /**
     * Font size for larger labels.
     */
    private static final int LARGE_FONT_SIZE = 18;
    
    /**
     * Smaller font size.
     */
    private static final int SMALL_FONT_SIZE = 12;
    
    /**
     * Label for the score value.
     */
    private final JLabel myScoreLabel = new JLabel("Score: 0");
    
    /**
     * Label for the level number.
     */
    private final JLabel myLevelLabel = new JLabel("Level 1");
    
    /**
     * Label for the number of lines cleared.
     */
    private final JLabel myLinesLabel = new JLabel("Lines cleared: 0");
    
    /**
     * Label for the time delay value.
     */
    private final JLabel myTimeDelayLabel = new JLabel("Time delay: 1.0 s");
    /**
     * The current level number the user is on.
     */
    private int myLevel = 1;
    
    /**
     * The current game's score.
     */
    private int myScore;
    
    /**
     * Number of lines cleared.
     */
    private int myLinesCleared;
    
    /**
     * The timer delay between steps.
     */
    private BigDecimal myTimerDelay;
    
    /**
     * Timer delay decrease step.
     */
    private final BigDecimal myTimerStep;

    /**
     * Constructs a new scoring panel.
     * 
     * @param thePanel The given panel to get scoring data from.
     */
    public ScorePanel(final VisualBoard thePanel) {
        super(true);
        
        thePanel.addObserver(this);
        myTimerDelay = BigDecimal.valueOf((double) VisualBoard.DEFAULT_TIMING / TIME_SCALE);
        myTimerDelay = myTimerDelay.setScale(2);
        myTimerStep = BigDecimal.valueOf((double) VisualBoard.TIMING_STEP / TIME_SCALE);
        setupPanel();
        
    }
    
    /**
     * Sets up the size of the panel, adds the labels, and other details.
     */
    private void setupPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        setupScoreLabels();
        setupScoreInfo();
        
        this.setPreferredSize(this.getPreferredSize());
        this.setBackground(Color.GRAY);
    }
    
    /**
     * Helper method to setup scoring labels.
     */
    private void setupScoreLabels() {
        myLevelLabel.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        myLevelLabel.setAlignmentX(CENTER_ALIGNMENT);
        myLevelLabel.setFont(LABEL_FONT.deriveFont(Font.BOLD, LARGE_FONT_SIZE));
        myScoreLabel.setAlignmentX(CENTER_ALIGNMENT);
        myScoreLabel.setFont(LABEL_FONT);
        myLinesLabel.setAlignmentX(CENTER_ALIGNMENT);
        myLinesLabel.setFont(LABEL_FONT);
        myTimeDelayLabel.setAlignmentX(CENTER_ALIGNMENT);
        myTimeDelayLabel.setFont(LABEL_FONT);
        
        final Box scoreLayout = new Box(BoxLayout.Y_AXIS);
        scoreLayout.setOpaque(true);
        scoreLayout.setBackground(Color.WHITE);
        scoreLayout.setBorder(BorderFactory.createLoweredBevelBorder());
        scoreLayout.add(myLevelLabel);
        scoreLayout.add(Box.createVerticalStrut(SMALL_VERT_SPACE));
        scoreLayout.add(myScoreLabel);
        scoreLayout.add(Box.createVerticalStrut(VERT_SPACE));
        scoreLayout.add(myLinesLabel);
        scoreLayout.add(myTimeDelayLabel);
        scoreLayout.add(Box.createVerticalStrut(VERT_SPACE));
        final JLabel optionsLabel = new JLabel("  For controls, see the Options menu  ");
        optionsLabel.setAlignmentX(CENTER_ALIGNMENT);
        scoreLayout.add(optionsLabel);
        scoreLayout.add(Box.createVerticalStrut(SMALL_VERT_SPACE));
        final JLabel pauseLabel = new JLabel("Press 'P' to pause/unpause");
        pauseLabel.setAlignmentX(CENTER_ALIGNMENT);
        scoreLayout.add(pauseLabel);
        
        this.add(scoreLayout);
    }

    /**
     * Helper method to setup score info.
     */
    private void setupScoreInfo() {
        final Box infoLayout = new Box(BoxLayout.Y_AXIS);
        infoLayout.setOpaque(true);
        infoLayout.setBackground(Color.WHITE);
        infoLayout.setBorder(BorderFactory.createLoweredBevelBorder());
        final JLabel line1 = new JLabel("50 points - Single Line Cleared");
        line1.setAlignmentX(CENTER_ALIGNMENT);
        line1.setFont(LABEL_FONT.deriveFont(Font.ITALIC, SMALL_FONT_SIZE));
        final JLabel line2 = new JLabel("50 x N points - Each Additional Line");
        line2.setAlignmentX(CENTER_ALIGNMENT);
        line2.setFont(LABEL_FONT.deriveFont(Font.ITALIC, SMALL_FONT_SIZE));
        final JLabel line3 = new JLabel("  Ex: 3 lines = 50 + 100 + 150 = 300 pts  ");
        line3.setAlignmentX(CENTER_ALIGNMENT);
        line3.setFont(LABEL_FONT.deriveFont(Font.ITALIC, SMALL_FONT_SIZE));
        final JLabel line4 = new JLabel("500 x Lvl - Each Level Cleared");
        line4.setAlignmentX(CENTER_ALIGNMENT);
        line4.setFont(LABEL_FONT.deriveFont(Font.ITALIC, SMALL_FONT_SIZE));
        final JLabel line5 = new JLabel("Level increases every 5 cleared lines");
        line5.setAlignmentX(CENTER_ALIGNMENT);
        line5.setFont(LABEL_FONT.deriveFont(Font.ITALIC, SMALL_FONT_SIZE));
        infoLayout.add(line1);
        infoLayout.add(Box.createVerticalStrut(SMALL_VERT_SPACE));
        infoLayout.add(line2);
        infoLayout.add(Box.createVerticalStrut(SMALL_VERT_SPACE));
        infoLayout.add(line3);
        infoLayout.add(Box.createVerticalStrut(SMALL_VERT_SPACE));
        infoLayout.add(line4);
        infoLayout.add(Box.createVerticalStrut(SMALL_VERT_SPACE));
        infoLayout.add(line5);
        infoLayout.setAlignmentX(CENTER_ALIGNMENT);
        
        final JLabel title = new JLabel("Scoring Info");
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(LABEL_FONT.deriveFont(Font.BOLD));
        this.add(title);
        this.add(infoLayout);
    }
    
    /**
     * Updates the score data when the visual board panel notifies.
     * 
     * @param theObservable The object providing notification.
     * @param theData The data being passed.
     */
    @Override
    public void update(final Observable theObservable, final Object theData) {
        if ("linesCleared".equals(((String) theData).substring(1))) {
            myLinesCleared++;
            myScore += LINE_SCORE * Integer.parseInt(((String) theData).substring(0, 1));
            
        } else if ("delayShort".equals(theData)) {
            myTimerDelay = myTimerDelay.subtract(myTimerStep);
            myScore += LEVEL_BONUS * myLevel;
            myLevel++;
        } else if ("clear".equals(theData)) {
            myLevel = 1;
            myLinesCleared = 0;
            myScore = 0;
            myTimerDelay = BigDecimal.valueOf(
                                            (double) VisualBoard.DEFAULT_TIMING / TIME_SCALE);
        }
        myLevelLabel.setText("Level " + myLevel);
        myScoreLabel.setText("Score: " + myScore);
        myLinesLabel.setText("Lines cleared: " + myLinesCleared);
        myTimeDelayLabel.setText("Timer delay: " + myTimerDelay + " s");
    }
    
    
}
