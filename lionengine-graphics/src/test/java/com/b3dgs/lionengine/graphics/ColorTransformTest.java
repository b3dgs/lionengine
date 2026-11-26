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
package com.b3dgs.lionengine.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.b3dgs.lionengine.render.ColorEffect;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Test {@link ColorTransform}: the mask, the tint and the alpha, the order of the parts, the rounding that makes the
 * CPU path independent of the split, the split for a backend capability, and the canonical text (REQ-SPRITE-004,
 * ADR-0056).
 */
final class ColorTransformTest
{
    /** Pixels and transforms drawn for the path independence check. */
    private static final int SAMPLES = 10_000;

    /**
     * Returns a backend capability.
     *
     * @param tint {@code true} if the tint is applied per frame.
     * @param alpha {@code true} if the alpha is applied per frame.
     * @return The recording backend, as capability.
     */
    private static RecordingBackend capability(boolean tint, boolean alpha)
    {
        final RecordingBackend backend = new RecordingBackend();
        backend.setSupported(ColorEffect.TINT, tint);
        backend.setSupported(ColorEffect.ALPHA, alpha);
        return backend;
    }

    /**
     * Returns a transform with every part.
     *
     * @return The transform.
     */
    private static ColorTransform full()
    {
        return ColorTransform.NONE.withGrading(ColorMatrix.saturation(0.5))
                                  .withMask(0x00FF00, 0.25)
                                  .withTint(0xC0FF8040)
                                  .withAlpha(0.75);
    }

    /**
     * The identity has no part and changes no pixel.
     */
    @Test
    void testNone()
    {
        assertTrue(ColorTransform.NONE.isIdentity());
        assertSame(ColorMatrix.IDENTITY, ColorTransform.NONE.getGrading());
        assertEquals(0, ColorTransform.NONE.getMaskColor());
        assertEquals(0.0, ColorTransform.NONE.getMaskAmount());
        assertEquals(CommandBuffer.NO_TINT, ColorTransform.NONE.getTint());
        assertEquals(1.0, ColorTransform.NONE.getAlpha());
        assertEquals(0x80C08040, ColorTransform.NONE.apply(0x80C08040));
        assertEquals(0x00000000, ColorTransform.NONE.apply(0x00000000));
        assertEquals("none", ColorTransform.NONE.toString());
    }

    /**
     * The {@code with} methods return a new transform and leave the original unchanged.
     */
    @Test
    void testImmutable()
    {
        final ColorTransform faded = ColorTransform.NONE.withAlpha(0.5);
        final ColorTransform tinted = faded.withTint(0xFF00FF00);

        assertEquals(0.5, faded.getAlpha());
        assertEquals(CommandBuffer.NO_TINT, faded.getTint());
        assertEquals(0.5, tinted.getAlpha());
        assertEquals(0xFF00FF00, tinted.getTint());
        assertTrue(ColorTransform.NONE.isIdentity());
        assertFalse(faded.isIdentity());
    }

    /**
     * The mask blends every pixel toward its color by its amount and keeps the alpha; the alpha bits of the mask
     * color are ignored, and an amount of {@code 0} is no mask.
     */
    @Test
    void testMask()
    {
        final ColorTransform silhouette = ColorTransform.NONE.withMask(0xFFFFFF, 1.0);
        final ColorTransform half = ColorTransform.NONE.withMask(0xFF0000, 0.5);

        assertEquals(0x80FFFFFF, silhouette.apply(0x80123456));
        assertEquals(0x00FFFFFF, silhouette.apply(0x00000000));
        assertEquals(0xFF800000, half.apply(0xFF000000));
        assertEquals(0xFFFF8080, half.apply(0xFFFFFFFF));
        assertEquals(0xFF0000, half.getMaskColor());
        assertEquals(0.5, half.getMaskAmount());
        assertEquals(half, ColorTransform.NONE.withMask(0x12FF0000, 0.5));
        assertEquals(ColorTransform.NONE, ColorTransform.NONE.withMask(0xFF0000, 0.0));
        assertTrue(ColorTransform.NONE.withMask(0xFF0000, 0.0).isIdentity());
    }

    /**
     * The tint multiplies the four channels and the alpha the opacity, as a render command does: these are the
     * values the AWT backend draws per frame over black (REQ-SPRITE-004 parity).
     */
    @Test
    void testTintAndAlpha()
    {
        assertEquals(0xFF00FF00, ColorTransform.NONE.withTint(0xFF00FF00).apply(0xFFFFFFFF));
        assertEquals(0x80FFFFFF, ColorTransform.NONE.withAlpha(0.5).apply(0xFFFFFFFF));
        assertEquals(0x80FFFFFF, ColorTransform.NONE.withTint(0x80FFFFFF).apply(0xFFFFFFFF));
        assertEquals(0x40FF0000, ColorTransform.NONE.withTint(0x80FF0000).withAlpha(0.5).apply(0xFFFFFFFF));
        assertEquals(0xFF0000FF, ColorTransform.NONE.withTint(0xFF0000FF).apply(0xFFFFFFFF));
        assertEquals(0xFF604020, ColorTransform.NONE.withTint(0xFF808080).apply(0xFFC08040));
        assertEquals(0x00C08040, ColorTransform.NONE.withAlpha(0.0).apply(0xFFC08040));
    }

    /**
     * The parts apply in order: grading, then mask, then tint and alpha.
     */
    @Test
    void testOrder()
    {
        final ColorTransform grayThenRed = ColorTransform.NONE.withGrading(ColorMatrix.saturation(0.0))
                                                              .withMask(0xFF0000, 1.0);
        final ColorTransform whiteThenTint = ColorTransform.NONE.withTint(0xFF808080).withMask(0xFFFFFF, 1.0);

        assertEquals(0xFFFF0000, grayThenRed.apply(0xFF00FF00));
        assertEquals(0xFF808080, whiteThenTint.apply(0xFF000000));
        assertEquals(0x80B6B6B6, ColorTransform.NONE.withGrading(ColorMatrix.saturation(0.0)).apply(0x8000FF00));
    }

    /**
     * Baking the whole transform gives exactly the pixels of its static part baked, then its per-frame part applied,
     * for any pixel: the description does not depend on the path (ADR-0056).
     */
    @Test
    void testPathIndependence()
    {
        final Random random = new Random(0x4C494F4EL);
        final RecordingBackend both = capability(true, true);
        for (int i = 0; i < SAMPLES; i++)
        {
            final ColorTransform transform = ColorTransform.NONE.withGrading(ColorMatrix.saturation(random.nextDouble()
                                                                                                    * 2.0))
                                                                .withMask(random.nextInt(), random.nextDouble())
                                                                .withTint(random.nextInt())
                                                                .withAlpha(random.nextDouble());
            final int pixel = random.nextInt();
            final int baked = transform.getBakedPart(both).apply(pixel);

            assertEquals(transform.apply(pixel), transform.getFramePart(both).apply(baked));
        }
    }

    /**
     * A backend applying both effects per frame gets the tint and the alpha per frame and bakes the grading and the
     * mask; a backend applying none bakes everything; the others bake what they do not apply.
     */
    @Test
    void testSplit()
    {
        final ColorTransform transform = full();
        final ColorTransform staticPart = ColorTransform.NONE.withGrading(ColorMatrix.saturation(0.5))
                                                             .withMask(0x00FF00, 0.25);
        final ColorTransform framePart = ColorTransform.NONE.withTint(0xC0FF8040).withAlpha(0.75);

        assertEquals(framePart, transform.getFramePart(capability(true, true)));
        assertEquals(staticPart, transform.getBakedPart(capability(true, true)));
        assertEquals(ColorTransform.NONE, transform.getFramePart(capability(false, false)));
        assertEquals(transform, transform.getBakedPart(capability(false, false)));
        assertEquals(ColorTransform.NONE.withTint(0xC0FF8040), transform.getFramePart(capability(true, false)));
        assertEquals(staticPart.withAlpha(0.75), transform.getBakedPart(capability(true, false)));
        assertEquals(ColorTransform.NONE.withAlpha(0.75), transform.getFramePart(capability(false, true)));
        assertEquals(staticPart.withTint(0xC0FF8040), transform.getBakedPart(capability(false, true)));
        assertTrue(framePart.getBakedPart(capability(true, true)).isIdentity());
    }

    /**
     * Equal parts make equal transforms, {@code -0.0} included; the canonical text lists the parts other than their
     * default in the order they apply.
     */
    @Test
    void testEqualityAndText()
    {
        final ColorTransform transform = ColorTransform.NONE.withMask(0xFFFFFF, 1.0).withAlpha(0.5);

        assertEquals(transform, ColorTransform.NONE.withAlpha(0.5).withMask(0xFFFFFF, 1.0));
        assertEquals(transform.hashCode(), ColorTransform.NONE.withAlpha(0.5).withMask(0xFFFFFF, 1.0).hashCode());
        assertEquals(ColorTransform.NONE.withAlpha(0.0), ColorTransform.NONE.withAlpha(-0.0));
        assertNotEquals(transform, transform.withTint(0xFF00FF00));
        assertNotEquals(transform, transform.withGrading(ColorMatrix.saturation(0.0)));
        assertEquals("mask(#FFFFFF,1.0),alpha(0.5)", transform.toString());
        assertEquals("tint(#80FF0000)", ColorTransform.NONE.withTint(0x80FF0000).toString());
        assertEquals("mask(#00FF00,0.25),tint(#C0FF8040),alpha(0.75)",
                     full().withGrading(ColorMatrix.IDENTITY).toString());
        assertTrue(full().toString().startsWith("grading("), full().toString());
        assertTrue(full().toString().endsWith(",mask(#00FF00,0.25),tint(#C0FF8040),alpha(0.75)"), full().toString());
    }

    /**
     * An amount or an alpha outside {@code [0, 1]} and {@code null} arguments are refused.
     *
     * @param value The invalid value.
     */
    @ParameterizedTest
    @ValueSource(doubles =
    {
        -0.1, 1.1, Double.NaN, Double.POSITIVE_INFINITY
    })
    void testInvalid(double value)
    {
        final ColorTransform transform = ColorTransform.NONE;

        assertThrows(IllegalArgumentException.class, () -> transform.withMask(0xFFFFFF, value));
        assertThrows(IllegalArgumentException.class, () -> transform.withAlpha(value));
        assertThrows(IllegalArgumentException.class, () -> transform.withGrading(null));
        assertThrows(IllegalArgumentException.class, () -> transform.getFramePart(null));
        assertThrows(IllegalArgumentException.class, () -> transform.getBakedPart(null));
    }
}
