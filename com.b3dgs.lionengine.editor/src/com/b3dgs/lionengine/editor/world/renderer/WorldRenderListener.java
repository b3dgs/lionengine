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
package com.b3dgs.lionengine.editor.world.renderer;

import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Listener to rendering event.
 */
public interface WorldRenderListener
{
    /** Extension ID. */
    String EXTENSION_ID = Activator.PLUGIN_ID + ".worldRenderListener";

    /**
     * Called when rendering is requested.
     * 
     * @param g The graphic output.
     * @param width The rendering width.
     * @param height The rendering height.
     * @param scale The current world scaling.
     * @param tw The current tile width.
     * @param th The current tile height.
     */
    void onRender(Graphic g, int width, int height, double scale, int tw, int th);
}
