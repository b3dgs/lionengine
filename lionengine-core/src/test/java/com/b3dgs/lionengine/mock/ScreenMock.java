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
package com.b3dgs.lionengine.mock;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.InputDevice;
import com.b3dgs.lionengine.core.InputDeviceKeyListener;
import com.b3dgs.lionengine.core.Screen;
import com.b3dgs.lionengine.core.Sequence;

/**
 * Screen mock.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ScreenMock
        implements Screen
{
    /** Config. */
    private final Config config;

    /**
     * Constructor.
     * 
     * @param config The config reference.
     */
    public ScreenMock(Config config)
    {
        this.config = config;
    }

    /*
     * Screen
     */

    @Override
    public void start()
    {
        // Mock
    }

    @Override
    public void preUpdate()
    {
        // Mock
    }

    @Override
    public void update()
    {
        // Mock
    }

    @Override
    public void dispose()
    {
        // Mock
    }

    @Override
    public void requestFocus()
    {
        // Mock
    }

    @Override
    public void hideCursor()
    {
        // Mock
    }

    @Override
    public void showCursor()
    {
        // Mock
    }

    @Override
    public void addKeyListener(InputDeviceKeyListener listener)
    {
        // Mock
    }

    @Override
    public void setSequence(Sequence sequence)
    {
        sequence.onFocusGained();
        sequence.onLostFocus();
    }

    @Override
    public void setIcon(String filename)
    {
        // Mock
    }

    @Override
    public Graphic getGraphic()
    {
        return new GraphicMock();
    }

    @Override
    public Config getConfig()
    {
        return config;
    }

    @Override
    public <T extends InputDevice> T getInputDevice(Class<T> type)
    {
        return null;
    }

    @Override
    public int getX()
    {
        return 0;
    }

    @Override
    public int getY()
    {
        return 0;
    }

    @Override
    public boolean isReady()
    {
        return true;
    }
}
