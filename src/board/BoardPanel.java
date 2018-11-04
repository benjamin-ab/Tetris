/*
 * <pre> 
 * Class: <b>BoardPanel</b> 
 * File: BoardPanel.java 
 * Course: TCSS 305 – Autumn 2015
 * Assignment 6 – Tetris
 * Copyright 2015 Benjamin Abdipour
 * </pre>
 */

package board;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import model.AbstractPiece;
import model.Block;
import model.Board;
import model.Piece;
import view.TetrisGUI;

/**
 * <pre>
 * This class is the a panel that displays the board of blocks. The board will
 * be updated on a predefined time interval and each update displays a progress
 * in the game.
 * </pre>
 * 
 * @author Benjamin Abdipour
 * @version 12/11/2015
 * @since November 22, 2015
 */

public class BoardPanel extends JPanel implements Observer {

    /**
     * The auto-generated serial ID.
     */
    private static final long serialVersionUID = 309099333020990856L;

    /**
     * The initial scale to which panel should be drawn.
     */
    private static final int INITIAL_SCALE = 20;

    /**
     * A map that saves the user's key control preferences. The default values
     * are set in this class.
     */
    private static final Map<String, Integer> CONTROL_KEY_MAP = new HashMap<String, Integer>();

    /**
     * The initial timer delay and the time interval between each timer tick.
     * The delay in different levels is the level number multiply by the
     * TIMER_DELAY_CHANGE variable multiply by the INITIAL_TIMER_INTERVAL variable.
     * As a result, the initial timer delay of 2000 and the TIMER_DELAY_CHANGE of 0.5
     * gives us a 1000 timer delay for the first level.
     */
    private static final int INITIAL_TIMER_INTERVAL = 2000;

    /**
     * The speed increment percentage as the level goes up. 
     */
    private static final float TIMER_DELAY_CHANGE = 0.5f;

    /**
     * The panel's background color.
     */
    private static final Color PANEL_COLOR = Color.gray.brighter();

    /**
     * The board's background color.
     */
    private static final Color BOARD_COLOR = Color.gray;

    /**
     * The grid's color.
     */
    private static final Color GRID_COLOR = new Color(0, 0, 0, 15);

    /**
     * The running block color.
     */
    private static final Color RUNNING_BLOCK_COLOR = Color.red;

    /**
     * The dead block color.
     */
    private static final Color DEAD_BLOCK_COLOR = Color.white;

    /**
     * The ratio of scale to width.
     */
    private static final int WIDTH_RATIO = 24;

    /**
     * The ratio of scale to height.
     */
    private static final int HEIGHT_RATIO = 24;

    /**
     * The game board.
     */
    private final Board myBoard;

    /**
     * The timer controlling the speed of the game.
     */
    private final Timer myMainTimer;

    /**
     * The width of game board.
     */
    private final int myBoardWidth;

    /**
     * The height of the game board.
     */
    private final int myBoardHeight;

    /**
     * The scale to which panel contents should be drawn.
     */
    private int myScale;

    /**
     * The boolean that shows whether the grid option is enabled or not. True
     * means enabled and vice versa.
     */
    private boolean myGridEnabled;

    /**
     * Boolean that shows if the game is paused. True means paused and vice versa.
     */
    private boolean myIsPaused;

    /**
     * Boolean that shows if the game is playing. True means playing and vice versa.
     */
    private boolean myIsPlaying;

    /**
     * The key listener for an active game.
     */
    private ControlKeyMapSetter myPlayListener;

    /**
     * The key listener for a paused game.
     */
    private ControlKeyMapSetter myPauseListener;

    /**
     * The key listener for a paused game.
     */
    private int myLeftMargin;

    /**
     * The key listener for a paused game.
     */
    private int myTopMargin;

    /**
     * The header label that communicates with the user.
     */
    private final JLabel myHeader;
    
    /**
     * The difficulty level.
     */
    private int myDifficultyLevel;

    /**
     * Constructor for game board.
     * 
     * @param theWidth the width of the game board
     * @param theHeight the height of the game board
     */
    public BoardPanel(final int theWidth, final int theHeight) {
        super();
        myBoardWidth = theWidth;
        myBoardHeight = theHeight;
        myBoard = new Board(myBoardWidth, myBoardHeight, new LinkedList<Piece>());
        myBoard.addObserver(this);
        myMainTimer = new Timer(INITIAL_TIMER_INTERVAL, new TickListener());
        myHeader = new JLabel("", SwingConstants.CENTER);
        myDifficultyLevel = 1;
        setup();
    }

    /**
     * Activates the practice mode. Practice mode is the game simulator that helps
     * the user to understand the gameplay. In practice mode, the timer is not running
     * and the user can navigate the piece on the board using the control keys.
     */
    public void practiceMode() {
        clearBoard();
        addKeyListener(myPlayListener);
        myHeader.setText("Practice");
    }

    /**
     * Sets up the frame for a new game. 
     */
    public final void setup() {
        setFocusable(true);
        setBackground(PANEL_COLOR);
        myPlayListener = new ControlKeyMapSetter();
        myPauseListener = myPlayListener.getPauseListener();
        myGridEnabled = false;
        myScale = INITIAL_SCALE;
        myHeader.setBackground(PANEL_COLOR);
        myHeader.setOpaque(true);
        myHeader.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        add(myHeader);
    }

    /**
     * Starts a new game.
     */
    public void start() {
        myMainTimer.start();
    }

    /**
     * Starts a new game and eliminates the old game.
     */
    public void newGame() {
        myBoard.newGame(myBoardWidth, myBoardHeight, new LinkedList<Piece>());
        removeKeyListener(myPauseListener);
        addKeyListener(myPlayListener);
        myIsPlaying = true;
        myHeader.setText("Playing");
        setDifficulty(myDifficultyLevel);
        start();
    }

    /**
     * Clears the board to a stand-by mode.
     */
    public void clearBoard() {
        endGame();
        myBoard.newGame(myBoardWidth, myBoardHeight, new LinkedList<Piece>());
        pause();
        removeKeyListener(myPauseListener);
        addKeyListener(myPlayListener);
        myIsPlaying = false;
        myHeader.setText("");
    }

    /**
     * Method to enable or disable the grid.
     * 
     * @param theEnabled True means the grid option is enabled and vice versa
     */
    public void enableGrid(final boolean theEnabled) {
        myGridEnabled = theEnabled;
    }

    /**
     * Method to add observer to he board.
     * 
     * @param theObserver the observer to add to the board
     */
    public void addBoardObserver(final Observer theObserver) {
        myBoard.addObserver(theObserver);
    }

    /**
     * Returns the set of control keys.
     * 
     * @return map of control keys
     */
    public Map<String, Integer> getControlKeys() {
        return myPlayListener.getKeyMap();
    }

    /**
     * Paints the game board.
     * 
     * @param theGraphic the graphic object to be drawn
     */
    @Override
    public void paintComponent(final Graphics theGraphic) {
        super.paintComponent(theGraphic);
        final Graphics2D g2d = (Graphics2D) theGraphic;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                             RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(BOARD_COLOR);
        Stroke stroke = new BasicStroke(2);
        g2d.setStroke(stroke);
        g2d.fillRect(myLeftMargin, myTopMargin,
                     myBoardWidth * myScale + 1, myBoardHeight * myScale + 1);
        drawPanel(g2d);
    }

    /**
     * Draws blocks on the board panel.
     * 
     * @param theGraphic the graphics2D object to be drawn on
     */
    private void drawPanel(final Graphics2D theGraphic) {
        // draws frozen blocks
        int row = 0;
        for (final Block[] localBlock : myBoard.getFrozenBlocks()) {

            int column = 0;
            for (final Block block : localBlock) {
                if (block == Block.EMPTY) {
                    theGraphic.setColor(Color.gray);
                } else {
                    theGraphic.setColor(DEAD_BLOCK_COLOR);
                }
                theGraphic.fillRect(myLeftMargin + column * myScale,
                                    myTopMargin + (myBoardHeight - row - 1) 
                                    * myScale, myScale, myScale);
                theGraphic.setColor(Color.gray);
                theGraphic.drawRect(myLeftMargin + column * myScale,
                                    myTopMargin + (myBoardHeight - row - 1) 
                                    * myScale, myScale, myScale);
                column++;
            }
            row++;
        }

        // draws current block
        final Piece localPiece = myBoard.getCurrentPiece();
        final int[][] pieceCoordinates = ((AbstractPiece) localPiece).getBoardCoordinates();

        for (final int[] block : pieceCoordinates) {
            theGraphic.setColor(RUNNING_BLOCK_COLOR);

            theGraphic.fillRect(myLeftMargin + block[0] * myScale,
                                (myBoardHeight - block[1] - 1) 
                                * myScale + myTopMargin, myScale, myScale);
            theGraphic.setColor(Color.gray);
            theGraphic.drawRect(myLeftMargin + block[0] * myScale,
                                (myBoardHeight - block[1] - 1) 
                                * myScale + myTopMargin, myScale, myScale);
        }

        // updates the header label
        myHeader.setLocation(myLeftMargin , 0);
        myHeader.setSize(myBoardWidth * myScale + 1, myTopMargin);
        myHeader.setFont(new Font("Tahoma", Font.BOLD, myScale));

        // draws grid if it is enabled
        if (myGridEnabled) {
            theGraphic.setColor(GRID_COLOR);
            for (int i = 1; i < myBoardWidth; i++) {
                //vertical lines
                theGraphic.drawLine(myScale * i + myLeftMargin,
                                    myTopMargin, myScale * i + myLeftMargin,
                                    myBoardHeight * myScale + myTopMargin);
            }
            //horizontal lines
            for (int i = 1; i <= myBoardHeight; i++) {
                theGraphic.drawLine(myLeftMargin, myScale * i + myTopMargin,
                                    myBoardWidth * myScale + myLeftMargin,
                                    myScale * i + myTopMargin);
            }
        }
    }

    /**
     * Updates the board.
     * 
     * @param theObserver the in-action observable (GUI)
     * @param theObject the object passed in by observable object
     */
    @Override
    public void update(final Observable theObserver, final Object theObject) {
        if (theObserver instanceof TetrisGUI) {
            final Dimension currentSize = ((ComponentEvent) theObject).
                            getComponent().getSize();
            final double width = currentSize.getWidth();
            final double height = currentSize.getHeight();

            if (width < height) {
                myScale = (int) width / WIDTH_RATIO;
            } else {
                myScale = (int) height / HEIGHT_RATIO;
            }
        }

        myLeftMargin = (getWidth() - myBoardWidth * myScale) / 2;
        myTopMargin = (getHeight() - myBoardHeight * myScale) / 2;

        repaint();
    }

    /**
     * Returns the frozenblock size.
     * @return The frozenblock size
     */
    public int getFrozenBlockSize() {
        return myBoard.getFrozenBlocks().size();
    }

    /**
     * Method to pause game.
     */
    public void pause() {
        if (myMainTimer.isRunning() && !myIsPaused) {
            myMainTimer.stop();
            addKeyListener(myPauseListener);
            removeKeyListener(myPlayListener);
            myIsPaused = true;
            update((Observable) myBoard, null);
            myHeader.setText("Paused");
        } else if (!myBoard.isGameOver() && myIsPaused) {
            addKeyListener(myPlayListener);
            removeKeyListener(myPauseListener);
            myIsPaused = false;
            myMainTimer.start();
            myHeader.setText("Playing");
        }
    }

    /**
     * Method to stop the game.
     */
    public void endGame() {
        myMainTimer.stop();
        removeKeyListener(myPauseListener);
        removeKeyListener(myPlayListener);
        myIsPaused = false;
        myIsPlaying = false;
        myHeader.setText("");
    }

    /**
     * Getter that shows if a game is in progress.
     * @return boolean True means a game is playing and vice versa.
     */
    public boolean isPlaying() {
        return myIsPlaying;
    }

//    /**
//     * Sets the timer's time interval.
//     */
//    public void setTimeInterval() {
//        myMainTimer.setDelay((int) (myMainTimer.getDelay() * TIMER_DELAY_CHANGE));
//    }

    /**
     * Sets the difficulty of the game.
     * @param theDifficulty The desired difficulty level.
     */
    public void setDifficulty(final int theDifficulty) {
        myDifficultyLevel = theDifficulty;
        myMainTimer.setDelay((int) ((Math.pow(TIMER_DELAY_CHANGE,
                                              theDifficulty)) * INITIAL_TIMER_INTERVAL));
    }

    /**
     * Toggle the visibility of the grid.
     */
    public void toggleGrid() {
        myGridEnabled ^= true;
        repaint();
    }

    /**
     * <pre>
     * This class makes defines the actions need to be done on each timer's tick.
     * </pre>
     * 
     * @author Benjamin Abdipour
     * @version 12/11/2015
     * @since November 22, 2015
     */
    private class TickListener implements ActionListener {

        /**
         * Makes the actions to be done on each timer's tick.
         * 
         * @param theEvent the timer firing event
         */
        @Override
        public void actionPerformed(final ActionEvent theEvent) {
            if (myBoard.isGameOver()) {
                myIsPlaying = false;
                myMainTimer.stop();
                myHeader.setText("Game Over");
                for (final KeyListener localLitstener : getKeyListeners()) {
                    removeKeyListener(localLitstener);
                }
            } else {
                myBoard.step();
            }
        }
    }

    /**
     * <pre>
     * This class sets the key controllers of the game.
     * </pre>
     * 
     * @author Benjamin Abdipour
     * @version 12/11/2015
     * @since November 22, 2015
     */
    private class ControlKeyMapSetter extends KeyAdapter {

        /**
         * String representing move left action.
         */
        private static final String LEFT = "left";

        /**
         * String representing move right action.
         */
        private static final String RIGHT = "right";

        /**
         * String representing move down action.
         */
        private static final String DOWN = "down";

        /**
         * String representing drop action.
         */
        private static final String DROP = "drop";

        /**
         * String representing rotate action.
         */
        private static final String ROTATE = "rotate";

        /**
         * String representing pause action.
         */
        private static final String PAUSE = "pause";

        /**
         * Array of action names.
         */
        private final String[] myControls = {LEFT, RIGHT, DOWN, DROP, ROTATE, PAUSE };

        /**
         * Array of key codes for each of actions.
         */
        private final int[] myControlKeys = {KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT
                        , KeyEvent.VK_DOWN, KeyEvent.VK_UP, KeyEvent.VK_SPACE, KeyEvent.VK_P};

        /**
         * Constructor that sets up control key map.
         */
        public ControlKeyMapSetter() {
            super();
            for (int i = 0; i < myControls.length; i++) {
                CONTROL_KEY_MAP.put(myControls[i], myControlKeys[i]);
            }
        }

        /**
         * Returns the control key map.
         * 
         * @return key control map <name, keycode>
         */
        protected Map<String, Integer> getKeyMap() {
            return CONTROL_KEY_MAP;
        }

        /**
         * Query to obtain controls during game paused event.
         * 
         * @return key listener for paused game
         */
        protected ControlKeyPause getPauseListener() {
            return new ControlKeyPause();
        }

        /**
         * Defines the actions to the control keys.
         * 
         * @param theEvent the keyPressed event passed in
         */
        @Override
        public void keyPressed(final KeyEvent theEvent) {
            if (theEvent.getKeyCode() == CONTROL_KEY_MAP.get(LEFT)) {
                myBoard.moveLeft();
            } else if (theEvent.getKeyCode() == CONTROL_KEY_MAP.get(RIGHT)) {
                myBoard.moveRight();
            } else if (theEvent.getKeyCode() == CONTROL_KEY_MAP.get(DOWN)) {
                myBoard.moveDown();
            } else if (theEvent.getKeyCode() == CONTROL_KEY_MAP.get(DROP)) {
                myBoard.drop();
            } else if (theEvent.getKeyCode() == CONTROL_KEY_MAP.get(ROTATE)) {
                myBoard.rotate();
            } else if (theEvent.getKeyCode() == CONTROL_KEY_MAP.get(PAUSE)) {  
                pause();
            } 
        }

        /**
         * <pre>
         * This class responds to the pause key event.
         * </pre>
         * 
         * @author Benjamin Abdipour
         * @version 12/11/2015
         * @since November 22, 2015
         */
        private class ControlKeyPause extends ControlKeyMapSetter {

            /**
             * Responds only to corresponding unpause key event.
             * 
             * @param theEvent the key event to be analyzed
             */
            @Override
            public void keyPressed(final KeyEvent theEvent) {
                if (theEvent.getKeyCode() == CONTROL_KEY_MAP.get(PAUSE)) {  
                    pause();
                } 

            }
        }
    }
}