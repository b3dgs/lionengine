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
package com.b3dgs.lionengine.game.feature.assignable;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.FeatureInterface;

/**
 * Represents an assignable action, allows to assign an action by click.
 */
@FeatureInterface
public interface Assignable extends Feature, Updatable
{
    /**
     * Set the executable assign.
     * 
     * @param assign The assign to execute.
     */
    void setAssign(Assign assign);

    /**
     * Set the mouse click selection value to {@link Assign#assign()} the assign.
     * 
     * @param click The click number.
     */
    void setClickAssign(Integer click);
}
