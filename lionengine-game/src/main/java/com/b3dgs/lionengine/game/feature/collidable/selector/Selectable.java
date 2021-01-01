/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable.selector;

import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.FeatureInterface;

/**
 * Selection event linking.
 */
@FeatureInterface
public interface Selectable extends Feature
{
    /**
     * Called when has been selected.
     * 
     * @param selected <code>true</code> if selected, <code>false</code> if unselected.
     */
    void onSelection(boolean selected);

    /**
     * Check if is selected.
     * 
     * @return <code>true</code> if selected, <code>false</code> else.
     */
    boolean isSelected();
}
