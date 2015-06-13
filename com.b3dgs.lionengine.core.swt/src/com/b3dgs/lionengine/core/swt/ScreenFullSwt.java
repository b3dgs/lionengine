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
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Canvas;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Renderer;

/**
 * Screen implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Keyboard
 * @see Mouse
 */
final class ScreenFullSwt
        extends ScreenSwt
{
    /** Error message full screen. */
    private static final String ERROR_FULL_SCREEN = "Full screen mode initialization failed !";

    /**
     * Internal constructor.
     * 
     * @param renderer The renderer reference.
     * @throws LionEngineException If renderer is <code>null</code>, engine has not been started or resolution is not
     *             supported.
     */
    ScreenFullSwt(Renderer renderer) throws LionEngineException
    {
        super(renderer);
    }

    /**
     * Prepare fullscreen mode.
     * 
     * @param output The output resolution
     * @param depth The bit depth color.
     * @throws LionEngineException If full screen is not supported.
     */
    private void initFullscreen(Resolution output, int depth) throws LionEngineException
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
            frame.pack();

            buf = canvas;
            frame.setFullScreen(true);
        }
        catch (final SWTException exception)
        {
            throw new LionEngineException(exception, ScreenFullSwt.ERROR_FULL_SCREEN);
        }
    }

    @Override
    protected void setResolution(Resolution output) throws LionEngineException
    {
        initFullscreen(output, config.getDepth());
        super.setResolution(output);
    }
}
