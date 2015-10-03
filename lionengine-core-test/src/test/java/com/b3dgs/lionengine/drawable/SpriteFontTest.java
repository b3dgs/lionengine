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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.test.DrawableTestTool;
import com.b3dgs.lionengine.test.FactoryGraphicMock;
import com.b3dgs.lionengine.test.MediaMock;
import com.b3dgs.lionengine.test.ViewerMock;

/**
 * Test the font sprite class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SpriteFontTest
{
    /** Image media. */
    private static Media MEDIA = new MediaMock("image.png");
    /** Font data. */
    private static Media FONT = new MediaMock("fontdata.xml");
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
     * Test the sprite font class.
     */
    @Test
    public void testSpriteFont()
    {
        final String text = "a%z";
        final SpriteFont sprite = Drawable.loadSpriteFont(MEDIA, FONT, 6, 7);
        Assert.assertTrue(sprite.equals(Drawable.loadSpriteFont(MEDIA, FONT, 6, 7)));

        sprite.setOrigin(Origin.TOP_LEFT);
        sprite.setLocation(1.0, 2.0);
        sprite.setLocation(new ViewerMock(), sprite);
        sprite.setMirror(Mirror.VERTICAL);
        Assert.assertEquals(1.0, sprite.getX(), 0.001);
        Assert.assertEquals(2.0, sprite.getY(), 0.001);
        Assert.assertEquals(Mirror.VERTICAL, sprite.getMirror());

        DrawableTestTool.testSpriteLoading(sprite);
        DrawableTestTool.testImageRender(g, sprite);
        DrawableTestTool.testSpriteModification(2, sprite);
        sprite.setFade(0, -255);
        sprite.stretch(200, 200);

        sprite.draw(g, 0, 0, Align.LEFT, text);
        sprite.draw(g, 0, 0, Align.CENTER, text);
        sprite.draw(g, 0, 0, Align.RIGHT, text);
        sprite.setLineHeight(0);
        Assert.assertTrue(sprite.getTextWidth(text) >= 1);
        Assert.assertTrue(sprite.getTextHeight(text) >= 0);

        Assert.assertFalse(sprite.equals(Drawable.loadSpriteFont(MEDIA, FONT, 6, 7)));

        sprite.stretch(90, 110);
        Assert.assertFalse(sprite.equals(Drawable.loadSpriteFont(MEDIA, FONT, 6, 7)));

        // Hash code
        final SpriteFont spriteB = Drawable.loadSpriteFont(MEDIA, FONT, 5, 4);
        Assert.assertTrue(sprite.hashCode() != spriteB.hashCode());
    }
}
