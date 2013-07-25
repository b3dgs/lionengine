package com.b3dgs.lionengine.game.platform.background;

import com.b3dgs.lionengine.Graphic;

/**
 * Describe a standard interface for a scrolling background, depending of a speed and a vertical location.
 */
public interface Background
{
    /**
     * Background updates.
     * 
     * @param speed The scrolling speed.
     * @param y The background y.
     * @param extrp The extrapolation value.
     */
    void update(double speed, double y, double extrp);

    /**
     * Background renderings.
     * 
     * @param g The graphic output.
     */
    void render(Graphic g);
}
