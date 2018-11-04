/*
 * <pre> 
 * Class: <b>TetrisMain</b> 
 * File: TetrisMain.java 
 * Course: TCSS 305 – Autumn 2015
 * Assignment 6 – Tetris
 * Copyright 2015 Benjamin Abdipour
 * </pre>
 */

package view;

import java.awt.EventQueue;

/**
 * <pre>
 * This class is the starting point of the program. It includes 
 * the Main method and instantiates
 * a new TetrisGUI object.
 * </pre>
 * 
 * @author Benjamin Abdipour
 * @version 12/11/2015
 * @since November 20, 2015
 */
public final class TetrisMain {

    /**
     * Constructor to prevent illegal instantiation.
     */
    private TetrisMain() {
    }

    /**
     * Main method for running the program.
     * 
     * @param theArgs The argument array
     */
    public static void main(final String[] theArgs) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TetrisGUI();
            }
        });
    }
}
