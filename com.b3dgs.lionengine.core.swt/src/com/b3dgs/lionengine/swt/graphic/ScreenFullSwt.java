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
import org.eclipse.swt.widgets.Canvas;

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
final class ScreenFullSwt extends ScreenSwtAbstract
{
    /** Error message full screen. */
    static final String ERROR_FULL_SCREEN = "Full screen mode initialization failed !";

    /**
     * Internal constructor.
     * 
     * @param config The config reference.
     * @throws LionEngineException If renderer is <code>null</code>, engine has not been started or resolution is not
     *             supported.
     */
    ScreenFullSwt(Config config)
    {
        super(config);
    }

    /**
     * Prepare fullscreen mode.
     * 
     * @param output The output resolution
     * @throws LionEngineException If full screen is not supported.
     */
    private void initFullscreen(Resolution output)
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

            buf = canvas;
            frame.setFullScreen(true);
        }
        catch (final SWTError exception)
        {
            throw new LionEngineException(exception, ScreenFullSwt.ERROR_FULL_SCREEN);
        }
    }

    @Override
    protected void setResolution(Resolution output)
    {
        initFullscreen(output);
        super.setResolution(output);
    }
}
