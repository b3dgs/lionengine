package com.b3dgs.lionengine.game;

/**
 * Surface representation.
 */
public interface Surface
{
    /**
     * Get the current rounded horizontal location.
     * 
     * @return The current horizontal location.
     */
    int getLocationIntX();

    /**
     * Get the current rounded vertical location.
     * 
     * @return The current vertical location.
     */
    int getLocationIntY();

    /**
     * Get the width.
     * 
     * @return The width.
     */
    int getWidth();

    /**
     * Get the height.
     * 
     * @return The height.
     */
    int getHeight();
}
