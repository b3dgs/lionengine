/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.awt.graphic;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.FactoryMediaDefault;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.graphic.FactoryGraphicTest;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Test {@link FactoryGraphicAwt}.
 */
public final class FactoryGraphicAwtTest extends FactoryGraphicTest
{
    /**
     * Prepare test.
     * 
     * @throws IOException If error.
     */
    @BeforeAll
    public static void setUp() throws IOException
    {
        Medias.setFactoryMedia(new FactoryMediaDefault());
        Medias.setLoadFromJar(FactoryGraphicTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicAwt());
    }

    /**
     * Clean test.
     */
    @AfterAll
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /*
     * FactoryGraphicTest
     */

    @Test
    @Override
    public void testRotate()
    {
        final ImageBuffer image = Graphics.getImageBuffer(Medias.create("image.png"));
        final ImageBuffer rotate = Graphics.rotate(image, 90);

        assertNotEquals(image, rotate);
        assertEquals(image.getWidth(), rotate.getHeight());
        assertEquals(image.getHeight(), rotate.getWidth());

        rotate.dispose();
        image.dispose();
    }
}
