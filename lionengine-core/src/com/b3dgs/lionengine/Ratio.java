package com.b3dgs.lionengine;

/**
 * List of standards ratio.
 */
public enum Ratio
{
    /** 4/3 ratio. */
    R4_3,
    /** 5/4 ratio. */
    R5_4,
    /** 16/9 wide ratio. */
    R16_9,
    /** 16/10 wide ratio. */
    R16_10;

    /** Constant representing the 4/3 screen ratio. */
    public static final double K4_3 = 4.0 / 3.0;
    /** Constant representing the 5/4 screen ratio. */
    public static final double K5_4 = 5.0 / 4.0;
    /** Constant representing the 16/9 screen ratio. */
    public static final double K16_9 = 16.0 / 9.0;
    /** Constant representing the 16/10 screen ratio. */
    public static final double K16_10 = 16.0 / 10.0;
    /** Error message ratio. */
    private static final String MESSAGE_ERROR_RATIO = "The ratio must not be null !";

    /**
     * Get the ratio value from its enum.
     * 
     * @param ratio The ratio enum.
     * @return The ratio value.
     */
    public static double getRatioFromEnum(Ratio ratio)
    {
        Check.notNull(ratio, Ratio.MESSAGE_ERROR_RATIO);
        switch (ratio)
        {
            case R4_3:
                return Ratio.K4_3;
            case R5_4:
                return Ratio.K5_4;
            case R16_9:
                return Ratio.K16_9;
            case R16_10:
                return Ratio.K16_10;
            default:
                throw new IllegalArgumentException("Invalid ratio value: " + ratio);
        }
    }

    /**
     * Get the ratio enum from its value.
     * 
     * @param width The width value.
     * @param height The height value.
     * @return The ratio enum.
     */
    public static Ratio getRatioFromValue(int width, int height)
    {
        final double ratio = width / (double) height;
        if (Double.compare(ratio, Ratio.K4_3) == 0)
        {
            return Ratio.R4_3;
        }
        else if (Double.compare(ratio, Ratio.K16_9) == 0)
        {
            return Ratio.R16_9;
        }
        else if (Double.compare(ratio, Ratio.K16_10) == 0)
        {
            return Ratio.R16_10;
        }
        else if (Double.compare(ratio, Ratio.K5_4) == 0)
        {
            return Ratio.R5_4;
        }
        throw new IllegalArgumentException("Invalid ratio value: " + ratio);
    }
}
