/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.swt.graphic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Monitor;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.swt.Keyboard;
import com.b3dgs.lionengine.swt.Mouse;

/**
 * Screen implementation.
 * 
 * @see Keyboard
 * @see Mouse
 */
final class ScreenWindowedSwt extends ScreenSwtAbstract
{
    /** Error message windowed. */
    static final String ERROR_WINDOWED = "Windowed mode initialization failed !";

    /**
     * Internal constructor.
     * 
     * @param config The config reference.
     * @throws LionEngineException If renderer is <code>null</code>, engine has not been started or resolution is not
     *             supported.
     */
    ScreenWindowedSwt(Config config)
    {
        super(config);
    }

    /**
     * Prepare windowed mode.
     * 
     * @param output The output resolution
     * @throws LionEngineException If windowed is not supported.
     */
    private void initWindowed(Resolution output)
    {
        try
        {
            if (canvas == null)
            {
                canvas = new Canvas(frame, SWT.DOUBLE_BUFFERED);
                canvas.setEnabled(true);
                canvas.setVisible(true);
            }
            canvas.setSize(output.getWidth(), output.getHeight());
            buffer = (ImageBufferSwt) Graphics.createImageBuffer(output.getWidth(), output.getHeight());
            frame.pack();

            final Monitor primary = frame.getDisplay().getPrimaryMonitor();
            final Rectangle bounds = primary.getBounds();
            final Rectangle rect = frame.getBounds();
            final int x = bounds.x + (bounds.width - rect.width) / 2;
            final int y = bounds.y + (bounds.height - rect.height) / 2;
            frame.setLocation(x, y);

            buf = canvas;
        }
        catch (final SWTError exception)
        {
            throw new LionEngineException(exception, ERROR_WINDOWED);
        }
    }

    @Override
    protected void setResolution(Resolution output)
    {
        initWindowed(output);
        super.setResolution(output);
    }
}
