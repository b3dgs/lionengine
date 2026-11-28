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
package com.b3dgs.lionengine.traits;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.ecs.CountingType;
import com.b3dgs.lionengine.ecs.StepHarness;
import com.b3dgs.lionengine.ecs.StepHarness.StepStatistics;
import com.sun.management.ThreadMXBean;

/**
 * The wall-clock benchmark of REQ-ECS-009 (ADR-0030, decision 3; ADR-0034; ADR-0079): the median and percentiles of
 * the step of 1,000 objects with the 4 standard traits and their five systems, and of 10,000 objects for the scaling,
 * with the bytes per standard trait instance and the bytes allocated to build the world (REQ-PERF-001, ADR-0081),
 * and the collision step of {@link CollisionBenchmarkWorld} at both populations, reported only (ADR-0082).
 *
 * <p>
 * Tagged {@value #TAG}: the blocking build leaves it out; the {@code benchmark} profile runs it, in the non-blocking
 * trend job of the CI. The results are written to the build output and to {@value #REPORT}; the 1 ms budget is
 * asserted only when the system property {@value #BUDGET_PROPERTY} is {@code true}, which the {@code benchmark}
 * profile sets, so a failure there marks the trend job only.
 * </p>
 */
@Tag(StepBenchmarkTest.TAG)
final class StepBenchmarkTest
{
    /** The JUnit tag of the benchmarks. */
    static final String TAG = "benchmark";
    /** The system property enabling the budget assertion. */
    static final String BUDGET_PROPERTY = "lionengine.benchmark.budget";
    /** The report file, relative to the module directory. */
    static final String REPORT = "target/benchmark-reports/step.md";
    /** The budget of a median step at 1,000 objects, in nanoseconds (1 ms, REQ-ECS-009). */
    private static final double BUDGET = 1_000_000.0;
    /** The benchmark population. */
    private static final int OBJECTS = 1000;
    /** The scaling population. */
    private static final int SCALING_OBJECTS = 10_000;
    /** Warm-up steps. */
    private static final int WARMUP = 5000;
    /** Measured steps. */
    private static final int STEPS = 10_000;
    /** Instances created to measure the bytes per instance. */
    private static final int INSTANCES = 10_000;
    /** Nanoseconds per microsecond. */
    private static final double MICROS = 1000.0;

    /**
     * Measures and reports the step at both populations and the memory, and asserts the budget when
     * {@value #BUDGET_PROPERTY} is set.
     *
     * @throws IOException If the report cannot be written.
     */
    @Test
    void testStep() throws IOException
    {
        final BenchmarkWorld scene = new BenchmarkWorld(OBJECTS);
        scene.populate();
        final long worldBytes = worldBytes();
        final StepStatistics benchmark = StepHarness.measure(scene.getWorld(), BenchmarkWorld.STEP, WARMUP, STEPS);
        final BenchmarkWorld large = new BenchmarkWorld(SCALING_OBJECTS);
        large.populate();
        final StepStatistics scaling = StepHarness.measure(large.getWorld(), BenchmarkWorld.STEP, WARMUP, STEPS);
        final CollisionBenchmarkWorld colliding = new CollisionBenchmarkWorld(OBJECTS);
        colliding.populate();
        final StepStatistics collision = StepHarness.measure(colliding.getWorld(),
                                                             CollisionBenchmarkWorld.STEP,
                                                             WARMUP,
                                                             STEPS);
        final CollisionBenchmarkWorld largeColliding = new CollisionBenchmarkWorld(SCALING_OBJECTS);
        largeColliding.populate();
        final StepStatistics collisionScaling = StepHarness.measure(largeColliding.getWorld(),
                                                                    CollisionBenchmarkWorld.STEP,
                                                                    WARMUP,
                                                                    STEPS);

        final StringBuilder report = new StringBuilder(1024);
        report.append("## ECS step benchmark (REQ-ECS-009)\n\n")
              .append("JVM ")
              .append(Runtime.version())
              .append(", ")
              .append(System.getProperty("os.name"))
              .append(' ')
              .append(System.getProperty("os.arch"))
              .append(", ")
              .append(Runtime.getRuntime().availableProcessors())
              .append(" processors; ")
              .append(WARMUP)
              .append(" warm-up steps, ")
              .append(STEPS)
              .append(" measured steps; standard traits with the capture, Gravity, Movement and Lifetime systems")
              .append(" (ADR-0079, ADR-0081).\n\n")
              .append("| Objects × traits | Median (µs) | p90 (µs) | p99 (µs) | Max (µs) | Mean (µs) |\n")
              .append("|---|---|---|---|---|---|\n");
        row(report, OBJECTS, benchmark);
        row(report, SCALING_OBJECTS, scaling);
        report.append("\nMedian ratio 10,000 / 1,000: ")
              .append(format(scaling.median() / benchmark.median()))
              .append(". Budget: median ≤ 1,000 µs at 1,000 objects.\n\n")
              .append("| Memory | Bytes |\n|---|---|\n");
        final CountingType<?>[] types = scene.getTypes();
        final String[] names =
        {
            "Transformable", "Movable", "Gravity", "Lifetime"
        };
        for (int i = 0; i < types.length; i++)
        {
            report.append("| ")
                  .append(names[i])
                  .append(", per instance | ")
                  .append(format(bytesPerInstance(types[i])))
                  .append(" |\n");
        }
        report.append("| World of 1,000 objects × 4 traits, allocated to build and populate it | ")
              .append(worldBytes)
              .append(" |\n")
              .append("| Collidable, per instance | ")
              .append(format(bytesPerInstance(new CountingType<>(Collidable.TYPE))))
              .append(" |\n\n## Collision step (REQ-STD-005, ADR-0082)\n\n")
              .append("Moving squares of 0.5 m bouncing in an area of 4 m² per object, with the capture, Movement, a")
              .append(" bounce and Collision systems (default cell of 2 m); reported, not asserted.\n\n")
              .append("| Objects | Median (µs) | p90 (µs) | p99 (µs) | Max (µs) | Mean (µs) | Pairs |\n")
              .append("|---|---|---|---|---|---|---|\n");
        collisionRow(report, OBJECTS, collision, colliding.getCollision().getPairCount());
        collisionRow(report, SCALING_OBJECTS, collisionScaling, largeColliding.getCollision().getPairCount());
        report.append("\nMedian ratio 10,000 / 1,000: ")
              .append(format(collisionScaling.median() / collision.median()))
              .append(".\n");
        final String text = report.toString();
        final Path path = Path.of(REPORT);
        Files.createDirectories(path.getParent());
        Files.writeString(path, text, StandardCharsets.UTF_8);
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);
        log.log(LogLevel.INFO, text);

        assertTrue(Double.isFinite(scene.getChecksum() + large.getChecksum()));
        if (Boolean.getBoolean(BUDGET_PROPERTY))
        {
            assertTrue(benchmark.median() <= BUDGET, () -> "Median step: " + benchmark.median() + " ns");
        }
    }

    /**
     * Appends a result row.
     *
     * @param report The report.
     * @param objects The population.
     * @param statistics The statistics.
     */
    private static void row(StringBuilder report, int objects, StepStatistics statistics)
    {
        report.append("| ")
              .append(String.format(Locale.ROOT, "%,d", objects))
              .append(" × ")
              .append(BenchmarkWorld.TRAITS)
              .append(" | ")
              .append(format(statistics.median() / MICROS))
              .append(" | ")
              .append(format(statistics.p90() / MICROS))
              .append(" | ")
              .append(format(statistics.p99() / MICROS))
              .append(" | ")
              .append(format(statistics.max() / MICROS))
              .append(" | ")
              .append(format(statistics.mean() / MICROS))
              .append(" |\n");
    }

    /**
     * Appends a result row of the collision scene.
     *
     * @param report The report.
     * @param objects The population.
     * @param statistics The statistics.
     * @param pairs The overlapping pairs at the last step.
     */
    private static void collisionRow(StringBuilder report, int objects, StepStatistics statistics, int pairs)
    {
        report.append("| ")
              .append(String.format(Locale.ROOT, "%,d", objects))
              .append(" | ")
              .append(format(statistics.median() / MICROS))
              .append(" | ")
              .append(format(statistics.p90() / MICROS))
              .append(" | ")
              .append(format(statistics.p99() / MICROS))
              .append(" | ")
              .append(format(statistics.max() / MICROS))
              .append(" | ")
              .append(format(statistics.mean() / MICROS))
              .append(" | ")
              .append(pairs)
              .append(" |\n");
    }

    /**
     * Formats a value with one decimal.
     *
     * @param value The value.
     * @return The text.
     */
    private static String format(double value)
    {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    /**
     * Measures the bytes allocated to build a second benchmark world and populate it, once the classes are loaded:
     * tables, storages, pools, queries, systems and the growth from the default capacities.
     *
     * @return The bytes allocated on this thread.
     */
    private static long worldBytes()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final long before = bean.getCurrentThreadAllocatedBytes();
        final BenchmarkWorld scene = new BenchmarkWorld(OBJECTS);
        scene.populate();
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;
        assertEquals(OBJECTS, scene.getWorld().getObjectCount());
        return allocated;
    }

    /**
     * Measures the bytes per instance of a trait type: the bytes allocated by creating instances, divided by their
     * number (the object header and the fields, as laid out by this JVM).
     *
     * @param type The type.
     * @return The bytes per instance.
     */
    private static double bytesPerInstance(CountingType<?> type)
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final Object[] instances = new Object[INSTANCES];
        final long before = bean.getCurrentThreadAllocatedBytes();
        for (int i = 0; i < INSTANCES; i++)
        {
            instances[i] = type.getType().create();
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;
        assertNotNull(instances[INSTANCES - 1]);
        return allocated / (double) INSTANCES;
    }
}
