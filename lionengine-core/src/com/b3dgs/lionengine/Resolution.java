package com.b3dgs.lionengine;

/**
 * Describes a display resolution. It allows to define different parameters:
 * <ul>
 * <li><code>width & height</code> : represent the screen size</li>
 * <li><code>ratio</code>, which is computed by using the <code>width & height</code>, allows to know the screen ratio.</li>
 * <li><code>rate</code> : represents the screen refresh rate (in frames per seconds)</li>
 * </ul>
 * The This class is mainly used to describe the display resolution chosen.
 */
public final class Resolution
{
    /** Display width. */
    private int width;
    /** Display height. */
    private int height;
    /** Display ratio. */
    private double ratio;
    /** Display rate. */
    private int rate;

    /**
     * Constructor.
     * 
     * @param width The display width (in pixel).
     * @param height The display height (in pixel).
     * @param rate The refresh rate (usually 50 or 60).
     */
    public Resolution(int width, int height, int rate)
    {
        set(width, height, rate);
    }

    /**
     * Set the resolution.
     * 
     * @param width The display width (in pixel).
     * @param height The display height (in pixel).
     */
    public void set(int width, int height)
    {
        set(width, height, rate);
    }

    /**
     * Set the resolution.
     * 
     * @param width The display width (in pixel).
     * @param height The display height (in pixel).
     * @param rate The refresh rate in hertz (usually 50 or 60).
     */
    public void set(int width, int height, int rate)
    {
        Check.argument(width > 0 && height > 0 && rate >= 0);
        this.width = width;
        this.height = height;
        this.ratio = width / (double) height;
        this.rate = rate;
    }

    /**
     * Set the ratio and adapt the resolution to the new ratio (based on the height value).
     * 
     * @param ratio The new ratio.
     */
    public void setRatio(double ratio)
    {
        if (!Ratio.equals(this.ratio, ratio))
        {
            width = (int) Math.floor(height * ratio);
            this.ratio = ratio;
        }
    }

    /**
     * Set the refresh rate value in hertz.
     * 
     * @param rate The refresh rate value.
     */
    public void setRate(int rate)
    {
        this.rate = rate;
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
     * Get the display ratio.
     * 
     * @return The display ratio.
     */
    public double getRatio()
    {
        return ratio;
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
}
