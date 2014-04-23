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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.ImageInfo;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.FactoryGraphicMock;
import com.b3dgs.lionengine.core.FactoryMediaMock;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityImage;

/**
 * Test the font sprite class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SpriteFontTest
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
        UtilityImage.setGraphicFactory(new FactoryGraphicMock());
        Media.setMediaFactory(new FactoryMediaMock());
        SpriteFontTest.media = Media.create(Media.getPath("src", "test", "resources", "drawable", "image.png"));
        SpriteFontTest.g = UtilityImage.createImageBuffer(100, 100, Transparency.OPAQUE).createGraphic();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        UtilityImage.setGraphicFactory(null);
        Media.setMediaFactory(null);
    }

    /**
     * Test the sprite font class.
     */
    @Test
    public void testSpriteFont()
    {
        final Media fontData = Media.create(Media.getPath("src", "test", "resources", "drawable", "fontdata.xml"));
        final String text = "a%z";
        final SpriteFont sprite = Drawable.loadSpriteFont(SpriteFontTest.media, fontData, 6, 7);
        Assert.assertTrue(sprite.equals(Drawable.loadSpriteFont(SpriteFontTest.media, fontData, 6, 7)));

        final ImageInfo info = DrawableTestTool.assertImageInfoCorrect(SpriteFontTest.media, sprite);
        Assert.assertEquals(info.getWidth(), sprite.getWidthOriginal());
        Assert.assertEquals(info.getHeight(), sprite.getHeightOriginal());

        DrawableTestTool.testSpriteLoading(sprite);
        DrawableTestTool.testImageRender(SpriteFontTest.g, sprite);
        DrawableTestTool.testSpriteModification(2, sprite);
        sprite.setFade(0, -255);
        sprite.scale(200);

        sprite.draw(SpriteFontTest.g, 0, 0, Align.LEFT, text);
        sprite.draw(SpriteFontTest.g, 0, 0, Align.CENTER, text);
        sprite.draw(SpriteFontTest.g, 0, 0, Align.RIGHT, text);
        sprite.setLineHeight(0);
        Assert.assertTrue(sprite.getTextWidth(text) >= 1);
        Assert.assertTrue(sprite.getTextHeight(text) >= 0);

        Assert.assertFalse(sprite.equals(Drawable.loadSpriteFont(SpriteFontTest.media, fontData, 6, 7)));

        sprite.stretch(90, 110);
        Assert.assertFalse(sprite.equals(Drawable.loadSpriteFont(SpriteFontTest.media, fontData, 6, 7)));
    }
}
