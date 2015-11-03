/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

/**
 * Utility class check.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * Check.superiorStrict(value, 0);
 * final Object object = null;
 * Check.notNull(object);
 * </pre>
 * 
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class Check
{
    /** Argument error message. */
    private static final String ERROR_ARGUMENT = "Invalid argument: ";
    /** Null argument error message. */
    private static final String ERROR_NULL = "Unexpected null argument !";
    /** Superior comparison error. */
    private static final String ERROR_SUPERIOR = " is not superior or equal to ";
    /** Strictly superior comparison error. */
    private static final String ERROR_SUPERIOR_STRICT = " is not strictly superior to ";
    /** Inferior comparison error. */
    private static final String ERROR_INFERIOR = " is not inferior or equal to ";
    /** Strictly inferior comparison error. */
    private static final String ERROR_INFERIOR_STRICT = " is not strictly inferior to ";
    /** Different comparison error. */
    private static final String ERROR_DIFFERENT = " is not different to ";
    /** Equals comparison error. */
    private static final String ERROR_EQUALS = " is not equal to ";

    /**
     * Check if <code>a</code> is superior to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @throws LionEngineException If check failed.
     */
    public static void superiorOrEqual(int a, int b)
    {
        superior(a, b, false);
    }

    /**
     * Check if <code>a</code> is superior to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @throws LionEngineException If check failed.
     */
    public static void superiorOrEqual(double a, double b)
    {
        superior(a, b, false);
    }

    /**
     * Check if <code>a</code> is strictly superior to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @throws LionEngineException If check failed.
     */
    public static void superiorStrict(int a, int b)
    {
        superior(a, b, true);
    }

    /**
     * Check if <code>a</code> is strictly superior to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @throws LionEngineException If check failed.
     */
    public static void superiorStrict(double a, double b)
    {
        superior(a, b, true);
    }

    /**
     * Check if <code>a</code> is inferior to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @throws LionEngineException If check failed.
     */
    public static void inferiorOrEqual(int a, int b)
    {
        inferior(a, b, false);
    }

    /**
     * Check if <code>a</code> is inferior to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @throws LionEngineException If check failed.
     */
    public static void inferiorOrEqual(double a, double b)
    {
        inferior(a, b, false);
    }

    /**
     * Check if <code>a</code> is strictly inferior to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @throws LionEngineException If check failed.
     */
    public static void inferiorStrict(int a, int b)
    {
        inferior(a, b, true);
    }

    /**
     * Check if <code>a</code> is strictly inferior to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @throws LionEngineException If check failed.
     */
    public static void inferiorStrict(double a, double b)
    {
        inferior(a, b, true);
    }

    /**
     * Check if <code>a</code> is different to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @throws LionEngineException If check failed.
     */
    public static void different(int a, int b)
    {
        if (a == b)
        {
            throw argumentError(a, b, ERROR_DIFFERENT);
        }
    }

    /**
     * Check if <code>a</code> is equal to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @throws LionEngineException If check failed.
     */
    public static void equals(int a, int b)
    {
        if (a != b)
        {
            throw argumentError(a, b, ERROR_EQUALS);
        }
    }

    /**
     * Check if value is in the range (min and max included).
     * 
     * @param range The range to check.
     * @param value The value to check.
     */
    public static void range(Range range, int value)
    {
        inferior(value, range.getMax(), false);
        superior(value, range.getMin(), false);
    }

    /**
     * Check if the object is not <code>null</code>.
     * 
     * @param object The object to check.
     * @throws LionEngineException If object is <code>null</code>.
     */
    public static void notNull(Object object)
    {
        if (object == null)
        {
            throw new LionEngineException(ERROR_NULL);
        }
    }

    /**
     * Check if <code>a</code> is superior to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @param strict <code>true</code> for strictly superior, <code>false</code> for strict or equal superior.
     * @throws LionEngineException If check failed.
     */
    private static void superior(int a, int b, boolean strict)
    {
        if (strict)
        {
            if (a <= b)
            {
                throw argumentError(a, b, ERROR_SUPERIOR_STRICT);
            }
        }
        else
        {
            if (a < b)
            {
                throw argumentError(a, b, ERROR_SUPERIOR);
            }
        }
    }

    /**
     * Check if <code>a</code> is superior to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @param strict <code>true</code> for strictly superior, <code>false</code> for strict or equal superior.
     * @throws LionEngineException If check failed.
     */
    private static void superior(double a, double b, boolean strict)
    {
        if (strict)
        {
            if (!(Double.compare(a, b) > 0))
            {
                throw argumentError(a, b, ERROR_SUPERIOR_STRICT);
            }
        }
        else
        {
            if (Double.compare(a, b) < 0)
            {
                throw argumentError(a, b, ERROR_SUPERIOR);
            }
        }
    }

    /**
     * Check if <code>a</code> is inferior to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @param strict <code>true</code> for strictly inferior, <code>false</code> for strict or equal inferior.
     * @throws LionEngineException If check failed.
     */
    private static void inferior(int a, int b, boolean strict)
    {
        if (strict)
        {
            if (a >= b)
            {
                throw argumentError(a, b, ERROR_INFERIOR_STRICT);
            }
        }
        else
        {
            if (a > b)
            {
                throw argumentError(a, b, ERROR_INFERIOR);
            }
        }
    }

    /**
     * Check if <code>a</code> is inferior to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @param strict <code>true</code> for strictly inferior, <code>false</code> for strict or equal inferior.
     * @throws LionEngineException If check failed.
     */
    private static void inferior(double a, double b, boolean strict)
    {
        if (strict)
        {
            if (!(Double.compare(a, b) < 0))
            {
                throw argumentError(a, b, ERROR_INFERIOR_STRICT);
            }
        }
        else
        {
            if (Double.compare(a, b) > 0)
            {
                throw argumentError(a, b, ERROR_INFERIOR);
            }
        }
    }

    /**
     * Create an argument exception.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @param message The error message comparison.
     * @return The exception instance.
     */
    private static LionEngineException argumentError(int a, int b, String message)
    {
        return new LionEngineException(ERROR_ARGUMENT, String.valueOf(a), message, String.valueOf(b));
    }

    /**
     * Create an argument exception.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @param message The error message comparison.
     * @return The exception instance.
     */
    private static LionEngineException argumentError(double a, double b, String message)
    {
        return new LionEngineException(ERROR_ARGUMENT, String.valueOf(a), message, String.valueOf(b));
    }

    /**
     * Private constructor.
     */
    private Check()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
