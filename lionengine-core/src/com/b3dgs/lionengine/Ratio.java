package com.b3dgs.lionengine;

/**
 * List of standard ratios.
 */
public final class Ratio
{
    /** Constant representing the 4/3 screen ratio. */
    public static final double R4_3 = 4.0 / 3.0;
    /** Constant representing the 5/4 screen ratio. */
    public static final double R5_4 = 5.0 / 4.0;
    /** Constant representing the 16/9 screen ratio. */
    public static final double R16_9 = 16.0 / 9.0;
    /** Constant representing the 16/10 screen ratio. */
    public static final double R16_10 = 16.0 / 10.0;

    /**
     * Get the ratio enum from its value.
     * 
     * @param ratio1 The ratio1 value.
     * @param ratio2 The ratio2 value.
     * @return <code>true</code> if equals, <code>false</code> else.
     */
    public static boolean equals(double ratio1, double ratio2)
    {
        return Double.compare(ratio1, ratio2) == 0;
    }

    /**
     * Private constructor.
     */
    private Ratio()
    {
        throw new RuntimeException();
    }
}
