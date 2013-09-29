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
     * Get the horizontal offset location.
     * 
     * @return The horizontal offset location.
     */
    int getLocationOffsetX();
    
    /**
     * Get the vertical offset location.
     * 
     * @return The vertical offset location.
     */
    int getLocationOffsetY();

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
