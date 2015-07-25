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
package com.b3dgs.lionengine.editor.world.handler;

import org.eclipse.e4.core.di.annotations.Execute;

import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.editor.world.WorldViewPart;
import com.b3dgs.lionengine.editor.world.ZoomItem;
import com.b3dgs.lionengine.editor.world.updater.WorldZoom;

/**
 * Zoom in handler.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class ZoomInHandler
{
    /** Element ID. */
    public static final String ID = "zoom-in";

    /**
     * Create handler.
     */
    public ZoomInHandler()
    {
        // Nothing to do
    }

    /**
     * Execute the handler.
     */
    @Execute
    @SuppressWarnings("static-method")
    public void execute()
    {
        final WorldZoom zoom = WorldViewModel.INSTANCE.getServices().get(WorldZoom.class);
        final double scale = zoom.getPercent() / 100.0;

        final int tw = WorldViewModel.INSTANCE.getMap().getTileWidth();
        final int step = (int) Math.ceil((tw * scale + 1) / tw * 100.0);
        zoom.setPercent(step);

        final WorldViewPart part = UtilEclipse.getPart(WorldViewPart.ID, WorldViewPart.class);
        part.setToolItemText(ZoomItem.ID, String.valueOf(step));
        part.update();
    }
}
