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
package com.b3dgs.lionengine.core.awt;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.io.awt.Keyboard;
import com.b3dgs.lionengine.io.awt.Mouse;

/**
 * Windowed screen implementation.
 * 
 * @see Keyboard
 * @see Mouse
 */
final class ScreenWindowedAwt extends ScreenBaseAwt
{
    /**
     * Internal constructor.
     * 
     * @param config The config reference.
     * @throws LionEngineException If renderer is <code>null</code> or no available display.
     */
    ScreenWindowedAwt(Config config)
    {
        super(config);
    }

    /**
     * Prepare windowed mode.
     * 
     * @param output The output resolution
     * @throws LionEngineException If unable to initialize windowed mode.
     */
    private void initWindowed(Resolution output)
    {
        final Canvas canvas = new Canvas(conf);
        canvas.setBackground(Color.BLACK);
        canvas.setEnabled(true);
        canvas.setVisible(true);
        canvas.setIgnoreRepaint(true);

        frame.add(canvas);

        canvas.setPreferredSize(new Dimension(output.getWidth(), output.getHeight()));
        frame.pack();
        frame.setLocationRelativeTo(null);

        ToolsAwt.createBufferStrategy(canvas, conf);
        buf = canvas.getBufferStrategy();

        // Set input listeners
        componentForKeyboard = canvas;
        componentForMouse = canvas;
        componentForCursor = frame;
        frame.validate();
    }

    /*
     * ScreenAwt
     */

    @Override
    protected void setResolution(Resolution output)
    {
        initWindowed(output);
        super.setResolution(output);
    }
}
