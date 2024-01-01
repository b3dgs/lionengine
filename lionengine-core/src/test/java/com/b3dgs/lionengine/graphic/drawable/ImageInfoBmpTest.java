/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.drawable;

import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.MediaMock;

/**
 * Test {@link ImageHeaderBmp}.
 */
final class ImageInfoBmpTest
{
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageInfoBmpTest.class);

    /**
     * Test constructor.
     */
    @Test
    void testBmp()
    {
        LOGGER.info("*********************************** EXPECTED VERBOSE ***********************************");

        assertFalse(new ImageHeaderBmp().is(new MediaMock()));
        assertThrows(IOException.class, () -> new ImageHeaderBmp().readHeader(new MediaMock().getInputStream()), null);

        LOGGER.info("****************************************************************************************");
    }
}
