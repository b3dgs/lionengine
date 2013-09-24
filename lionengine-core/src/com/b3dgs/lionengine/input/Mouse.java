package com.b3dgs.lionengine.input;

import java.awt.event.MouseEvent;

/**
 * Represents the mouse input. Gives informations such as mouse click and cursor location.
 */
public interface Mouse
{
    /** Left click. */
    int LEFT = MouseEvent.BUTTON1;
    /** Middle click. */
    int MIDDLE = MouseEvent.BUTTON2;
    /** Right click. */
    int RIGHT = MouseEvent.BUTTON3;

    /**
     * Update the mouse.
     */
    void update();

    /**
     * Lock mouse at its center.
     */
    void lock();

    /**
     * Lock mouse at specified location.
     * 
     * @param x The location x.
     * @param y The location y.
     */
    void lock(int x, int y);

    /**
     * Perform a click.
     * 
     * @param click The click to perform.
     */
    void doClick(int click);

    /**
     * Perform a click at specified coordinate.
     * 
     * @param click The click to perform.
     * @param x The location x.
     * @param y The location y.
     */
    void doClickAt(int click, int x, int y);

    /**
     * Set mouse center for lock operation.
     * 
     * @param x The location x.
     * @param y The location y.
     */
    void setCenter(int x, int y);

    /**
     * Get current pressed click.
     * 
     * @return The pressed click.
     */
    int getMouseClick();

    /**
     * Get location on screen x.
     * 
     * @return The location on screen x.
     */
    int getOnScreenX();

    /**
     * Get location on screen y.
     * 
     * @return The location on screen y.
     */
    int getOnScreenY();

    /**
     * Get location on window x.
     * 
     * @return The location on window x.
     */
    int getOnWindowX();

    /**
     * Get location on window y.
     * 
     * @return The location on window y.
     */
    int getOnWindowY();

    /**
     * Get mouse horizontal move.
     * 
     * @return The horizontal move.
     */
    int getMoveX();

    /**
     * Get mouse vertical move.
     * 
     * @return The vertical move.
     */
    int getMoveY();

    /**
     * Check if click if pressed.
     * 
     * @param click The click to check.
     * @return The pressed state.
     */
    boolean hasClicked(int click);

    /**
     * Check if click if pressed once only (ignore 'still clicked').
     * 
     * @param click The click to check.
     * @return The pressed state.
     */
    boolean hasClickedOnce(int click);

    /**
     * Check if mouse moved.
     * 
     * @return <code>true</code> if moved, <code>false</code> else.
     */
    boolean moved();
}
