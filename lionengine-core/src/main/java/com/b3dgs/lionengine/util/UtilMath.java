/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.util;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Static functions around math manipulation.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class UtilMath
{
    /**
     * Get the rounded floor or ceil value depending of the speed.
     * 
     * @param speed The speed value.
     * @param value The value to round.
     * @return The rounded value.
     */
    public static double getRound(double speed, double value)
    {
        if (speed < 0)
        {
            return Math.floor(value);
        }
        return Math.ceil(value);
    }

    /**
     * Check if value is between an interval.
     * 
     * @param value The value to check.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return <code>true</code> if between, <code>false</code> else.
     */
    public static boolean isBetween(int value, int min, int max)
    {
        return value >= min && value <= max;
    }

    /**
     * Check if value is between an interval.
     * 
     * @param value The value to check.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return <code>true</code> if between, <code>false</code> else.
     */
    public static boolean isBetween(double value, double min, double max)
    {
        return Double.compare(value, min) >= 0 && Double.compare(value, max) <= 0;
    }

    /**
     * Fix a value between an interval.
     * 
     * @param value The value to fix.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The fixed value.
     */
    public static int clamp(int value, int min, int max)
    {
        final int fixed;
        if (value < min)
        {
            fixed = min;
        }
        else if (value > max)
        {
            fixed = max;
        }
        else
        {
            fixed = value;
        }
        return fixed;
    }

    /**
     * Fix a value between an interval.
     * 
     * @param value The value to fix.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The fixed value.
     */
    public static double clamp(double value, double min, double max)
    {
        final double fixed;
        if (value < min)
        {
            fixed = min;
        }
        else if (value > max)
        {
            fixed = max;
        }
        else
        {
            fixed = value;
        }
        return fixed;
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
     * Get distance of two points.
     * 
     * @param x1 The point 1 x.
     * @param y1 The point 1 y.
     * @param x2 The point 2 x.
     * @param y2 The point 2 y.
     * @return The distance between them.
     */
    public static double getDistance(double x1, double y1, double x2, double y2)
    {
        return StrictMath.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    /**
     * Get distance from point to area.
     * 
     * @param x1 The first area x.
     * @param y1 The first area y.
     * @param x2 The second area x.
     * @param y2 The second area y.
     * @param w2 The second area width.
     * @param h2 The second area height.
     * @return The distance between them.
     */
    public static double getDistance(double x1, double y1, double x2, double y2, int w2, int h2)
    {
        final double maxX = x2 + w2;
        final double maxY = y2 + h2;

        double min = getDistance(x1, y1, x2, y2);
        for (double x = x2; x <= maxX; x++)
        {
            for (double y = y2; y <= maxY; y++)
            {
                final double dist = getDistance(x1, y1, x, y);
                if (dist < min)
                {
                    min = dist;
                }
            }
        }
        return min;
    }

    /**
     * Get distance between two areas.
     * 
     * @param x1 The first area x.
     * @param y1 The first area y.
     * @param w1 The first area width.
     * @param h1 The first area height.
     * @param x2 The second area x.
     * @param y2 The second area y.
     * @param w2 The second area width.
     * @param h2 The second area height.
     * @return The distance between them.
     */
    public static double getDistance(double x1, double y1, int w1, int h1, double x2, double y2, int w2, int h2)
    {
        final double maxX = x2 + w2;
        final double maxY = y2 + h2;

        double min = getDistance(x1, y1, x2, y2);
        for (double x = x2; x <= maxX; x++)
        {
            for (double y = y2; y <= maxY; y++)
            {
                final double dist = getDistance(x, y, x1, y1, w1, h1);
                if (dist < min)
                {
                    min = dist;
                }
            }
        }
        return min;
    }

    /**
     * Wrap value (keep value between min and max). Useful to keep an angle between 0 and 360 for example.
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
     * Get the rounded value.
     * 
     * @param value The value.
     * @param round The round factor.
     * @return The rounded value.
     */
    public static int getRounded(double value, int round)
    {
        return (int) Math.floor(value / round) * round;
    }

    /**
     * Get the rounded value with ceil.
     * 
     * @param value The value.
     * @param round The round factor.
     * @return The rounded value.
     */
    public static int getRoundedC(double value, int round)
    {
        return (int) Math.ceil(value / round) * round;
    }

    /**
     * Get cosinus in degree.
     * 
     * @param degree The angle in degree.
     * @return The cos value.
     */
    public static double cos(double degree)
    {
        return StrictMath.cos(StrictMath.toRadians(degree));
    }

    /**
     * Get sinus in degree.
     * 
     * @param degree The angle in degree.
     * @return The the sin value.
     */
    public static double sin(double degree)
    {
        return StrictMath.sin(StrictMath.toRadians(degree));
    }

    /**
     * Get the sign of a value.
     * 
     * @param value The value to check.
     * @return -1 if negative strict, 1 if positive strict, 0 if 0.
     */
    public static int getSign(double value)
    {
        final int sign;
        if (value > 0)
        {
            sign = 1;
        }
        else if (value < 0)
        {
            sign = -1;
        }
        else
        {
            sign = 0;
        }
        return sign;
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

    /**
     * Private constructor.
     */
    private UtilMath()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
