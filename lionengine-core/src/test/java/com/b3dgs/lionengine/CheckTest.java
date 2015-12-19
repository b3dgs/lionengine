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

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the check class.
 */
public class CheckTest
{
    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(Check.class);
    }

    /**
     * Test the check with non null object.
     */
    @Test
    public void testNotNull()
    {
        Check.notNull(new Object());
    }

    /**
     * Test the check with a null object.
     */
    @Test(expected = LionEngineException.class)
    public void testNotNullFail()
    {
        Check.notNull(null);
    }

    /**
     * Test the check superior or equal valid cases.
     */
    @Test
    public void testSuperiorOrEqual()
    {
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
        {
            Check.superiorOrEqual(i, i);
            Check.superiorOrEqual(i + 1, i);
        }
        Check.superiorOrEqual(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Check.superiorOrEqual(Integer.MAX_VALUE, Integer.MIN_VALUE);
        Check.superiorOrEqual(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Test the check superior invalid cases.
     */
    @Test(expected = LionEngineException.class)
    public void testSuperiorOrEqualFail()
    {
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
        {
            try
            {
                Check.superiorOrEqual(i, i + 1);

                Assert.fail();
            }
            catch (final LionEngineException exception)
            {
                // Success
            }
        }
        Check.superiorOrEqual(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Test the check superior or equal with double valid cases.
     */
    @Test
    public void testSuperiorOrEqualDouble()
    {
        final double factor = Double.MIN_VALUE;
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
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
     * Test the check superior invalid cases.
     */
    @Test(expected = LionEngineException.class)
    public void testSuperiorOrEqualDoubleFail()
    {
        final double factor = Double.MIN_VALUE;
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
        {
            final double value = i * factor;
            try
            {
                Check.superiorOrEqual(value, value + Double.MIN_VALUE);
            }
            catch (final LionEngineException exception)
            {
                // Success
            }
        }
        Check.superiorOrEqual(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * Test the check superior strict valid cases.
     */
    @Test
    public void testSuperiorStrict()
    {
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
        {
            Check.superiorStrict(i + 1, i);
        }
        Check.superiorStrict(Integer.MAX_VALUE, Integer.MIN_VALUE);
    }

    /**
     * Test the check superior strict invalid cases.
     */
    @Test(expected = LionEngineException.class)
    public void testSuperiorStrictFail()
    {
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
        {
            try
            {
                Check.superiorStrict(i, i + 1);

                Assert.fail();
            }
            catch (final LionEngineException exception)
            {
                // Success
            }
        }
        Check.superiorStrict(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Test the check superior strict with double valid cases.
     */
    @Test
    public void testSuperiorStrictDouble()
    {
        final double factor = Double.MIN_VALUE;
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
        {
            final double value = i * factor;

            Check.superiorStrict(value + Double.MIN_VALUE, value);
        }
        Check.superiorStrict(Double.MAX_VALUE, Double.MIN_VALUE);
    }

    /**
     * Test the check superior strict invalid cases.
     */
    @Test(expected = LionEngineException.class)
    public void testSuperiorStrictDoubleFail()
    {
        final double factor = Double.MIN_VALUE;
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
        {
            final double value = i * factor;
            try
            {
                Check.superiorStrict(value, value + Double.MIN_VALUE);
            }
            catch (final LionEngineException exception)
            {
                // Success
            }
        }
        Check.superiorStrict(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * Test the check inferior or equal valid cases.
     */
    @Test
    public void testInferiorOrEqual()
    {
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
        {
            Check.inferiorOrEqual(i, i);
            Check.inferiorOrEqual(i, i + 1);
        }
        Check.inferiorOrEqual(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Check.inferiorOrEqual(Integer.MIN_VALUE, Integer.MAX_VALUE);
        Check.inferiorOrEqual(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Test the check inferior invalid cases.
     */
    @Test(expected = LionEngineException.class)
    public void testInferiorOrEqualFail()
    {
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
        {
            try
            {
                Check.inferiorOrEqual(i + 1, i);

                Assert.fail();
            }
            catch (final LionEngineException exception)
            {
                // Success
            }
        }
        Check.inferiorOrEqual(Integer.MAX_VALUE, Integer.MIN_VALUE);
    }

    /**
     * Test the check inferior or equal with double valid cases.
     */
    @Test
    public void testInferiorOrEqualDouble()
    {
        final double factor = Double.MIN_VALUE;
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
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
     * Test the check inferior invalid cases.
     */
    @Test(expected = LionEngineException.class)
    public void testInferiorOrEqualDoubleFail()
    {
        final double factor = Double.MIN_VALUE;
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
        {
            final double value = i * factor;
            try
            {
                Check.inferiorOrEqual(value + Double.MIN_VALUE, value);
            }
            catch (final LionEngineException exception)
            {
                // Success
            }
        }
        Check.inferiorOrEqual(Double.MAX_VALUE, Double.MIN_VALUE);
    }

    /**
     * Test the check inferior strict valid cases.
     */
    @Test
    public void testInferiorStrict()
    {
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
        {
            Check.inferiorStrict(i, i + 1);
        }
        Check.inferiorStrict(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Test the check inferior strict invalid cases.
     */
    @Test(expected = LionEngineException.class)
    public void testInferiorStrictFail()
    {
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
        {
            try
            {
                Check.inferiorStrict(i + 1, i);

                Assert.fail();
            }
            catch (final LionEngineException exception)
            {
                // Success
            }
        }
        Check.inferiorStrict(Integer.MAX_VALUE, Integer.MIN_VALUE);
    }

    /**
     * Test the check inferior strict with double valid cases.
     */
    @Test
    public void testInferiorStrictDouble()
    {
        final double factor = Double.MIN_VALUE;
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
        {
            final double value = i * factor;

            Check.inferiorStrict(value, value + Double.MIN_VALUE);
        }
        Check.inferiorStrict(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * Test the check inferior strict invalid cases.
     */
    @Test(expected = LionEngineException.class)
    public void testInferiorStrictDoubleFail()
    {
        final double factor = Double.MIN_VALUE;
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
        {
            final double value = i * factor;
            try
            {
                Check.inferiorStrict(value, value + Double.MIN_VALUE);
            }
            catch (final LionEngineException exception)
            {
                // Success
            }
        }
        Check.inferiorStrict(Double.MAX_VALUE, Double.MIN_VALUE);
    }

    /**
     * Test the check different valid cases.
     */
    @Test
    public void testDifferent()
    {
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
        {
            for (int j = -Constant.THOUSAND; j < Constant.THOUSAND; j++)
            {
                if (i != j)
                {
                    Check.different(i, j);
                }
            }
        }
    }

    /**
     * Test the check different invalid cases.
     */
    @Test
    public void testDifferentFail()
    {
        for (int i = -Constant.THOUSAND; i < Constant.THOUSAND; i++)
        {
            try
            {
                Check.different(i, i);

                Assert.fail();
            }
            catch (final LionEngineException exception)
            {
                // Success
            }
        }
    }

    /**
     * Test the check equals.
     */
    @Test
    public void testEquals()
    {
        Check.equals(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Check.equals(0, 0);
        Check.equals(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Test the check equals fail.
     */
    @Test(expected = LionEngineException.class)
    public void testEqualsFail()
    {
        Check.equals(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Test the check range with valid value.
     */
    @Test
    public void testRange()
    {
        Check.range(Range.INT_POSITIVE, 0);
        Check.range(Range.INT_POSITIVE, Integer.MAX_VALUE);

        Check.range(Range.INT_POSITIVE_STRICT, 1);
        Check.range(Range.INT_POSITIVE_STRICT, Integer.MAX_VALUE);
    }

    /**
     * Test the check range with too low value.
     */
    @Test(expected = LionEngineException.class)
    public void testRangeMin()
    {
        Check.range(Range.INT_POSITIVE_STRICT, 0);
    }

    /**
     * Test the check range with too high value.
     */
    @Test(expected = LionEngineException.class)
    public void testRangeMax()
    {
        Check.range(new Range(0, 1), 2);
    }
}
