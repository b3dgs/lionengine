package com.b3dgs.lionengine.game.platform.background;

import com.b3dgs.lionengine.Graphic;

/**
 * Represents the background component interface, which will contain background elements.
 */
public interface BackgroundComponent
{
    /**
     * Update component.
     * @param extrp The extrapolation value.
     * @param x The horizontal offset.
     * @param y The vertical offset.
     * @param speed The scrolling speed.
     */
    void update(double extrp, int x, int y, double speed);

    /**
     * Render component.
     * 
     * @param g The graphic output.
     */
    void render(Graphic g);
}
