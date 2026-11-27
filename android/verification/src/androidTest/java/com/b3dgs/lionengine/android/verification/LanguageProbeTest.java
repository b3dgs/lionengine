/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.android.verification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

/**
 * Java language features compiled at level 21 and dexed for API 21 (ADR-0044, docs/android-compatibility.md): the
 * features verified by the owner, kept as a regression check of the toolchain, and the default, static and private
 * interface methods, which D8 desugars below API 24 and which were not verified before.
 */
@RunWith(AndroidJUnit4.class)
public final class LanguageProbeTest
{
    /**
     * Interface with a default, a static and a private method.
     */
    private interface Greeter
    {
        /**
         * Returns the name.
         *
         * @return The name.
         */
        String name();

        /**
         * Default method calling the abstract one and the private one.
         *
         * @return The greeting.
         */
        default String greet()
        {
            return prefix() + name();
        }

        /**
         * Private method.
         *
         * @return The prefix.
         */
        private String prefix()
        {
            return "Hello ";
        }

        /**
         * Static method.
         *
         * @param name The name.
         * @return A greeter of that name.
         */
        static Greeter of(String name)
        {
            return () -> name;
        }
    }

    /**
     * Implementation overriding the default method.
     */
    private static final class LoudGreeter implements Greeter
    {
        /**
         * Creates the greeter.
         */
        LoudGreeter()
        {
            super();
        }

        @Override
        public String name()
        {
            return "loud";
        }

        @Override
        public String greet()
        {
            return Greeter.super.greet().toUpperCase(java.util.Locale.ROOT);
        }
    }

    /**
     * Sealed hierarchy of the pattern probes.
     */
    private sealed interface Shape permits Circle, Square, Unit
    {
        // Marker of the sealed hierarchy.
    }

    /**
     * Circle.
     *
     * @param radius The radius.
     */
    private record Circle(double radius) implements Shape
    {
        // Record probe.
    }

    /**
     * Square.
     *
     * @param side The side.
     */
    private record Square(double side) implements Shape
    {
        // Record probe.
    }

    /**
     * Enumeration in the sealed hierarchy.
     */
    private enum Unit implements Shape
    {
        /** First constant. */
        ONE,
        /** Second constant. */
        TWO
    }

    /**
     * Nested class whose private member is read by the outer class (nestmates).
     */
    private static final class Nested
    {
        /** Private value. */
        private final int value;

        /**
         * Creates the instance.
         *
         * @param value The value.
         */
        private Nested(int value)
        {
            this.value = value;
        }
    }

    /**
     * Describes a shape with a pattern switch over the sealed hierarchy: types, record patterns, guards, {@code null},
     * and enumeration constants through a guard (a qualified constant label fails to dex, ADR-0044).
     *
     * @param shape The shape.
     * @return The description.
     */
    private static String describe(Shape shape)
    {
        return switch (shape)
        {
            case null -> "null";
            case Circle(double radius) when radius > 10.0 -> "large circle";
            case Circle circle -> "circle " + circle.radius();
            case Square(double side) -> "square " + side;
            case Unit unit when unit == Unit.ONE -> "one";
            case Unit unit -> "unit " + unit.ordinal();
        };
    }

    /**
     * Creates the test.
     */
    public LanguageProbeTest()
    {
        super();
    }

    /**
     * Default, static and private interface methods, and a call to an overridden default method with
     * {@code Interface.super}.
     */
    @Test
    public void testInterfaceMethods()
    {
        assertEquals("Hello world", Greeter.of("world").greet());
        assertEquals("HELLO LOUD", new LoudGreeter().greet());
    }

    /**
     * Lambdas capturing a local, method references, {@code var}, and nestmate access.
     */
    @Test
    public void testLambdasVarNestmates()
    {
        final int offset = 3;
        final Greeter greeter = () -> "n" + offset;
        final var nested = new Nested(41);
        final java.util.Comparator<String> comparator = String::compareTo;

        assertEquals("n3", greeter.name());
        assertEquals(42, nested.value + 1);
        assertTrue(comparator.compare("a", "b") < 0);
    }

    /**
     * Switch expressions with {@code yield}, a switch on strings, text blocks and string concatenation.
     */
    @Test
    public void testSwitchTextBlockConcatenation()
    {
        final String text = """
            line "one"
              line two\
             end
            """;
        final int length = switch (text.length())
        {
            case 0 -> 0;
            default ->
            {
                final int value = text.length();
                yield value;
            }
        };
        final String label = switch ("b")
        {
            case "a" -> "first";
            case "b" -> "second";
            default -> "other";
        };
        final Object absent = null;

        assertEquals("line \"one\"\n  line two end\n", text);
        assertEquals(text.length(), length);
        assertEquals("second", label);
        assertEquals("a1c2.5truenull", "a" + 1 + 'c' + 2.5 + true + absent);
    }

    /**
     * Records: accessors, equality, hash code and the text form of the JVM.
     */
    @Test
    public void testRecords()
    {
        final Circle circle = new Circle(1.5);

        assertEquals(1.5, circle.radius(), 0.0);
        assertEquals(new Circle(1.5), circle);
        assertEquals(new Circle(1.5).hashCode(), circle.hashCode());
        assertNotEquals(new Circle(2.0), circle);
        assertEquals("Circle[radius=1.5]", circle.toString());
    }

    /**
     * Pattern matching for {@code switch} and {@code instanceof} over a sealed hierarchy.
     */
    @Test
    public void testPatterns()
    {
        assertEquals("null", describe(null));
        assertEquals("large circle", describe(new Circle(11.0)));
        assertEquals("circle 1.0", describe(new Circle(1.0)));
        assertEquals("square 2.0", describe(new Square(2.0)));
        assertEquals("one", describe(Unit.ONE));
        assertEquals("unit 1", describe(Unit.TWO));
        final Object value = new Square(3.0);
        assertTrue(value instanceof Square(double side) && side == 3.0);
        assertFalse(value instanceof Circle);
    }
}
