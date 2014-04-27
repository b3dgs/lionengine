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
import com.b3dgs.lionengine.ImageInfo;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.FactoryGraphicProvider;
import com.b3dgs.lionengine.core.FactoryMediaProvider;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.mock.FactoryMediaMock;

/**
 * Test the font sprite class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SpriteFontTest
{
    /** Image media. */
    protected static Media media;
    /** Font data. */
    protected static Media font;
    /** Graphic test output. */
    protected static Graphic g;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(new FactoryGraphicMock());
        FactoryMediaProvider.setFactoryMedia(new FactoryMediaMock());
        SpriteFontTest.media = Core.MEDIA.create("src", "test", "resources", "drawable", "image.png");
        SpriteFontTest.font = Core.MEDIA.create("src", "test", "resources", "drawable", "fontdata.xml");
        SpriteFontTest.g = Core.GRAPHIC.createImageBuffer(100, 100, Transparency.OPAQUE).createGraphic();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(null);
        FactoryMediaProvider.setFactoryMedia(null);
    }

    /**
     * Test the sprite font class.
     */
    @Test
    public void testSpriteFont()
    {
        final String text = "a%z";
        final SpriteFont sprite = Drawable.loadSpriteFont(SpriteFontTest.media, SpriteFontTest.font, 6, 7);
        Assert.assertTrue(sprite.equals(Drawable.loadSpriteFont(SpriteFontTest.media, SpriteFontTest.font, 6, 7)));

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

        Assert.assertFalse(sprite.equals(Drawable.loadSpriteFont(SpriteFontTest.media, SpriteFontTest.font, 6, 7)));

        sprite.stretch(90, 110);
        Assert.assertFalse(sprite.equals(Drawable.loadSpriteFont(SpriteFontTest.media, SpriteFontTest.font, 6, 7)));
    }
}
