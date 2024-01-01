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
package com.b3dgs.lionengine.editor.world.handler;

import org.eclipse.e4.core.di.annotations.Execute;

import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.updater.WorldZoomUpdater;
import com.b3dgs.lionengine.editor.world.view.WorldPart;
import com.b3dgs.lionengine.editor.world.view.ZoomItem;
import com.b3dgs.lionengine.game.feature.Services;

/**
 * Zoom out handler.
 */
public final class ZoomOutHandler
{
    /** Element ID. */
    public static final String ID = "zoom-out";

    /**
     * Create handler.
     */
    public ZoomOutHandler()
    {
        super();
    }

    /**
     * Execute the handler.
     */
    @Execute
    public void execute()
    {
        final Services services = WorldModel.INSTANCE.getServices();

        final WorldZoomUpdater zoom = services.get(WorldZoomUpdater.class);
        zoom.zoomOut();

        final WorldPart part = services.get(WorldPart.class);
        part.setToolItemText(ZoomItem.ID, String.valueOf(zoom.getPercent()));
        part.update();
    }
}
