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
package com.b3dgs.lionengine.examples.audio;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioBackend;
import com.b3dgs.lionengine.audio.ShortSound;
import com.b3dgs.lionengine.audio.ShortSoundCache;
import com.b3dgs.lionengine.audio.VariantSet;
import com.b3dgs.lionengine.resources.ResourceKey;

/**
 * Plays footsteps chosen at random among variants, never the same one twice in a row, and an explosion made of two
 * layers started together (REQ-AUDIO-005, ADR-0067).
 *
 * <p>
 * A scene loads the sounds of its sets on entry and releases them on exit: a {@link VariantSet} refers to the
 * {@link ShortSound} handles it is given and releases none. {@link VariantSet#random(long, ShortSound[], double[])}
 * builds a random set with its own seed, separate from the world's generator, so that the choice of a footstep never
 * changes the simulation; the same seed gives the same footsteps on every platform. Here the soft step is twice as
 * likely as each of the two others, and a weight of {@code 0} would exclude a variant.
 * {@link VariantSet#composed(ShortSound...)} builds an explosion of a blast and a debris layer, which
 * {@link Audio#play(VariantSet, int, int[])} starts in the same call and writes to an array of voices, so that the
 * play allocates nothing.
 * </p>
 *
 * @since 11.0.0
 */
public final class VariantSetExample
{
    /** Keys of the footstep variants, in the game data. */
    public static final List<ResourceKey> STEPS = List.of(new ResourceKey("sounds/step-soft.wav"),
                                                          new ResourceKey("sounds/step-hard.wav"),
                                                          new ResourceKey("sounds/step-gravel.wav"));
    /** Weights of the footstep variants: the soft step twice as likely as each other one. */
    private static final double[] STEP_WEIGHTS =
    {
        2.0, 1.0, 1.0
    };
    /** Key of the blast layer of the explosion. */
    public static final ResourceKey BLAST = new ResourceKey("sounds/blast.wav");
    /** Key of the debris layer of the explosion. */
    public static final ResourceKey DEBRIS = new ResourceKey("sounds/debris.wav");
    /** Seed of the footsteps, separate from the world's seed. */
    public static final long STEP_SEED = 42L;
    /** Number of footsteps played. */
    public static final int FOOTSTEPS = 8;
    /** Priority of the footsteps. */
    public static final int STEP_PRIORITY = 20;
    /** Priority of the explosion. */
    public static final int EXPLOSION_PRIORITY = 90;

    /**
     * Private constructor.
     */
    private VariantSetExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Loads the sounds, plays {@link #FOOTSTEPS} footsteps and the explosion, then releases the sounds.
     *
     * @param cache The short-sound cache over the game data holding {@link #STEPS}, {@link #BLAST} and
     *            {@link #DEBRIS}.
     * @param audio The audio system, with at least {@link #FOOTSTEPS} plus two voices for every sound to play.
     * @return The footsteps chosen and the voices of the explosion.
     */
    public static Result run(ShortSoundCache cache, Audio audio)
    {
        final ShortSound[] steps = new ShortSound[STEPS.size()];
        for (int i = 0; i < steps.length; i++)
        {
            steps[i] = cache.load(STEPS.get(i));
        }
        final ShortSound blast = cache.load(BLAST);
        final ShortSound debris = cache.load(DEBRIS);
        final VariantSet footsteps = VariantSet.random(STEP_SEED, steps, STEP_WEIGHTS);
        final VariantSet explosion = VariantSet.composed(blast, debris);

        final List<Integer> chosen = new ArrayList<>(FOOTSTEPS);
        for (int i = 0; i < FOOTSTEPS; i++)
        {
            audio.play(footsteps, STEP_PRIORITY);
            chosen.add(Integer.valueOf(footsteps.getLastIndex()));
        }
        final int[] layers = new int[explosion.getVoicesPerPlay()];
        final int started = audio.play(explosion, EXPLOSION_PRIORITY, layers);

        for (final ShortSound step : steps)
        {
            step.release();
        }
        blast.release();
        debris.release();
        return new Result(List.copyOf(chosen), layers[0], layers[1], started);
    }

    /**
     * Result of the example.
     *
     * @param footsteps The index in {@link #STEPS} of each footstep played, never twice the same in a row.
     * @param blast The voice of the blast layer, positive, or {@link AudioBackend#NO_VOICE}.
     * @param debris The voice of the debris layer, positive, or {@link AudioBackend#NO_VOICE}.
     * @param started The number of layers started.
     * @since 11.0.0
     */
    public record Result(List<Integer> footsteps, int blast, int debris, int started)
    {
        // Value type of the example.
    }
}
