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

import com.b3dgs.lionengine.render.ColorCapability;
import com.b3dgs.lionengine.render.ColorEffect;
import com.b3dgs.lionengine.render.CommandBuffer;

/**
 * Backend-independent description of a sprite color transform: grading, mask, tint and alpha (ADR-0003, ADR-0026,
 * ADR-0056, REQ-SPRITE-004).
 *
 * <p>
 * The parts apply in this order, each optional:
 * </p>
 * <ol>
 * <li>grading, a {@link ColorMatrix} over the four channels;</li>
 * <li>mask, a silhouette fill: every pixel is blended toward one color by an amount, keeping its alpha,
 * {@code rgb' = rgb + (maskRgb - rgb) × amount};</li>
 * <li>tint, multiplied with the four channels, {@code c' = c × t / 255}, as a render command applies it;</li>
 * <li>alpha, multiplied with the opacity.</li>
 * </ol>
 *
 * <p>
 * Grading and mask are the static part, baked on the CPU into a cached image with
 * {@link ImageCache#bake(Image, ColorTransform)}. Tint and alpha are the per-frame part, written into the render
 * commands by {@link Sprite#setTint(int)} and {@link Sprite#setAlpha(double)} when the backend applies them per frame,
 * and baked otherwise: {@link #getFramePart(ColorCapability)} and {@link #getBakedPart(ColorCapability)} split a
 * transform for a backend. On the CPU, the static part is rounded to 8 bits per channel before the per-frame part, so
 * baking the whole transform gives the same pixels as baking its static part then applying the per-frame part;
 * channels are computed in {@code double}, rounded half up and clamped to {@code [0, 255]}, on straight alpha.
 * </p>
 *
 * <p>
 * Immutable and thread-safe: the {@code with} methods return a new transform. Equal transforms have equal parts
 * ({@code -0.0} is stored as {@code 0.0}, and a mask of amount {@code 0} as no mask); the canonical text
 * ({@link #toString()}) names the cached image of a bake.
 * </p>
 *
 * @since 11.0.0
 */
public final class ColorTransform
{
    /** The transform that changes nothing. */
    public static final ColorTransform NONE = new ColorTransform(ColorMatrix.IDENTITY,
                                                                 0,
                                                                 0.0,
                                                                 CommandBuffer.NO_TINT,
                                                                 1.0);

    /** Mask of the color channels. */
    private static final int RGB = 0xFFFFFF;
    /** Maximum value of a channel. */
    private static final int CHANNEL_MAX = 255;
    /** Maximum value of a channel, as a divisor. */
    private static final double CHANNEL_SCALE = 255.0;
    /** Shift of the alpha channel. */
    private static final int ALPHA_SHIFT = 24;
    /** Shift of the red channel. */
    private static final int RED_SHIFT = 16;
    /** Shift of the green channel. */
    private static final int GREEN_SHIFT = 8;
    /** Digits of a color without alpha in the text. */
    private static final int RGB_DIGITS = 6;
    /** Digits of a color with alpha in the text. */
    private static final int ARGB_DIGITS = 8;
    /** Hexadecimal radix. */
    private static final int HEX = 16;
    /** Bits of a hexadecimal digit. */
    private static final int DIGIT_BITS = 4;
    /** Mask of a hexadecimal digit. */
    private static final int DIGIT_MASK = HEX - 1;
    /** Separator of the parts in the text. */
    private static final char SEPARATOR = ',';
    /** End of a part in the text. */
    private static final char END = ')';
    /** Name of the capability argument, for the messages. */
    private static final String NAME_CAPABILITY = "Capability";

    /**
     * Checks that a value is in {@code [0, 1]}.
     *
     * @param value The value.
     * @param name The name of the value, for the message.
     * @return The value, {@code -0.0} as {@code 0.0}.
     * @throws IllegalArgumentException If the value is outside {@code [0, 1]} or not a number.
     */
    private static double checkUnit(double value, String name)
    {
        if (Double.isNaN(value) || value < 0.0 || value > 1.0)
        {
            throw new IllegalArgumentException(name + " outside [0, 1]: " + value);
        }
        return value + 0.0;
    }

    /**
     * Returns a channel of a pixel.
     *
     * @param argb The pixel.
     * @param shift The shift of the channel.
     * @return The channel in {@code [0, 255]}.
     */
    private static int channel(int argb, int shift)
    {
        return (argb >>> shift) & CHANNEL_MAX;
    }

    /**
     * Returns a color in hexadecimal, upper case, with a fixed number of digits.
     *
     * @param value The color.
     * @param digits The number of digits.
     * @return The text, {@code #} then the digits.
     */
    private static String hex(int value, int digits)
    {
        final StringBuilder text = new StringBuilder(digits + 1).append('#');
        for (int i = digits - 1; i >= 0; i--)
        {
            text.append(Character.toUpperCase(Character.forDigit((value >>> (i * DIGIT_BITS)) & DIGIT_MASK, HEX)));
        }
        return text.toString();
    }

    /**
     * Appends a part to the canonical text.
     *
     * @param text The text so far, empty if none.
     * @param part The part.
     * @return The text with the part, after a comma unless the text was empty.
     */
    private static String join(String text, String part)
    {
        final String result;
        if (text.isEmpty())
        {
            result = part;
        }
        else
        {
            result = text + SEPARATOR + part;
        }
        return result;
    }

    /** The grading. */
    private final ColorMatrix grading;
    /** The mask color, {@code 0xRRGGBB}. */
    private final int maskColor;
    /** The mask amount in {@code [0, 1]}, {@code 0} for no mask. */
    private final double maskAmount;
    /** The tint, {@code 0xAARRGGBB}. */
    private final int tint;
    /** The alpha in {@code [0, 1]}. */
    private final double alpha;

    /**
     * Creates a transform from checked parts.
     *
     * @param grading The grading.
     * @param maskColor The mask color, {@code 0xRRGGBB}.
     * @param maskAmount The mask amount in {@code [0, 1]}.
     * @param tint The tint.
     * @param alpha The alpha in {@code [0, 1]}.
     */
    private ColorTransform(ColorMatrix grading, int maskColor, double maskAmount, int tint, double alpha)
    {
        super();

        this.grading = grading;
        if (maskAmount > 0.0)
        {
            this.maskColor = maskColor & RGB;
        }
        else
        {
            this.maskColor = 0;
        }
        this.maskAmount = maskAmount;
        this.tint = tint;
        this.alpha = alpha;
    }

    /**
     * Returns this transform with a grading.
     *
     * @param matrix The grading, {@link ColorMatrix#IDENTITY} for none (never {@code null}).
     * @return The new transform.
     * @throws IllegalArgumentException If the matrix is {@code null}.
     */
    public ColorTransform withGrading(ColorMatrix matrix)
    {
        ImageCache.checkNotNull(matrix, "Grading");
        return new ColorTransform(matrix, maskColor, maskAmount, tint, alpha);
    }

    /**
     * Returns this transform with a mask: every pixel blended toward a color, keeping its alpha.
     *
     * @param rgb The color, packed {@code 0xRRGGBB}; the alpha bits are ignored.
     * @param amount The amount in {@code [0, 1]}: {@code 0} for no mask, {@code 1} for a full silhouette.
     * @return The new transform.
     * @throws IllegalArgumentException If the amount is outside {@code [0, 1]} or not a number.
     */
    public ColorTransform withMask(int rgb, double amount)
    {
        return new ColorTransform(grading, rgb, checkUnit(amount, "Mask amount"), tint, alpha);
    }

    /**
     * Returns this transform with a tint, multiplied with the four channels.
     *
     * @param argb The tint, packed {@code 0xAARRGGBB}; {@link CommandBuffer#NO_TINT} for none.
     * @return The new transform.
     */
    public ColorTransform withTint(int argb)
    {
        return new ColorTransform(grading, maskColor, maskAmount, argb, alpha);
    }

    /**
     * Returns this transform with an alpha, multiplied with the opacity.
     *
     * @param value The alpha in {@code [0, 1]}, {@code 1} for none.
     * @return The new transform.
     * @throws IllegalArgumentException If the alpha is outside {@code [0, 1]} or not a number.
     */
    public ColorTransform withAlpha(double value)
    {
        return new ColorTransform(grading, maskColor, maskAmount, tint, checkUnit(value, "Alpha"));
    }

    /**
     * Returns the grading.
     *
     * @return The grading, {@link ColorMatrix#IDENTITY} for none.
     */
    public ColorMatrix getGrading()
    {
        return grading;
    }

    /**
     * Returns the mask color.
     *
     * @return The color, {@code 0xRRGGBB}; {@code 0} without mask.
     */
    public int getMaskColor()
    {
        return maskColor;
    }

    /**
     * Returns the mask amount.
     *
     * @return The amount in {@code [0, 1]}, {@code 0} for no mask.
     */
    public double getMaskAmount()
    {
        return maskAmount;
    }

    /**
     * Returns the tint.
     *
     * @return The tint, {@code 0xAARRGGBB}; {@link CommandBuffer#NO_TINT} for none.
     */
    public int getTint()
    {
        return tint;
    }

    /**
     * Returns the alpha.
     *
     * @return The alpha in {@code [0, 1]}, {@code 1} for none.
     */
    public double getAlpha()
    {
        return alpha;
    }

    /**
     * Tells whether the transform changes nothing.
     *
     * @return {@code true} if every part is absent.
     */
    public boolean isIdentity()
    {
        return equals(NONE);
    }

    /**
     * Returns the part of this transform a backend applies per frame: the tint and the alpha it supports.
     *
     * @param capability The color capability of the backend (never {@code null}).
     * @return The per-frame part, to write into the sprite; no grading and no mask.
     * @throws IllegalArgumentException If the capability is {@code null}.
     */
    public ColorTransform getFramePart(ColorCapability capability)
    {
        ImageCache.checkNotNull(capability, NAME_CAPABILITY);
        return new ColorTransform(ColorMatrix.IDENTITY, 0, 0.0, frameTint(capability), frameAlpha(capability));
    }

    /**
     * Returns the part of this transform to bake on the CPU for a backend: the grading and the mask, and the tint and
     * the alpha it does not apply per frame.
     *
     * @param capability The color capability of the backend (never {@code null}).
     * @return The part to give to {@link ImageCache#bake(Image, ColorTransform)}; {@link #isIdentity()} if nothing is
     *         left to bake.
     * @throws IllegalArgumentException If the capability is {@code null}.
     */
    public ColorTransform getBakedPart(ColorCapability capability)
    {
        ImageCache.checkNotNull(capability, NAME_CAPABILITY);
        final int bakedTint;
        if (capability.isSupported(ColorEffect.TINT))
        {
            bakedTint = CommandBuffer.NO_TINT;
        }
        else
        {
            bakedTint = tint;
        }
        final double bakedAlpha;
        if (capability.isSupported(ColorEffect.ALPHA))
        {
            bakedAlpha = 1.0;
        }
        else
        {
            bakedAlpha = alpha;
        }
        return new ColorTransform(grading, maskColor, maskAmount, bakedTint, bakedAlpha);
    }

    /**
     * Applies the transform to a pixel, on the CPU: grading, mask, then tint and alpha.
     *
     * @param argb The pixel, packed {@code 0xAARRGGBB}, straight alpha.
     * @return The transformed pixel.
     */
    public int apply(int argb)
    {
        int value = argb;
        if (!grading.isIdentity())
        {
            value = grading.apply(value);
        }
        if (maskAmount > 0.0)
        {
            value = mask(value);
        }
        if (tint != CommandBuffer.NO_TINT || alpha < 1.0)
        {
            value = multiply(value);
        }
        return value;
    }

    /**
     * Returns the tint applied per frame.
     *
     * @param capability The color capability.
     * @return The tint if supported, no tint otherwise.
     */
    private int frameTint(ColorCapability capability)
    {
        final int result;
        if (capability.isSupported(ColorEffect.TINT))
        {
            result = tint;
        }
        else
        {
            result = CommandBuffer.NO_TINT;
        }
        return result;
    }

    /**
     * Returns the alpha applied per frame.
     *
     * @param capability The color capability.
     * @return The alpha if supported, {@code 1} otherwise.
     */
    private double frameAlpha(ColorCapability capability)
    {
        final double result;
        if (capability.isSupported(ColorEffect.ALPHA))
        {
            result = alpha;
        }
        else
        {
            result = 1.0;
        }
        return result;
    }

    /**
     * Blends a pixel toward the mask color, keeping its alpha.
     *
     * @param argb The pixel.
     * @return The masked pixel.
     */
    private int mask(int argb)
    {
        final int kept = argb & ~RGB;
        final int red = blend(argb, RED_SHIFT) << RED_SHIFT;
        final int green = blend(argb, GREEN_SHIFT) << GREEN_SHIFT;
        return kept | red | green | blend(argb, 0);
    }

    /**
     * Blends one channel of a pixel toward the mask color.
     *
     * @param argb The pixel.
     * @param shift The shift of the channel.
     * @return The blended channel in {@code [0, 255]}.
     */
    private int blend(int argb, int shift)
    {
        final int value = channel(argb, shift);
        return ColorMatrix.channel(value + (channel(maskColor, shift) - value) * maskAmount);
    }

    /**
     * Multiplies a pixel by the tint and the alpha.
     *
     * @param argb The pixel.
     * @return The tinted pixel.
     */
    private int multiply(int argb)
    {
        final double opacity = channel(tint, ALPHA_SHIFT) / CHANNEL_SCALE * alpha;
        return ColorMatrix.channel(channel(argb, ALPHA_SHIFT) * opacity) << ALPHA_SHIFT
               | ColorMatrix.channel(channel(argb, RED_SHIFT) * channel(tint, RED_SHIFT) / CHANNEL_SCALE)
                 << RED_SHIFT
               | ColorMatrix.channel(channel(argb, GREEN_SHIFT) * channel(tint, GREEN_SHIFT) / CHANNEL_SCALE)
                 << GREEN_SHIFT
               | ColorMatrix.channel(channel(argb, 0) * channel(tint, 0) / CHANNEL_SCALE);
    }

    @Override
    public boolean equals(Object object)
    {
        return object instanceof final ColorTransform other && sameStatic(other) && sameFrame(other);
    }

    /**
     * Tells whether another transform has the same static part.
     *
     * @param other The other transform.
     * @return {@code true} if the grading and the mask are equal.
     */
    private boolean sameStatic(ColorTransform other)
    {
        return grading.equals(other.grading)
               && maskColor == other.maskColor
               && Double.compare(maskAmount, other.maskAmount) == 0;
    }

    /**
     * Tells whether another transform has the same per-frame part.
     *
     * @param other The other transform.
     * @return {@code true} if the tint and the alpha are equal.
     */
    private boolean sameFrame(ColorTransform other)
    {
        return tint == other.tint && Double.compare(alpha, other.alpha) == 0;
    }

    @Override
    public int hashCode()
    {
        int hash = grading.hashCode();
        hash = hash * 31 + maskColor;
        hash = hash * 31 + Double.hashCode(maskAmount);
        hash = hash * 31 + tint;
        return hash * 31 + Double.hashCode(alpha);
    }

    /**
     * Returns the canonical text of the transform: its parts other than their default, in the order they apply,
     * separated by commas, for example {@code mask(#FFFFFF,1.0),alpha(0.5)}; {@code none} for the identity. It names
     * the cached image of a bake.
     *
     * @return The canonical text.
     */
    @Override
    public String toString()
    {
        String text = "";
        if (!grading.isIdentity())
        {
            text = grading.toString();
        }
        if (maskAmount > 0.0)
        {
            text = join(text, "mask(" + hex(maskColor, RGB_DIGITS) + SEPARATOR + maskAmount + END);
        }
        if (tint != CommandBuffer.NO_TINT)
        {
            text = join(text, "tint(" + hex(tint, ARGB_DIGITS) + END);
        }
        if (alpha < 1.0)
        {
            text = join(text, "alpha(" + alpha + END);
        }
        if (text.isEmpty())
        {
            text = "none";
        }
        return text;
    }
}
