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
package com.b3dgs.lionengine.ecs;

import java.util.Arrays;

/**
 * The headless step harness of the benchmark (ADR-0030, decision 3; ADR-0079): no benchmark library, a warm-up, then
 * the duration of each measured step on the monotonic clock, summarized by its median and percentiles.
 *
 * <p>
 * Its results are reported, never asserted by the blocking build: wall-clock time depends on the machine and its load.
 * </p>
 */
final class StepHarness
{
    /** Percent. */
    private static final int PERCENT = 100;
    /** 90th percentile. */
    private static final int P90 = 90;
    /** 99th percentile. */
    private static final int P99 = 99;

    /**
     * Measures the steps of a populated scene.
     *
     * @param scene The scene, already populated.
     * @param warmup The steps run first and not measured, so that the code is compiled and the capacities fit.
     * @param steps The measured steps, at least one.
     * @return The statistics of the measured steps.
     */
    static StepStatistics measure(BenchmarkWorld scene, int warmup, int steps)
    {
        scene.run(warmup);
        final long[] durations = new long[steps];
        for (int i = 0; i < steps; i++)
        {
            final long start = System.nanoTime();
            scene.step();
            durations[i] = System.nanoTime() - start;
        }
        return summarize(durations);
    }

    /**
     * Summarizes step durations: the median (the mean of the two middle values for an even count), the 90th and
     * 99th percentiles (nearest rank), the maximum and the mean.
     *
     * @param durations The durations, in nanoseconds, at least one; sorted in place.
     * @return The statistics.
     */
    static StepStatistics summarize(long[] durations)
    {
        Arrays.sort(durations);
        final int count = durations.length;
        final double median;
        if (count % 2 == 0)
        {
            median = (durations[count / 2 - 1] + durations[count / 2]) / 2.0;
        }
        else
        {
            median = durations[count / 2];
        }
        double sum = 0.0;
        for (final long duration : durations)
        {
            sum += duration;
        }
        return new StepStatistics(count,
                                  median,
                                  percentile(durations, P90),
                                  percentile(durations, P99),
                                  durations[count - 1],
                                  sum / count);
    }

    /**
     * Returns a percentile by nearest rank.
     *
     * @param sorted The sorted durations.
     * @param percent The percentile, from 1 to 100.
     * @return The smallest duration such that at least {@code percent} percent of the durations are at or under it.
     */
    private static long percentile(long[] sorted, int percent)
    {
        final int rank = (int) Math.ceil(percent * (double) sorted.length / PERCENT);
        return sorted[Math.max(rank, 1) - 1];
    }

    /**
     * Not instantiable.
     */
    private StepHarness()
    {
        throw new AssertionError();
    }

    /**
     * The statistics of measured steps, in nanoseconds.
     *
     * @param steps The measured steps.
     * @param median The median duration.
     * @param p90 The 90th percentile.
     * @param p99 The 99th percentile.
     * @param max The longest step.
     * @param mean The mean duration.
     */
    record StepStatistics(int steps, double median, long p90, long p99, long max, double mean)
    {
    }
}
