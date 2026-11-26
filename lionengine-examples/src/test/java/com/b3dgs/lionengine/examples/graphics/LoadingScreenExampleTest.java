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
package com.b3dgs.lionengine.examples.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.FakeWorkerPool;
import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.graphics.PngWriter;
import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Runs {@link LoadingScreenExample} on images written in memory, with the fake worker pool of the {@code core} test
 * JAR (REQ-DOC-001, REQ-RT-004, REQ-RT-010).
 */
final class LoadingScreenExampleTest
{
    /**
     * Writes an image of a size into memory.
     *
     * @param memory The memory provider.
     * @param key The key of the image.
     * @param width The width.
     * @param height The height.
     * @throws IOException If the image cannot be written.
     */
    private static void write(MemoryProvider memory, ResourceKey key, int width, int height) throws IOException
    {
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(PngWriter.rgba(width, height, new int[width * height]).write());
        }
    }

    /**
     * Without worker the images are decoded at the request and delivered at the start of the next iteration, before
     * the first update of the loading scene: it sees the group ready at once, the level receives both images and
     * releases them on exit.
     *
     * @throws IOException If an image cannot be written.
     */
    @Test
    void testRun() throws IOException
    {
        final MemoryProvider memory = new MemoryProvider();
        write(memory, LoadingScreenExample.HERO, 64, 32);
        write(memory, LoadingScreenExample.TILES, 31, 21);

        final LoadingScreenExample.Result result = LoadingScreenExample.run(new Resources(memory),
                                                                            new RecordingBackend(),
                                                                            new FakeWorkerPool(0),
                                                                            new Log());

        assertEquals(0, result.loadingSteps());
        assertEquals(1.0, result.firstProgress());
        assertEquals(64, result.heroWidth());
        assertEquals(31, result.tilesWidth());
        assertEquals(0, result.cachedAtEnd());
    }

    /**
     * Workers that never run leave the group pending: the loading scene waits at a progress of 0 until the example
     * gives up, then its exit cancels the loads, and nothing stays cached.
     *
     * @throws IOException If an image cannot be written.
     */
    @Test
    void testWaiting() throws IOException
    {
        final MemoryProvider memory = new MemoryProvider();
        write(memory, LoadingScreenExample.HERO, 64, 32);
        write(memory, LoadingScreenExample.TILES, 31, 21);
        final FakeWorkerPool workers = new FakeWorkerPool(1);

        final LoadingScreenExample.Result result = LoadingScreenExample.run(new Resources(memory),
                                                                            new RecordingBackend(),
                                                                            workers,
                                                                            new Log());

        assertTrue(result.loadingSteps() > 500, "Steps: " + result.loadingSteps());
        assertEquals(0.0, result.firstProgress());
        assertEquals(0, result.heroWidth());
        assertEquals(0, result.cachedAtEnd());
        assertEquals(2, workers.getPendingCount());
    }

    /**
     * A missing image fails the group, and the loading scene reports the failure, which stops the loop.
     */
    @Test
    void testFailure()
    {
        final MemoryProvider memory = new MemoryProvider();
        final Resources resources = new Resources(memory);
        final RecordingBackend backend = new RecordingBackend();
        final FakeWorkerPool workers = new FakeWorkerPool(0);
        final Log log = new Log();

        final ResourceException failure = assertThrows(ResourceException.class,
                                                       () -> LoadingScreenExample.run(resources,
                                                                                      backend,
                                                                                      workers,
                                                                                      log));
        assertTrue(failure.getMessage().contains(LoadingScreenExample.HERO.path()), failure.getMessage());
    }
}
