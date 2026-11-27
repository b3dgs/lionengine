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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.zip.DataFormatException;

import org.junit.jupiter.api.Test;

/**
 * Test {@link ImageDecodingException} (REQ-SPRITE-009, ADR-0007).
 */
final class ImageDecodingExceptionTest
{
    /**
     * The exception keeps its message, and its cause when given.
     */
    @Test
    void testMessageAndCause()
    {
        final DataFormatException cause = new DataFormatException("invalid block type");
        final ImageDecodingException withCause = new ImageDecodingException("PNG image data corrupted", cause);
        final ImageDecodingException alone = new ImageDecodingException("Not a PNG image");

        assertEquals("PNG image data corrupted", withCause.getMessage());
        assertSame(cause, withCause.getCause());
        assertEquals("Not a PNG image", alone.getMessage());
        assertNull(alone.getCause());
    }
}
