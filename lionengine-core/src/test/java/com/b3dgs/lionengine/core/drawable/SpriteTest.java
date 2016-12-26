/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.drawable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Sprite;
import com.b3dgs.lionengine.graphic.Transparency;

/**
 * Test the sprite class.
 */
public class SpriteTest
{
    /** Image media. */
    private static Media media;
    /** Graphic test output. */
    private static Graphic g;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(SpriteTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());

        media = Medias.create("image.png");
        g = Graphics.createImageBuffer(100, 100, Transparency.OPAQUE).createGraphic();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        g.dispose();

        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test function around the sprite.
     */
    @Test
    public void testSprite()
    {
        // Sprite with existing surface
        final ImageBuffer surface = Graphics.createImageBuffer(16, 16, Transparency.OPAQUE);
        final Sprite spriteA = Drawable.loadSprite(surface);

        spriteA.setOrigin(Origin.TOP_LEFT);
        spriteA.setLocation(1.0, 2.0);
        spriteA.setLocation(new ViewerMock(), spriteA);
        spriteA.setMirror(Mirror.VERTICAL);
        Assert.assertEquals(1.0, spriteA.getX(), 0.001);
        Assert.assertEquals(2.0, spriteA.getY(), 0.001);
        Assert.assertEquals(Mirror.VERTICAL, spriteA.getMirror());

        Assert.assertNotNull(spriteA.getSurface());
        Assert.assertEquals(surface, spriteA.getSurface());

        DrawableTestTool.testSpriteModification(2, spriteA);
        // Sprite clone (share surface only)
        Assert.assertEquals(spriteA, Drawable.loadSprite(spriteA.getSurface()));

        // Load from file
        final Sprite spriteB = Drawable.loadSprite(media);
        DrawableTestTool.assertImageInfoCorrect(media, spriteB);

        DrawableTestTool.testSpriteLoading(spriteB);
        DrawableTestTool.testImageRender(g, spriteB);
        Assert.assertFalse(spriteB.equals(Drawable.loadSprite(media)));

        // Hash code
        Assert.assertTrue(spriteA.hashCode() != spriteB.hashCode());

        spriteA.dispose();
    }

    /**
     * Test function around the sprite.
     */
    @Test
    public void testSpriteFailure()
    {
        try
        {
            Drawable.loadSprite(Medias.create("void"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }
    }
}
