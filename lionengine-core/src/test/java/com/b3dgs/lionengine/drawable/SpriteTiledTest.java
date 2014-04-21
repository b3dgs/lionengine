/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.EngineCore;
import com.b3dgs.lionengine.core.FactoryGraphicMock;
import com.b3dgs.lionengine.core.FactoryMediaMock;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.core.Verbose;

/**
 * Test the sprite tiled class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SpriteTiledTest
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
        EngineCore.start("DrawableTest", Version.create(1, 0, 0), Verbose.NONE, new FactoryGraphicMock(),
                new FactoryMediaMock());
        SpriteTiledTest.media = Media.create(Media.getPath("src", "test", "resources", "drawable", "image.png"));
        SpriteTiledTest.g = UtilityImage.createImageBuffer(100, 100, Transparency.OPAQUE).createGraphic();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        EngineCore.terminate();
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
                UtilityImage.createImageBuffer(width, height, Transparency.OPAQUE), tileSize, tileSize);

        Assert.assertNotNull(spriteA.getSurface());
        Assert.assertEquals(tileSize, spriteA.getTileWidth());
        Assert.assertEquals(tileSize, spriteA.getTileHeight());
        Assert.assertNotNull(spriteA.getTile(0));
        Assert.assertEquals(width, spriteA.getTilesHorizontal());
        Assert.assertEquals(height, spriteA.getTilesVertical());
        Assert.assertEquals(width * height, spriteA.getTilesNumber());

        // Load from file
        final int tileWidth = 16;
        final int tileHeight = 8;
        final SpriteTiled spriteB = Drawable.loadSpriteTiled(SpriteTiledTest.media, tileWidth, tileHeight);
        DrawableTestTool.assertImageInfoCorrect(SpriteTiledTest.media, spriteB);

        Assert.assertEquals(tileWidth, spriteB.getTileWidthOriginal());
        Assert.assertEquals(tileWidth, spriteB.getTileWidth());
        Assert.assertEquals(tileHeight, spriteB.getTileHeightOriginal());
        Assert.assertEquals(tileHeight, spriteB.getTileHeight());

        DrawableTestTool.testSpriteLoading(spriteB);

        DrawableTestTool.testSpriteModification(2, spriteB);
        DrawableTestTool.testImageRender(SpriteTiledTest.g, spriteB);
        spriteB.render(SpriteTiledTest.g, 0, 0, 0);

        Assert.assertFalse(spriteA.equals(spriteB));

        // Equals
        final SpriteTiled spriteD = Drawable.loadSpriteTiled(SpriteTiledTest.media, tileWidth, tileHeight);
        final SpriteTiled spriteE = Drawable.loadSpriteTiled(SpriteTiledTest.media, tileWidth + 2, tileHeight + 1);
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

        // Get tile
        Assert.assertNotNull(spriteB.getTile(0));
        Assert.assertNotNull(spriteB.getTile(99));
        Assert.assertNotNull(spriteB.getTile(-1));

        // Error
        DrawableTestTool.testSpriteTiledLoadError(0, 0);
        DrawableTestTool.testSpriteTiledLoadError(0, 1);
        DrawableTestTool.testSpriteTiledLoadError(1, 0);
        Assert.assertFalse(spriteB.equals(Drawable.loadSpriteTiled(SpriteTiledTest.media, tileWidth, tileHeight)));
    }
}
