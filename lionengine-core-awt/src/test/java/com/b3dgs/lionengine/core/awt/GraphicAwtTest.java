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
package com.b3dgs.lionengine.core.awt;

import java.awt.image.BufferedImage;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.ColorGradient;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.core.ImageBuffer;

/**
 * Test the graphic class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class GraphicAwtTest
{
    /**
     * Test the empty graphic.
     */
    @Test
    public void testEmptyGraphic()
    {
        final GraphicAwt g = new GraphicAwt();
        Assert.assertNull(g.getGraphic());
    }

    /**
     * Test the graphic.
     */
    @Test
    public void testGraphic()
    {
        final BufferedImage buffer = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        final GraphicAwt g = new GraphicAwt(buffer.createGraphics());

        Assert.assertNotNull(g.getGraphic());
        g.clear(0, 0, buffer.getWidth(), buffer.getWidth());
        g.copyArea(0, 0, buffer.getWidth(), buffer.getWidth(), 0, 0);

        final ImageBuffer image = new ImageBufferAwt(buffer);
        g.drawImage(image, 0, 0);
        final TransformAwt transform = new TransformAwt();
        g.drawImage(image, transform, 0, 0);
        g.drawImage(image, transform, 0, 0);
        g.drawImage(image, 0, 0, 0, 0, 0, 0, 0, 0);

        g.drawLine(0, 0, 0, 0);
        g.drawOval(0, 0, buffer.getWidth(), buffer.getWidth(), true);
        g.drawOval(0, 0, buffer.getWidth(), buffer.getWidth(), false);
        g.drawRect(0, 0, buffer.getWidth(), buffer.getWidth(), true);
        g.drawRect(0, 0, buffer.getWidth(), buffer.getWidth(), false);

        Assert.assertEquals(ColorRgba.WHITE.getRgba(), g.getColor().getRgba());
        g.setColor(ColorRgba.BLUE);
        Assert.assertEquals(ColorRgba.BLUE.getRgba(), g.getColor().getRgba());

        g.setColorGradient(new ColorGradient(0, 0, ColorRgba.CYAN, 100, 100, ColorRgba.RED));
        g.drawGradient(0, 0, 100, 100);

        g.dispose();

        g.setGraphic(null);
        Assert.assertNull(g.getGraphic());
    }
}
