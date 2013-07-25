package com.b3dgs.lionengine.utility;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Static functions around math manipulation.
 */
public final class UtilityMath
{
    /**
     * Private constructor.
     */
    private UtilityMath()
    {
        throw new RuntimeException();
    }

    /**
     * Fix a value between an interval.
     * 
     * @param value The value to fix.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The fixed value.
     */
    public static int fixBetween(int value, int min, int max)
    {
        int newValue = value;
        if (newValue < min)
        {
            newValue = min;
        }
        if (newValue > max)
        {
            newValue = max;
        }
        return newValue;
    }

    /**
     * Fix a value between an interval.
     * 
     * @param value The value to fix.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The fixed value.
     */
    public static double fixBetween(double value, double min, double max)
    {
        double newValue = value;
        if (newValue < min)
        {
            newValue = min;
        }
        if (newValue > max)
        {
            newValue = max;
        }
        return newValue;
    }

    /**
     * Apply progressive modifications to a value.
     * 
     * @param value The value.
     * @param dest The value to reach.
     * @param speed The effect speed.
     * @return The modified value.
     */
    public static double curveValue(double value, double dest, double speed)
    {
        final double reciprocal = 1.0 / speed;
        final double invReciprocal = 1.0 - reciprocal;
        return value * invReciprocal + dest * reciprocal;
    }

    /**
     * Get the intersection point of two lines.
     * 
     * @param l1 The first line.
     * @param l2 The second line.
     * @return The intersection point.
     */
    public static Point2D intersection(Line2D l1, Line2D l2)
    {
        final int x1 = (int) l1.getX1();
        final int x2 = (int) l1.getX2();
        final int y1 = (int) l1.getY1();
        final int y2 = (int) l1.getY2();

        final int x3 = (int) l2.getX1();
        final int x4 = (int) l2.getX2();
        final int y3 = (int) l2.getY1();
        final int y4 = (int) l2.getY2();

        final int d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (0 == d)
        {
            throw new IllegalStateException();
        }

        final int xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
        final int yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

        return new Point2D.Double(xi, yi);
    }

    /**
     * Get integer distance of two points.
     * 
     * @param x1 The point 1 x.
     * @param y1 The point 1 y.
     * @param x2 The point 2 x.
     * @param y2 The point 2 y.
     * @return The distance between point 1 and 2.
     */
    public static int getDistance(int x1, int y1, int x2, int y2)
    {
        return (int) StrictMath.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    /**
     * Get distance of two points.
     * 
     * @param x1 The point 1 x.
     * @param y1 The point 1 y.
     * @param x2 The point 2 x.
     * @param y2 The point 2 y.
     * @return The distance between point 1 and 2.
     */
    public static double getDistance(double x1, double y1, double x2, double y2)
    {
        return StrictMath.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    /**
     * Get distance in tile between the area.
     * 
     * @param tx1 The first tile x.
     * @param ty1 The first tile y.
     * @param tw1 The first width in tile.
     * @param th1 The first height in tile.
     * @param tx2 The second tile x.
     * @param ty2 The second tile y.
     * @param tw2 The second width in tile.
     * @param th2 The second height in tile.
     * @return The number of tiles between them.
     */
    public static int getDistance(int tx1, int ty1, int tw1, int th1, int tx2, int ty2, int tw2, int th2)
    {
        int min = Integer.MAX_VALUE;
        for (int h = tx2; h < tx2 + tw2; h++)
        {
            for (int v = ty2; v < ty2 + th2; v++)
            {
                final int dist = UtilityMath.getDistance(tx1, ty1, h, v);
                if (dist < min)
                {
                    min = dist;
                }
            }
        }
        return min;
    }

    /**
     * Wrap value (keep value between min and max).
     * 
     * @param value The input value.
     * @param min The minimum value (included).
     * @param max The maximum value (excluded).
     * @return The wrapped value.
     */
    public static double wrapDouble(double value, double min, double max)
    {
        double newValue = value;
        final double step = max - min;

        if (newValue >= max)
        {
            while (newValue >= max)
            {
                newValue -= step;
            }
        }
        else if (newValue < min)
        {
            while (newValue < min)
            {
                newValue += step;
            }
        }
        return newValue;
    }

    /**
     * Get cosinus in degree.
     * 
     * @param degree The angle in degree.
     * @return The cos value.
     */
    public static double cos(int degree)
    {
        return StrictMath.cos(StrictMath.toRadians(degree));
    }

    /**
     * Get sinus in degree.
     * 
     * @param degree The angle in degree.
     * @return The the sin value.
     */
    public static double sin(int degree)
    {
        return StrictMath.sin(StrictMath.toRadians(degree));
    }

    /**
     * Get the current time in millisecond.
     * 
     * @return The current time in millisecond.
     */
    public static long time()
    {
        return System.currentTimeMillis();
    }

    /**
     * Get the current time in nano second.
     * 
     * @return The current time in nano second.
     */
    public static long nano()
    {
        return System.nanoTime();
    }
}
