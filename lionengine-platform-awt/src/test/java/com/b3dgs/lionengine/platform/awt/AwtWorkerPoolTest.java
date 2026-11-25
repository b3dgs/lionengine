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
package com.b3dgs.lionengine.platform.awt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * Test of {@link AwtWorkerPool}, headless.
 */
final class AwtWorkerPoolTest
{
    /** Longest wait for the tasks, in seconds. */
    private static final long TIMEOUT = 10L;

    /**
     * Tasks run on daemon workers named after the engine; closing rejects later submissions.
     *
     * @throws InterruptedException If interrupted while waiting for the tasks.
     */
    @Test
    void testSubmitAndClose() throws InterruptedException
    {
        final AwtWorkerPool pool = new AwtWorkerPool(2);
        final List<String> names = new CopyOnWriteArrayList<>();
        final CountDownLatch done = new CountDownLatch(3);

        for (int i = 0; i < 3; i++)
        {
            pool.submit(() ->
            {
                final Thread thread = Thread.currentThread();
                if (thread.isDaemon())
                {
                    names.add(thread.getName());
                }
                done.countDown();
            });
        }

        assertTrue(done.await(TIMEOUT, TimeUnit.SECONDS));
        assertEquals(2, pool.getWorkerCount());
        assertEquals(3, names.size());
        for (final String name : names)
        {
            assertTrue(name.startsWith("lionengine-worker-"), name);
        }
        assertThrows(IllegalArgumentException.class, () -> pool.submit(null));

        pool.close();

        assertThrows(IllegalStateException.class, () -> pool.submit(() -> names.add("closed")));
    }
}
