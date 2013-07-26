package com.b3dgs.lionengine.game.platform.background;

import com.b3dgs.lionengine.Graphic;

/**
 * Foreground skeleton, used for future foregrounds implementation.
 */
public abstract class ForegroundPlatform
{
    /** Foreground theme name. */
    protected final String theme;
    /** Array of components. */
    protected BackgroundComponent[] components;
    /** Number of components. */
    protected int numberOfComponents;
    /** Wide state. */
    private boolean wide;

    /**
     * Create a new background.
     * 
     * @param theme The foreground theme.
     */
    public ForegroundPlatform(String theme)
    {
        this.theme = theme;
        wide = false;
        init();
    }

    /**
     * Background updates.
     * @param extrp The extrapolation value.
     * @param speed The scrolling speed.
     * @param y The foreground y.
     */
    public void update(double extrp, double speed, double y)
    {
        for (int i = 0; i < numberOfComponents; i++)
        {
            components[i].update(0, (int) y, speed, extrp);
        }
    }

    /**
     * Foreground primary renderings (before map).
     * 
     * @param g The graphic output.
     */
    public void primaryRender(Graphic g)
    {
        components[0].render(g);
    }

    /**
     * Foreground secondary renderings (after map).
     * 
     * @param g The graphic output.
     */
    public void secondaryRender(Graphic g)
    {
        for (int i = 1; i < numberOfComponents; i++)
        {
            components[i].render(g);
        }
    }

    /**
     * Set the wide state.
     * 
     * @param wide The wide state.
     */
    public void setWide(boolean wide)
    {
        this.wide = wide;
    }

    /**
     * Get wide state.
     * 
     * @return The wide state.
     */
    public boolean getWide()
    {
        return wide;
    }

    /**
     * Load foreground resources here, must be implemented.
     */
    protected abstract void load();

    /**
     * Initialize foreground.
     */
    private void init()
    {
        load();
    }
}
