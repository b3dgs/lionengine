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
package com.b3dgs.lionengine.editor.world.updater;

import com.b3dgs.lionengine.editor.Activator;

/**
 * Listen to world mouse mouse event.
 */
public interface WorldMouseMoveListener
{
    /** Extension ID. */
    String EXTENSION_ID = Activator.PLUGIN_ID + ".worldMouseMoveListener";

    /**
     * Called when the mouse moved.
     * 
     * @param click The click number ({@link com.b3dgs.lionengine.swt.graphic.MouseSwt#LEFT},
     *            {@link com.b3dgs.lionengine.swt.graphic.MouseSwt#RIGHT},
     *            {@link com.b3dgs.lionengine.swt.graphic.MouseSwt#MIDDLE}.
     * @param oldMx The old horizontal location.
     * @param oldMy The old vertical location.
     * @param mx The current horizontal location.
     * @param my The current vertical location.
     */
    void onMouseMoved(int click, int oldMx, int oldMy, int mx, int my);
}
