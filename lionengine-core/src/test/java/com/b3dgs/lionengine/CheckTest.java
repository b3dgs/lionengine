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

import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Check}.
 */
public final class CheckTest
{
    /**
     * Test the constructor.
     */
    @Test
    public void testConstructorPrivate()
    {
        assertPrivateConstructor(Check.class);
    }

    /**
     * Test with non <code>null</code> object.
     */
    @Test
    public void testNotNull()
    {
        Check.notNull(new Object());
    }

    /**
     * Test with a <code>null</code> object.
     */
    @Test
    public void testNotNullFail()
    {
        assertThrows(() -> Check.notNull(null), Check.ERROR_NULL);
    }

    /**
     * Test superior or equal valid cases.
     */
    @Test
    public void testSuperiorOrEqual()
    {
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            Check.superiorOrEqual(i, i);
            Check.superiorOrEqual(i + 1, i);
        }
        Check.superiorOrEqual(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Check.superiorOrEqual(Integer.MAX_VALUE, Integer.MIN_VALUE);
        Check.superiorOrEqual(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Test superior invalid cases.
     */
    @Test
    public void testSuperiorOrEqualFail()
    {
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            final int a = i;
            final int b = i + 1;
            assertThrows(() -> Check.superiorOrEqual(a, b), Check.ERROR_ARGUMENT + a + Check.ERROR_SUPERIOR + b);
        }
        assertThrows(() -> Check.superiorOrEqual(Integer.MIN_VALUE, Integer.MAX_VALUE),
                     Check.ERROR_ARGUMENT + Integer.MIN_VALUE + Check.ERROR_SUPERIOR + Integer.MAX_VALUE);
    }

    /**
     * Test superior or equal with double valid cases.
     */
    @Test
    public void testSuperiorOrEqualDouble()
    {
        final double factor = Double.MIN_VALUE;
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            final double value = i * factor;

            Check.superiorOrEqual(value, value);
            Check.superiorOrEqual(value + Double.MIN_VALUE, value);
        }
        Check.superiorOrEqual(Double.MIN_VALUE, Double.MIN_VALUE);
        Check.superiorOrEqual(Double.MAX_VALUE, Double.MIN_VALUE);
        Check.superiorOrEqual(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    /**
     * Test superior invalid cases.
     */
    @Test
    public void testSuperiorOrEqualDoubleFail()
    {
        final double factor = Double.MIN_VALUE;
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            final double a = i * factor;
            final double b = a + Double.MIN_VALUE;
            assertThrows(() -> Check.superiorOrEqual(a, b), Check.ERROR_ARGUMENT + a + Check.ERROR_SUPERIOR + b);
        }
        assertThrows(() -> Check.superiorOrEqual(Double.MIN_VALUE, Double.MAX_VALUE),
                     Check.ERROR_ARGUMENT + Double.MIN_VALUE + Check.ERROR_SUPERIOR + Double.MAX_VALUE);
    }

    /**
     * Test superior strict valid cases.
     */
    @Test
    public void testSuperiorStrict()
    {
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            Check.superiorStrict(i + 1, i);
        }
        Check.superiorStrict(Integer.MAX_VALUE, Integer.MIN_VALUE);
    }

    /**
     * Test superior strict invalid cases.
     */
    @Test
    public void testSuperiorStrictFail()
    {
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            final int a = i;
            final int b = i + 1;
            assertThrows(() -> Check.superiorStrict(a, b), Check.ERROR_ARGUMENT + a + Check.ERROR_SUPERIOR_STRICT + b);
        }
        assertThrows(() -> Check.superiorStrict(Integer.MIN_VALUE, Integer.MAX_VALUE),
                     Check.ERROR_ARGUMENT + Integer.MIN_VALUE + Check.ERROR_SUPERIOR_STRICT + Integer.MAX_VALUE);
    }

    /**
     * Test superior strict with double valid cases.
     */
    @Test
    public void testSuperiorStrictDouble()
    {
        final double factor = Double.MIN_VALUE;
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            final double value = i * factor;

            Check.superiorStrict(value + Double.MIN_VALUE, value);
        }
        Check.superiorStrict(Double.MAX_VALUE, Double.MIN_VALUE);
    }

    /**
     * Test superior strict invalid cases.
     */
    @Test
    public void testSuperiorStrictDoubleFail()
    {
        final double factor = Double.MIN_VALUE;
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            final double a = i * factor;
            final double b = a + Double.MIN_VALUE;
            assertThrows(() -> Check.superiorStrict(a, b), Check.ERROR_ARGUMENT + a + Check.ERROR_SUPERIOR_STRICT + b);
        }
        assertThrows(() -> Check.superiorStrict(Double.MIN_VALUE, Double.MAX_VALUE),
                     Check.ERROR_ARGUMENT + Double.MIN_VALUE + Check.ERROR_SUPERIOR_STRICT + Double.MAX_VALUE);
    }

    /**
     * Test inferior or equal valid cases.
     */
    @Test
    public void testInferiorOrEqual()
    {
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            Check.inferiorOrEqual(i, i);
            Check.inferiorOrEqual(i, i + 1);
        }
        Check.inferiorOrEqual(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Check.inferiorOrEqual(Integer.MIN_VALUE, Integer.MAX_VALUE);
        Check.inferiorOrEqual(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Test inferior invalid cases.
     */
    @Test
    public void testInferiorOrEqualFail()
    {
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            final int a = i + 1;
            final int b = i;
            assertThrows(() -> Check.inferiorOrEqual(a, b), Check.ERROR_ARGUMENT + a + Check.ERROR_INFERIOR + b);
        }
        assertThrows(() -> Check.inferiorOrEqual(Integer.MAX_VALUE, Integer.MIN_VALUE),
                     Check.ERROR_ARGUMENT + Integer.MAX_VALUE + Check.ERROR_INFERIOR + Integer.MIN_VALUE);
    }

    /**
     * Test inferior or equal with double valid cases.
     */
    @Test
    public void testInferiorOrEqualDouble()
    {
        final double factor = Double.MIN_VALUE;
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            final double value = i * factor;

            Check.inferiorOrEqual(value, value);
            Check.inferiorOrEqual(value, value + Double.MIN_VALUE);
        }
        Check.inferiorOrEqual(Double.MAX_VALUE, Double.MAX_VALUE);
        Check.inferiorOrEqual(Double.MIN_VALUE, Double.MAX_VALUE);
        Check.inferiorOrEqual(Double.MIN_VALUE, Double.MIN_VALUE);
    }

    /**
     * Test inferior invalid cases.
     */
    @Test
    public void testInferiorOrEqualDoubleFail()
    {
        final double factor = Double.MIN_VALUE;
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            final double a = i * factor + Double.MIN_VALUE;
            final double b = i * factor;
            assertThrows(() -> Check.inferiorOrEqual(a, b), Check.ERROR_ARGUMENT + a + Check.ERROR_INFERIOR + b);
        }
        assertThrows(() -> Check.inferiorOrEqual(Double.MAX_VALUE, Double.MIN_VALUE),
                     Check.ERROR_ARGUMENT + Double.MAX_VALUE + Check.ERROR_INFERIOR + Double.MIN_VALUE);
    }

    /**
     * Test inferior strict valid cases.
     */
    @Test
    public void testInferiorStrict()
    {
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            Check.inferiorStrict(i, i + 1);
        }
        Check.inferiorStrict(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Test inferior strict invalid cases.
     */
    @Test
    public void testInferiorStrictFail()
    {
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            final int a = i + 1;
            final int b = i;
            assertThrows(() -> Check.inferiorStrict(a, b), Check.ERROR_ARGUMENT + a + Check.ERROR_INFERIOR_STRICT + b);
        }
        assertThrows(() -> Check.inferiorStrict(Integer.MAX_VALUE, Integer.MIN_VALUE),
                     Check.ERROR_ARGUMENT + Integer.MAX_VALUE + Check.ERROR_INFERIOR_STRICT + Integer.MIN_VALUE);
    }

    /**
     * Test inferior strict with double valid cases.
     */
    @Test
    public void testInferiorStrictDouble()
    {
        final double factor = Double.MIN_VALUE;
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            final double value = i * factor;

            Check.inferiorStrict(value, value + Double.MIN_VALUE);
        }
        Check.inferiorStrict(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * Test inferior strict invalid cases.
     */
    @Test
    public void testInferiorStrictDoubleFail()
    {
        final double factor = Double.MIN_VALUE;
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            final double a = i * factor + Double.MIN_VALUE;
            final double b = i * factor;
            assertThrows(() -> Check.inferiorStrict(a, b), Check.ERROR_ARGUMENT + a + Check.ERROR_INFERIOR_STRICT + b);
        }
        assertThrows(() -> Check.inferiorStrict(Double.MAX_VALUE, Double.MIN_VALUE),
                     Check.ERROR_ARGUMENT + Double.MAX_VALUE + Check.ERROR_INFERIOR_STRICT + Double.MIN_VALUE);
    }

    /**
     * Test different valid cases.
     */
    @Test
    public void testDifferent()
    {
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            for (int j = -Constant.HUNDRED; j < Constant.HUNDRED; j++)
            {
                if (i != j)
                {
                    Check.different(i, j);
                }
            }
        }
    }

    /**
     * Test different valid cases.
     */
    @Test
    public void testDifferentDouble()
    {
        for (double i = -Constant.HUNDRED; Double.compare(i, Constant.HUNDRED) < 0; i += 0.9)
        {
            for (double j = -Constant.HUNDRED; Double.compare(j, Constant.HUNDRED) < 0; j += 0.9)
            {
                if (Double.compare(i, j) != 0)
                {
                    Check.different(i, j);
                }
            }
        }
    }

    /**
     * Test different invalid cases.
     */
    @Test
    public void testDifferentFail()
    {
        for (int i = -Constant.HUNDRED; i < Constant.HUNDRED; i++)
        {
            final int a = i;
            assertThrows(() -> Check.different(a, a), Check.ERROR_ARGUMENT + a + Check.ERROR_DIFFERENT + a);
        }
        assertThrows(() -> Check.different(Integer.MIN_VALUE, Integer.MIN_VALUE),
                     Check.ERROR_ARGUMENT + Integer.MIN_VALUE + Check.ERROR_DIFFERENT + Integer.MIN_VALUE);
    }

    /**
     * Test different invalid cases.
     */
    @Test
    public void testDifferentDoubleFail()
    {
        for (double i = -Constant.HUNDRED; Double.compare(i, Constant.HUNDRED) < 0; i += 0.9)
        {
            final double a = i;
            assertThrows(() -> Check.different(a, a), Check.ERROR_ARGUMENT + a + Check.ERROR_DIFFERENT + a);
        }
        assertThrows(() -> Check.different(Double.MIN_VALUE, Double.MIN_VALUE),
                     Check.ERROR_ARGUMENT + Double.MIN_VALUE + Check.ERROR_DIFFERENT + Double.MIN_VALUE);
    }

    /**
     * Test equality.
     */
    @Test
    public void testEquality()
    {
        Check.equality(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Check.equality(0, 0);
        Check.equality(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Test equality fail.
     */
    @Test
    public void testEqualityFail()
    {
        assertThrows(() -> Check.equality(Integer.MIN_VALUE, Integer.MAX_VALUE),
                     Check.ERROR_ARGUMENT + Integer.MIN_VALUE + Check.ERROR_EQUALS + Integer.MAX_VALUE);
    }
}
