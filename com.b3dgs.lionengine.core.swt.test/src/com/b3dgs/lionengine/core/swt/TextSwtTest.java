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
package com.b3dgs.lionengine.core.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Text;

/**
 * Test the text class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class TextSwtTest
{
    /**
     * Test the text.
     */
    @Test
    public void testText()
    {
        final Display display = UtilityImage.getDisplay();
        final TextSwt text = new TextSwt(display, Text.DIALOG, 10, TextStyle.NORMAL);
        final TextSwt textIta = new TextSwt(display, Text.DIALOG, 10, TextStyle.BOLD);
        final TextSwt textBold = new TextSwt(display, Text.DIALOG, 10, TextStyle.ITALIC);
        final Image buffer = UtilityImage.createImage(100, 100, SWT.TRANSPARENCY_NONE);
        final GC gc = new GC(buffer);
        final GraphicSwt g = new GraphicSwt(gc);

        text.draw(g, 0, 0, "test");
        text.draw(g, 0, 0, Align.LEFT, "test");
        textIta.draw(g, 0, 0, Align.CENTER, "test");
        textBold.draw(g, 0, 0, Align.RIGHT, "test");

        textIta.setText("toto");
        textIta.setAlign(Align.CENTER);
        textIta.setColor(ColorRgba.BLACK);
        textIta.setLocation(10, 20);
        textIta.render(g);

        Assert.assertEquals(10, textIta.getLocationX());
        Assert.assertEquals(20, textIta.getLocationY());
        Assert.assertEquals(10, textIta.getSize());

        Assert.assertEquals(3, text.getStringWidth(g, Constant.SPACE));
        Assert.assertEquals(11, text.getStringHeight(g, "test"));

        Assert.assertEquals(18, textIta.getWidth());
        Assert.assertEquals(11, textIta.getHeight());

        g.dispose();
        buffer.dispose();
    }
}
