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
package com.b3dgs.lionengine.audio;

import com.b3dgs.lionengine.RandomSource;

/**
 * Group of short sounds played as one effect: a random set plays one variant chosen at random, a composed set plays
 * every layer at once (ADR-0004, ADR-0027, ADR-0033, ADR-0067, REQ-AUDIO-005).
 *
 * <p>
 * Random set, from {@link #random(long, ShortSound...)} or {@link #random(long, ShortSound[], double[])}: each
 * {@link Audio#play(VariantSet, int)} chooses one variant with a probability proportional to its weight, a
 * non-negative {@code double}, {@link #DEFAULT_WEIGHT} by default; a weight of {@code 0} excludes the variant. When
 * at least two variants have a positive weight, the variant of the last play is never chosen again at once; with a
 * single one, it is chosen every time. The choice is made by the set, before and whatever the backend, and counts as
 * the last play even when no voice plays it.
 * </p>
 *
 * <p>
 * Randomness: each random set owns a {@link RandomSource} created from its seed, separate from the world's generator,
 * so that cosmetic audio choices never change the deterministic simulation (REQ-DET-001). The same seed and the same
 * plays give the same variants on every platform. A choice draws one value of the generator, none when a single
 * variant can be chosen.
 * </p>
 *
 * <p>
 * Composed set, from {@link #composed(ShortSound...)}: {@link Audio#play(VariantSet, int, int[])} starts every layer
 * in the same call, so that they start together; offsets between layers are not supported.
 * </p>
 *
 * <p>
 * Sounds: the set refers to the {@link ShortSound} handles it is given, without adding a reference nor releasing
 * them. The code that loaded them keeps them loaded while the set is played and releases them, normally the scene on
 * exit; playing a set of which a sound is released throws an {@link IllegalStateException}. A handle may appear more
 * than once; the no-repeat rule applies to the positions in the set.
 * </p>
 *
 * <p>
 * Threads: runtime thread only (ADR-0018), not thread-safe. Choosing a variant allocates nothing.
 * </p>
 *
 * @since 11.0.0
 */
public final class VariantSet
{
    /** Default weight of a variant. */
    public static final double DEFAULT_WEIGHT = 1.0;
    /** Index of no variant: no play yet, or a composed set. */
    public static final int NO_INDEX = -1;

    /** Name of a variant, for the messages. */
    private static final String NAME_VARIANT = "Variant";
    /** Separator of the messages. */
    private static final String COLON = ": ";

    /**
     * Creates a random set where every variant has the {@link #DEFAULT_WEIGHT}.
     *
     * @param seed The seed of the generator of the set; any value.
     * @param variants The variants, at least one, not released, copied (never {@code null}, no {@code null}).
     * @return The random set.
     * @throws IllegalArgumentException If {@code variants} is {@code null}, empty, or holds {@code null}.
     */
    public static VariantSet random(long seed, ShortSound... variants)
    {
        checkSounds(variants, NAME_VARIANT);
        final double[] weights = new double[variants.length];
        for (int i = 0; i < weights.length; i++)
        {
            weights[i] = DEFAULT_WEIGHT;
        }
        return new VariantSet(new RandomSource(seed), variants.clone(), weights);
    }

    /**
     * Creates a random set with a weight per variant.
     *
     * @param seed The seed of the generator of the set; any value.
     * @param variants The variants, at least one, not released, copied (never {@code null}, no {@code null}).
     * @param weights The weight of each variant, in the same order, copied: finite and not negative, {@code 0}
     *            excluding the variant, at least one positive (never {@code null}).
     * @return The random set.
     * @throws IllegalArgumentException If an array is {@code null}, {@code variants} is empty or holds {@code null},
     *             the arrays differ in length, a weight is negative, not finite, or none is positive, or the sum of
     *             the weights is not finite.
     */
    public static VariantSet random(long seed, ShortSound[] variants, double[] weights)
    {
        checkSounds(variants, NAME_VARIANT);
        ShortSoundCache.checkNotNull(weights, "Weights");
        if (weights.length != variants.length)
        {
            throw new IllegalArgumentException("Weights and variants differ in length: "
                                               + weights.length
                                               + " weights, "
                                               + variants.length
                                               + " variants");
        }
        double total = 0.0;
        for (int i = 0; i < weights.length; i++)
        {
            final double weight = weights[i];
            if (!Double.isFinite(weight) || weight < 0.0)
            {
                throw new IllegalArgumentException("Weight negative or not finite at " + i + COLON + weight);
            }
            total += weight;
        }
        if (total <= 0.0 || !Double.isFinite(total))
        {
            throw new IllegalArgumentException("Weights without a positive one or of infinite sum" + COLON + total);
        }
        return new VariantSet(new RandomSource(seed), variants.clone(), weights.clone());
    }

    /**
     * Creates a composed set: every layer starts at once at each play.
     *
     * @param layers The layers, at least one, not released, copied (never {@code null}, no {@code null}).
     * @return The composed set.
     * @throws IllegalArgumentException If {@code layers} is {@code null}, empty, or holds {@code null}.
     */
    public static VariantSet composed(ShortSound... layers)
    {
        checkSounds(layers, "Layer");
        return new VariantSet(null, layers.clone(), null);
    }

    /**
     * Checks an array of sounds.
     *
     * @param sounds The sounds.
     * @param name The name of an element, for the message.
     * @throws IllegalArgumentException If {@code sounds} is {@code null}, empty, or holds {@code null}.
     */
    private static void checkSounds(ShortSound[] sounds, String name)
    {
        ShortSoundCache.checkNotNull(sounds, name + "s");
        if (sounds.length == 0)
        {
            throw new IllegalArgumentException(name + "s must not be empty");
        }
        for (int i = 0; i < sounds.length; i++)
        {
            ShortSoundCache.checkNotNull(sounds[i], name + " " + i);
        }
    }

    /** The generator of a random set, {@code null} for a composed set. */
    private final RandomSource random;
    /** The sounds: the variants or the layers. */
    private final ShortSound[] sounds;
    /** The weight of each variant, {@code null} for a composed set. */
    private final double[] weights;
    /** The sum of the weights, {@code 0} for a composed set. */
    private final double total;
    /** The number of variants of positive weight, {@code 0} for a composed set. */
    private final int candidates;
    /** The variant of the last play, {@link #NO_INDEX} before it. */
    private int last = NO_INDEX;

    /**
     * Creates a set from checked copies.
     *
     * @param random The generator, {@code null} for a composed set.
     * @param sounds The sounds.
     * @param weights The weights, {@code null} for a composed set.
     */
    private VariantSet(RandomSource random, ShortSound[] sounds, double[] weights)
    {
        super();

        this.random = random;
        this.sounds = sounds;
        this.weights = weights;
        double sum = 0.0;
        int positive = 0;
        if (weights != null)
        {
            for (final double weight : weights)
            {
                sum += weight;
                if (weight > 0.0)
                {
                    positive++;
                }
            }
        }
        total = sum;
        candidates = positive;
    }

    /**
     * Tells whether the set is composed.
     *
     * @return {@code true} for a composed set, {@code false} for a random set.
     */
    public boolean isComposed()
    {
        return random == null;
    }

    /**
     * Returns the number of sounds.
     *
     * @return The variants or the layers, at least one.
     */
    public int size()
    {
        return sounds.length;
    }

    /**
     * Returns a sound.
     *
     * @param index The index of the variant or layer, from {@code 0} to {@link #size()} excluded.
     * @return The sound.
     * @throws IndexOutOfBoundsException If {@code index} is outside its range.
     */
    public ShortSound getSound(int index)
    {
        return sounds[index];
    }

    /**
     * Returns the weight of a variant.
     *
     * @param index The index of the variant, from {@code 0} to {@link #size()} excluded.
     * @return The weight, {@code 0} for an excluded variant; {@link #DEFAULT_WEIGHT} for a layer.
     * @throws IndexOutOfBoundsException If {@code index} is outside its range.
     */
    public double getWeight(int index)
    {
        if (weights == null)
        {
            if (index < 0 || index >= sounds.length)
            {
                throw new IndexOutOfBoundsException("Layer index outside 0 to " + sounds.length + COLON + index);
            }
            return DEFAULT_WEIGHT;
        }
        return weights[index];
    }

    /**
     * Returns the number of voices one play asks for: the length of the voices array of
     * {@link Audio#play(VariantSet, int, int[])}.
     *
     * @return {@code 1} for a random set, {@link #size()} for a composed set.
     */
    public int getVoicesPerPlay()
    {
        return random == null ? sounds.length : 1;
    }

    /**
     * Returns the variant chosen by the last play.
     *
     * @return Its index, {@link #NO_INDEX} before the first play and for a composed set.
     */
    public int getLastIndex()
    {
        return last;
    }

    /**
     * Chooses the variant of a play of a random set and records it as the last one.
     *
     * @return The index of the variant, of positive weight, never the last one when at least two have a positive
     *         weight.
     */
    int select()
    {
        final int chosen;
        if (candidates == 1)
        {
            chosen = firstPositive();
        }
        else
        {
            chosen = draw();
        }
        last = chosen;
        return chosen;
    }

    /**
     * Returns the only variant of positive weight.
     *
     * @return Its index.
     */
    private int firstPositive()
    {
        int index = 0;
        while (weights[index] <= 0.0)
        {
            index++;
        }
        return index;
    }

    /**
     * Draws a variant of positive weight other than the last one, with a probability proportional to its weight.
     *
     * @return Its index.
     */
    private int draw()
    {
        final double available;
        if (last == NO_INDEX)
        {
            available = total;
        }
        else
        {
            available = total - weights[last];
        }
        double remaining = random.nextDouble() * available;
        int chosen = NO_INDEX;
        for (int i = 0; i < weights.length && (chosen == NO_INDEX || remaining >= 0.0); i++)
        {
            if (i != last && weights[i] > 0.0)
            {
                // The last eligible variant stays chosen if rounding leaves a remainder.
                chosen = i;
                remaining -= weights[i];
            }
        }
        return chosen;
    }
}
