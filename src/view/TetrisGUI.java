/*
 * <pre> 
 * Class: <b>TetrisGUI</b> 
 * File: TetrisGUI.java 
 * Course: TCSS 305 – Autumn 2015
 * Assignment 6 – Tetris
 * Copyright 2015 Benjamin Abdipour
 * </pre>
 */

package view;

import board.BoardPanel;
import board.InfoPanel;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import menu.MenuBar;

/**
 * <pre>
 * This class is the GUI of the game. It gets updates from the Board class and 
 * communicate with the two boards on the UI (BoardPanel and InfoPanel). The
 * class sets the main frame and the two above panels along with their size and
 * their layouts.
 * </pre>
 * 
 * @author Benjamin Abdipour
 * @version 12/11/2015
 * @since November 20, 2015
 */
public class TetrisGUI extends Observable implements Observer {

    /**
     * The default image location.
     */
    private static final String IMAGE_LOCATION = "images/";

    /**
     * The main icon file name.
     */
    private static final String MAIN_ICON_FILE_NAME = "icon.png";

    /**
     * The title of the game.
     */
    private static final String TITLE = "Tetris";

    /**
     * Represents the block dimension for tetris board. The
     * first number is the columns and the second number is rows.
     */
    private static final int[] BOARD_PANEL_DIMENSION = {10, 20};

    /**
     * Represents minimum dimension of the game.
     */
    private static final int[] GAME_INITIAL_DIMENSION = {500, 500};

    /**
     * Represents height and width of the main icon.
     */
    private static final int ICON_DIMENSIONS = 30;

    /**
     * The main frame in which the game is displayed.
     */
    private final JFrame myMainFrame;

    /**
     * The panel displaying the board of the game.
     */
    private final BoardPanel myBoardPanel;

    /**
     * The panel displaying the information of the current game.
     */
    private final InfoPanel myInfoPanel;

    /**
     * The constructor that instantiates the main frame and the tow panels. The
     * method also adds the two panels as the observers of the class. At the end,
     * the method inits the main frame and starts the game.
     */
    public TetrisGUI() {
        super();
        myMainFrame = new JFrame();
        myBoardPanel = new BoardPanel(BOARD_PANEL_DIMENSION[0], BOARD_PANEL_DIMENSION[1]);
        myInfoPanel = new InfoPanel(myBoardPanel);
        addObserver(myBoardPanel);
        addObserver(myInfoPanel);
        myBoardPanel.addBoardObserver(myInfoPanel);
        initMainFrame();
    }

    /**
     * Method that inits the main frame. It sets the main icon for the game and
     * notifies the observers in case of a change size event.
     */
    private void initMainFrame() {
        myMainFrame.setTitle(TITLE);

        myMainFrame.setMinimumSize(new Dimension(GAME_INITIAL_DIMENSION[0],
                                                 GAME_INITIAL_DIMENSION[1]));

        final Image localImageIcon = new ImageIcon(IMAGE_LOCATION 
                                                   + MAIN_ICON_FILE_NAME).getImage().
                        getScaledInstance(ICON_DIMENSIONS, ICON_DIMENSIONS, 
                                          java.awt.Image.SCALE_SMOOTH);
        myMainFrame.setIconImage(localImageIcon);

        myMainFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent theEvent) {
                setChanged();
                notifyObservers(theEvent);
            }
        });

        myMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JPanel mainPanel = new JPanel();
        myBoardPanel.setLayout(new FlowLayout());
        myInfoPanel.setLayout(new FlowLayout());
        mainPanel.setLayout(new GridLayout(1, 2));
        mainPanel.add(myBoardPanel);
        mainPanel.add(myInfoPanel);

        final JMenuBar menuBar = new MenuBar(myBoardPanel, myInfoPanel).getMenu();
        myMainFrame.setJMenuBar(menuBar);

        myMainFrame.add(mainPanel);
        myMainFrame.pack();
        myMainFrame.setLocationRelativeTo(null);
        myMainFrame.setVisible(true);
    }

    /**
     * Ignored update method, handled by the observers.
     */
    @Override
    public void update(final Observable arg0, final Object arg1) {
    }
}