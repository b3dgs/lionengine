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
package com.b3dgs.lionengine.test.core.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.ImageBuffer;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.swt.ToolsSwt;

/**
 * Test the image buffer class.
 */
public class ImageBufferSwtTest
{
    /**
     * Test the image.
     */
    @Test
    public void testImage()
    {
        final Image buffer = ToolsSwt.createImage(100, 100, SWT.TRANSPARENCY_NONE);
        final ImageData data = buffer.getImageData();
        final ImageBuffer image = ToolsSwt.getImageBuffer(buffer);

        Assert.assertNotNull(image.createGraphic());

        image.prepare();
        Assert.assertNotEquals(buffer, image.getSurface());

        Assert.assertEquals(-1, image.getRgb(0, 0));
        Assert.assertNotNull(image.getRgb(0, 0, 1, 1, new int[1], 0, 0));
        Assert.assertEquals(Transparency.OPAQUE, image.getTransparency());
        Assert.assertEquals(data.width, image.getWidth());
        Assert.assertEquals(data.height, image.getHeight());

        image.setRgb(0, 0, ColorRgba.BLUE.getRgba());
        Assert.assertEquals(ColorRgba.BLUE.getRgba(), image.getRgb(0, 0));
        image.setRgb(0, 0, 0, 0, new int[1], 0, 0);

        image.dispose();

        Assert.assertEquals(Transparency.OPAQUE, ToolsSwt.getTransparency(SWT.TRANSPARENCY_NONE));
        Assert.assertEquals(Transparency.BITMASK, ToolsSwt.getTransparency(SWT.TRANSPARENCY_MASK));
        Assert.assertEquals(Transparency.TRANSLUCENT, ToolsSwt.getTransparency(SWT.TRANSPARENCY_ALPHA));
        Assert.assertEquals(Transparency.OPAQUE, ToolsSwt.getTransparency(465546));
    }
}
