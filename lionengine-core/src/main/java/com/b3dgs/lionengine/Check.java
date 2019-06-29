/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine;

/**
 * Utility class check.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class Check
{
    /** Argument error message. */
    static final String ERROR_ARGUMENT = "Invalid argument: ";
    /** Null argument error message. */
    static final String ERROR_NULL = "Unexpected null argument !";
    /** Superior comparison error. */
    static final String ERROR_SUPERIOR = " is not superior or equal to ";
    /** Strictly superior comparison error. */
    static final String ERROR_SUPERIOR_STRICT = " is not strictly superior to ";
    /** Inferior comparison error. */
    static final String ERROR_INFERIOR = " is not inferior or equal to ";
    /** Strictly inferior comparison error. */
    static final String ERROR_INFERIOR_STRICT = " is not strictly inferior to ";
    /** Different comparison error. */
    static final String ERROR_DIFFERENT = " is not different to ";
    /** Equals comparison error. */
    static final String ERROR_EQUALS = " is not equal to ";

    /**
     * Check if <code>a</code> is superior or equal to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @throws LionEngineException If check failed.
     */
    public static void superiorOrEqual(int a, int b)
    {
        if (a < b)
        {
            throw new LionEngineException(ERROR_ARGUMENT + String.valueOf(a) + ERROR_SUPERIOR + String.valueOf(b));
        }
    }

    /**
     * Check if <code>a</code> is superior or equal to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @throws LionEngineException If check failed.
     */
    public static void superiorOrEqual(double a, double b)
    {
        if (a < b)
        {
            throw new LionEngineException(ERROR_ARGUMENT + String.valueOf(a) + ERROR_SUPERIOR + String.valueOf(b));
        }
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
        if (a <= b)
        {
            throw new LionEngineException(ERROR_ARGUMENT
                                          + String.valueOf(a)
                                          + ERROR_SUPERIOR_STRICT
                                          + String.valueOf(b));
        }
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
        if (Double.compare(a, b) <= 0)
        {
            throw new LionEngineException(ERROR_ARGUMENT
                                          + String.valueOf(a)
                                          + ERROR_SUPERIOR_STRICT
                                          + String.valueOf(b));
        }
    }

    /**
     * Check if <code>a</code> is inferior or equal to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @throws LionEngineException If check failed.
     */
    public static void inferiorOrEqual(int a, int b)
    {
        if (a > b)
        {
            throw new LionEngineException(ERROR_ARGUMENT + String.valueOf(a) + ERROR_INFERIOR + String.valueOf(b));
        }
    }

    /**
     * Check if <code>a</code> is inferior or equal to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @throws LionEngineException If check failed.
     */
    public static void inferiorOrEqual(double a, double b)
    {
        if (a > b)
        {
            throw new LionEngineException(ERROR_ARGUMENT + String.valueOf(a) + ERROR_INFERIOR + String.valueOf(b));
        }
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
        if (a >= b)
        {
            throw new LionEngineException(ERROR_ARGUMENT
                                          + String.valueOf(a)
                                          + ERROR_INFERIOR_STRICT
                                          + String.valueOf(b));
        }
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
        if (a > b)
        {
            throw new LionEngineException(ERROR_ARGUMENT
                                          + String.valueOf(a)
                                          + ERROR_INFERIOR_STRICT
                                          + String.valueOf(b));
        }
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
            throw new LionEngineException(ERROR_ARGUMENT + String.valueOf(a) + ERROR_DIFFERENT + String.valueOf(b));
        }
    }

    /**
     * Check if <code>a</code> is different to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @throws LionEngineException If check failed.
     */
    public static void different(double a, double b)
    {
        if (Double.compare(a, b) == 0)
        {
            throw new LionEngineException(ERROR_ARGUMENT + String.valueOf(a) + ERROR_DIFFERENT + String.valueOf(b));
        }
    }

    /**
     * Check if <code>a</code> is equal to <code>b</code>.
     * 
     * @param a The parameter to test.
     * @param b The parameter to compare to.
     * @throws LionEngineException If check failed.
     */
    public static void equality(int a, int b)
    {
        if (a != b)
        {
            throw new LionEngineException(ERROR_ARGUMENT + String.valueOf(a) + ERROR_EQUALS + String.valueOf(b));
        }
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
     * Private constructor.
     */
    private Check()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
