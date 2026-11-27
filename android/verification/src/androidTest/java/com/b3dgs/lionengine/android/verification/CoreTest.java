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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.b3dgs.lionengine.AsyncHandle;
import com.b3dgs.lionengine.AsyncLoading;
import com.b3dgs.lionengine.AsyncState;
import com.b3dgs.lionengine.Configuration;
import com.b3dgs.lionengine.DefinitionException;
import com.b3dgs.lionengine.JsonArray;
import com.b3dgs.lionengine.JsonObject;
import com.b3dgs.lionengine.JsonReader;
import com.b3dgs.lionengine.Inbox;
import com.b3dgs.lionengine.InboxEvent;
import com.b3dgs.lionengine.JsonValue;
import com.b3dgs.lionengine.LoadGroup;
import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.RandomSource;
import com.b3dgs.lionengine.Registry;
import com.b3dgs.lionengine.ThreadConfinement;
import com.b3dgs.lionengine.WorkerPool;
import com.b3dgs.lionengine.WorkerTask;

/**
 * Runs {@code core} on Android API 21 (ADR-0044): random generator reference outputs (REQ-DET-003), JSON reader,
 * configuration, registry, log, thread confinement (ADR-0055) and asynchronous loading on a real worker thread. Expected values are the ones of the JVM tests of {@code lionengine-core}.
 */
@RunWith(AndroidJUnit4.class)
public final class CoreTest
{
    /** Source name of the documents. */
    private static final String SOURCE = "test.json";
    /** Seed of the SplitMix64 reference outputs (RandomSourceTest). */
    private static final long SPLIT_MIX_SEED = 0x1a2b3c4d5e6f7531L;
    /** First xoshiro256** reference outputs from the expanded seed {@code SPLIT_MIX_SEED + 24 * GOLDEN_GAMMA}. */
    private static final long[] XOSHIRO_OUTPUTS = {
            0x462c422df780c48eL, 0xa82f1f6031c183e6L, 0x8a113820e8d2ca8dL, 0x1ac7023a26534958L,
            0xac8e41d0101e109cL, 0x46e34bc13edd63c4L, 0x3a26776adcd665c3L, 0x9ac6c9bea8fc518cL,
    };
    /** Increment of the SplitMix64 state, the constant of the algorithm (package-private in RandomSource). */
    private static final long GOLDEN_GAMMA = 0x9E37_79B9_7F4A_7C15L;

    /**
     * Creates the test.
     */
    public CoreTest()
    {
        super();
    }

    /**
     * The random generator gives the reference outputs of the algorithm authors, as on the JVM: the seed whose first
     * four SplitMix64 outputs are the xoshiro256** reference state gives the xoshiro256** reference outputs.
     */
    @Test
    public void testRandomReferenceOutputs()
    {
        final RandomSource random = new RandomSource(SPLIT_MIX_SEED + 24 * GOLDEN_GAMMA);
        for (final long expected : XOSHIRO_OUTPUTS)
        {
            assertEquals(expected, random.nextLong());
        }
    }

    /**
     * Reseeding restarts the sequence in place at the reference outputs, after any number of draws (ADR-0078).
     */
    @Test
    public void testRandomReseed()
    {
        final RandomSource random = new RandomSource(1L);
        for (int i = 0; i < 37; i++)
        {
            random.nextLong();
        }
        random.reseed(SPLIT_MIX_SEED + 24 * GOLDEN_GAMMA);
        for (final long expected : XOSHIRO_OUTPUTS)
        {
            assertEquals(expected, random.nextLong());
        }
    }

    /**
     * Derived values are exact functions of the sequence, and a seed gives one sequence.
     */
    @Test
    public void testRandomDerivedValues()
    {
        final RandomSource random = new RandomSource(7L);
        final RandomSource reference = new RandomSource(7L);
        final RandomSource other = new RandomSource(8L);
        for (int i = 0; i < 1000; i++)
        {
            assertEquals((reference.nextLong() >>> 11) * 0x1.0p-53, random.nextDouble(), 0.0);
            assertEquals(reference.nextLong() < 0L, random.nextBoolean());
            assertEquals((int) (((reference.nextLong() >>> 32) * 6L) >>> 32), random.nextInt(6));
            assertNotEquals(random.nextLong(), other.nextLong());
            reference.nextLong();
        }
    }

    /**
     * A complete document read from UTF-8 bytes with a byte-order mark.
     */
    @Test
    public void testJsonDocument()
    {
        final String json = """
            ﻿{
              "format": "lionengine.sheet",
              "version": 1,
              "scale": 1.5,
              "visible": true,
              "hidden": false,
              "parent": null,
              "frames": [0, 1, [], {}],
              "empty": {}
            }
            """;
        final JsonObject root = new JsonReader().read(json.getBytes(StandardCharsets.UTF_8), SOURCE).asObject();

        assertEquals(8, root.size());
        assertEquals("format", root.getKey(0));
        assertEquals("empty", root.getKey(7));
        assertEquals("lionengine.sheet", root.get("format").asString());
        assertEquals(1, root.get("version").asInt());
        assertEquals(1.5, root.getValue(2).asDouble(), 0.0);
        assertTrue(root.get("visible").asBoolean());
        assertFalse(root.get("hidden").asBoolean());
        assertTrue(root.get("parent").isNull());
        assertEquals(4, root.get("frames").asArray().size());
        assertEquals(0, root.get("empty").asObject().size());
        assertEquals(3, root.get("version").getLine());
        assertEquals(14, root.get("version").getColumn());
    }

    /**
     * Numbers keep their kind and exact value; escapes and surrogate pairs are decoded.
     */
    @Test
    public void testJsonValues()
    {
        final JsonArray numbers = new JsonReader().read("[42, -0.25, 2E-2, 1.0e+2, 9223372036854775807, "
                                                        + "-9223372036854775808, 9223372036854775808, 1e-400]",
                                                        SOURCE)
                                                  .asArray();
        assertSame(JsonValue.Kind.LONG, numbers.get(0).getKind());
        assertEquals(-0.25, numbers.get(1).asDouble(), 0.0);
        assertEquals(0.02, numbers.get(2).asDouble(), 0.0);
        assertEquals(100.0, numbers.get(3).asDouble(), 0.0);
        assertEquals(Long.MAX_VALUE, numbers.get(4).asLong());
        assertEquals(Long.MIN_VALUE, numbers.get(5).asLong());
        assertSame(JsonValue.Kind.DOUBLE, numbers.get(6).getKind());
        assertEquals(9.223372036854775808E18, numbers.get(6).asDouble(), 0.0);
        assertEquals(0.0, numbers.get(7).asDouble(), 0.0);

        final JsonValue text = new JsonReader().read("\"\\\"\\\\\\/\\b\\f\\n\\r\\t\\u00e9\\u00C9\\uD83D\\uDE00 \"",
                                                     SOURCE);
        assertEquals("\"\\/\b\f\n\r\téÉ😀 ", text.asString());
    }

    /**
     * An error names its source, line, column and path.
     */
    @Test
    public void testJsonError()
    {
        final JsonReader reader = new JsonReader();
        final DefinitionException error = assertThrows(DefinitionException.class,
                                                       () -> reader.read("{\"a\":{\"b\":[1,{\"c\":x}]}}", SOURCE));
        assertEquals(SOURCE, error.getSource());
        assertEquals(1, error.getLine());
        assertEquals(19, error.getColumn());
        assertEquals("$.a.b[1].c", error.getPath());

        final byte[] invalid = {'[', '\n', '"', 'a', (byte) 0xC3, '"', ']'};
        final DefinitionException encoding = assertThrows(DefinitionException.class,
                                                          () -> reader.read(invalid, SOURCE));
        assertEquals(2, encoding.getLine());
        assertEquals(3, encoding.getColumn());
    }

    /**
     * A registry creates an object from its id and a configuration read from JSON.
     */
    @Test
    public void testRegistry()
    {
        final Registry<String> registry = new Registry<>();
        registry.register("lionengine:movable", configuration -> "speed " + configuration.getDouble("maxSpeed"));
        final Configuration parameters = new Configuration(new JsonReader().read("{\"maxSpeed\": 6}", SOURCE)
                                                                           .asObject());

        assertEquals("speed 6.0", registry.create("lionengine:movable", parameters));
        assertThrows(DefinitionException.class, () -> registry.create("lionengine:jumpable", parameters));
    }

    /**
     * The log writes the enabled levels to its sink only.
     */
    @Test
    public void testLog()
    {
        final List<String> written = new ArrayList<>();
        final Log log = new Log((level, message, error) -> written.add(level + " " + message));
        log.setLevel(LogLevel.INFO);

        log.log(LogLevel.DEBUG, "hidden");
        log.log(LogLevel.INFO, "Count", 3L);
        log.log(LogLevel.WARNING, "Seconds", 0.5);

        assertFalse(log.isEnabled(LogLevel.DEBUG));
        assertEquals(2, written.size());
        assertTrue(written.get(0).startsWith("INFO Count"));
        assertTrue(written.get(1).startsWith("WARNING Seconds"));
    }

    /**
     * The thread of the first check owns the object; another thread is refused (ADR-0055).
     *
     * @throws InterruptedException If interrupted.
     */
    @Test
    public void testThreadConfinement() throws InterruptedException
    {
        final ThreadConfinement confinement = new ThreadConfinement("Cache");
        confinement.check();
        final AtomicReference<IllegalStateException> failure = new AtomicReference<>();
        final Thread other = new Thread(() ->
        {
            try
            {
                confinement.check();
            }
            catch (final IllegalStateException exception)
            {
                failure.set(exception);
            }
        }, "worker");

        other.start();
        other.join();

        assertTrue(failure.get().getMessage().startsWith("Cache used on the thread worker"));
        confinement.check();
    }

    /**
     * An asynchronous request runs its work on a worker thread and becomes ready at inbox delivery; a load group gives
     * the mean progress (REQ-RT-004, REQ-RT-010).
     *
     * @throws InterruptedException If interrupted.
     */
    @Test
    public void testAsyncLoading() throws InterruptedException
    {
        final List<InboxEvent> events = new ArrayList<>();
        final Inbox inbox = new Inbox()
        {
            @Override
            public boolean post(InboxEvent event)
            {
                synchronized (events)
                {
                    events.add(event);
                }
                return true;
            }

            @Override
            public void deliverAll()
            {
                final List<InboxEvent> delivered;
                synchronized (events)
                {
                    delivered = new ArrayList<>(events);
                    events.clear();
                }
                for (int i = 0; i < delivered.size(); i++)
                {
                    delivered.get(i).deliver();
                }
            }

            @Override
            public long getDroppedCount()
            {
                return 0L;
            }
        };
        final Thread[] worker = new Thread[1];
        final WorkerPool workers = new WorkerPool()
        {
            @Override
            public void submit(WorkerTask task)
            {
                worker[0] = new Thread(task::execute, "worker");
                worker[0].start();
            }

            @Override
            public int getWorkerCount()
            {
                return 1;
            }
        };
        final LoadGroup group = new LoadGroup();

        final AsyncHandle<String> handle = AsyncLoading.submit(workers,
                                                               inbox,
                                                               () -> Thread.currentThread().getName(),
                                                               name -> "decoded on " + name);
        group.add(handle);
        group.add(AsyncLoading.ready("cached"));
        worker[0].join();

        assertEquals(AsyncState.PENDING, group.getState());
        assertEquals(0.5, group.getProgress(), 0.0);
        inbox.deliverAll();
        assertEquals("decoded on worker", handle.get());
        assertEquals(AsyncState.READY, group.getState());
    }
}
