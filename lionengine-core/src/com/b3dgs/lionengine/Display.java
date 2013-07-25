package com.b3dgs.lionengine;

/**
 * Describes a display screen configuration. It allows to define different parameters:
 * <ul>
 * <li><code>width & height</code> : represent the screen size</li>
 * <li><code>depth</code> : represents the screen color depth (number of colors)</li>
 * <li><code>rate</code> : represents the screen refresh rate (in frames per seconds)</li>
 * </ul>
 * The <code>ratio</code>, which is computed by using the <code>width & height</code>, allows to know the screen ratio.
 * This class is mainly used to describe the internal & external display.
 */
public final class Display
{
    /** Error message display. */
    private static final String MESSAGE_ERROR_DISPLAY = "Invalid display parameters !";
    /** Display width. */
    private final int width;
    /** Display height. */
    private final int height;
    /** Display depth. */
    private final int depth;
    /** Display rate. */
    private final int rate;
    /** Display ratio. */
    private final Ratio ratio;

    /**
     * Create a display.
     * 
     * @param width The screen width (in pixel).
     * @param height The screen height (in pixel).
     * @param depth The screen colour depth (usually 16 or 32).
     * @param rate The refresh rate (usually 50 or 60).
     */
    public Display(int width, int height, int depth, int rate)
    {
        Check.argument(width >= 0 && height >= 0 && depth >= 0 && rate >= 0, Display.MESSAGE_ERROR_DISPLAY);
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.rate = rate;
        ratio = Ratio.getRatioFromValue(this.width, this.height);
    }

    /**
     * Get the display width.
     * 
     * @return The display width.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Get the display height.
     * 
     * @return The display height.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Get the display depth.
     * 
     * @return The display depth.
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * Get the display rate.
     * 
     * @return The display rate.
     */
    public int getRate()
    {
        return rate;
    }

    /**
     * Get the display ratio.
     * 
     * @return The display ratio.
     */
    public Ratio getRatio()
    {
        return ratio;
    }
}
