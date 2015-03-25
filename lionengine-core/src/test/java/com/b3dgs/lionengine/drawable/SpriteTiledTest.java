/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.drawable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.mock.MediaMock;

/**
 * Test the sprite tiled class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SpriteTiledTest
{
    /** Image media. */
    private static final Media MEDIA = new MediaMock("image.png");
    /** Graphic test output. */
    private static Graphic g;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        g = Graphics.createImageBuffer(100, 100, Transparency.OPAQUE).createGraphic();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test function around tiled sprite.
     */
    @Test
    public void testSpriteTiled()
    {
        final int width = 16;
        final int height = 16;
        final int tileSize = 1;
        final SpriteTiled spriteA = Drawable.loadSpriteTiled(
                Graphics.createImageBuffer(width, height, Transparency.OPAQUE), tileSize, tileSize);

        Assert.assertNotNull(spriteA.getSurface());
        Assert.assertEquals(tileSize, spriteA.getTileWidth());
        Assert.assertEquals(tileSize, spriteA.getTileHeight());
        Assert.assertEquals(width, spriteA.getTilesHorizontal());
        Assert.assertEquals(height, spriteA.getTilesVertical());

        // Load from file
        final int tileWidth = 16;
        final int tileHeight = 8;
        final SpriteTiled spriteB = Drawable.loadSpriteTiled(MEDIA, tileWidth, tileHeight);
        DrawableTestTool.assertImageInfoCorrect(MEDIA, spriteB);

        Assert.assertEquals(tileWidth, spriteB.getTileWidth());
        Assert.assertEquals(tileHeight, spriteB.getTileHeight());

        DrawableTestTool.testSpriteLoading(spriteB);

        DrawableTestTool.testSpriteModification(2, spriteB);
        DrawableTestTool.testImageRender(g, spriteB);
        spriteB.render(g);

        Assert.assertFalse(spriteA.equals(spriteB));

        // Equals
        final SpriteTiled spriteD = Drawable.loadSpriteTiled(MEDIA, tileWidth, tileHeight);
        final SpriteTiled spriteE = Drawable.loadSpriteTiled(MEDIA, tileWidth + 2, tileHeight + 1);
        spriteD.load(false);
        spriteE.load(false);
        Assert.assertFalse(spriteD.equals(spriteE));
        spriteE.stretch(110, 100);
        Assert.assertFalse(spriteD.equals(spriteE));
        spriteE.stretch(100, 110);
        Assert.assertFalse(spriteD.equals(spriteE));
        spriteE.stretch(110, 110);
        Assert.assertFalse(spriteD.equals(spriteE));
        final SpriteTiled spriteF = Drawable.loadSpriteTiled(spriteD.getSurface(), tileWidth, tileHeight);
        Assert.assertTrue(spriteD.equals(spriteF));

        // Error
        DrawableTestTool.testSpriteTiledLoadError(0, 0);
        DrawableTestTool.testSpriteTiledLoadError(0, 1);
        DrawableTestTool.testSpriteTiledLoadError(1, 0);
        Assert.assertFalse(spriteB.equals(Drawable.loadSpriteTiled(MEDIA, tileWidth, tileHeight)));
    }
}
