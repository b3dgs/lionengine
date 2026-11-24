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
package com.b3dgs.lionengine.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Test {@link ResourceException} (REQ-RES-006, ADR-0007).
 */
final class ResourceExceptionTest
{
    /**
     * A missing resource carries its key and reason, without cause.
     */
    @Test
    void testMissing()
    {
        final ResourceKey key = new ResourceKey("maps/level1.json");
        final ResourceException error = new ResourceException(key, ResourceException.Reason.MISSING, null);

        assertEquals(key, error.getKey());
        assertEquals(ResourceException.Reason.MISSING, error.getReason());
        assertNull(error.getCause());
        assertEquals("MISSING: maps/level1.json", error.getMessage());
    }

    /**
     * An inaccessible resource keeps its cause.
     */
    @Test
    void testInaccessible()
    {
        final IOException cause = new IOException("denied");
        final ResourceException error = new ResourceException(new ResourceKey("save.dat"),
                                                              ResourceException.Reason.INACCESSIBLE,
                                                              cause);

        assertEquals(ResourceException.Reason.INACCESSIBLE, error.getReason());
        assertSame(cause, error.getCause());
    }
}
