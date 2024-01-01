/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.engine;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.graphic.Screen;

/**
 * Wrap screen into context to avoid direct reference.
 */
final class ContextWrapper implements Context
{
    /** Screen reference. */
    private final Screen screen;

    /**
     * Create wrapper.
     * 
     * @param screen The wrapper screen (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    ContextWrapper(Screen screen)
    {
        super();

        Check.notNull(screen);

        this.screen = screen;
    }

    @Override
    public int getX()
    {
        return screen.getX();
    }

    @Override
    public int getY()
    {
        return screen.getY();
    }

    @Override
    public int getWidth()
    {
        return screen.getWidth();
    }

    @Override
    public int getHeight()
    {
        return screen.getHeight();
    }

    @Override
    public Config getConfig()
    {
        return screen.getConfig();
    }

    @Override
    public <T extends InputDevice> T getInputDevice(Class<T> type)
    {
        return screen.getInputDevice(type);
    }
}
