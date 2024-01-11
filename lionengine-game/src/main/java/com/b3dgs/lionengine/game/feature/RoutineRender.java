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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Represents a feature aimed to be rendered.
 */
public interface RoutineRender extends Feature, Renderable
{
    /** Rasterable feature priority. */
    int RASTERABLE = 0;
    /** State handler feature priority. */
    int STATE = RASTERABLE + 1;
    /** Actionable feature priority. */
    int ACTIONABLE = STATE + 1;
    /** Assignable ACTIONABLE priority. */
    int ASSIGNABLE = STATE + 1;
    /** Attacker feature priority. */
    int ATTACKER = ACTIONABLE + 1;
    /** Extractor feature priority. */
    int EXTRACTOR = ATTACKER + 1;
    /** Producer feature priority. */
    int PRODUCER = EXTRACTOR + 1;
    /** Launchable feature priority. */
    int LAUNCHABLE = PRODUCER + 1;
    /** Launcher feature priority. */
    int LAUNCHER = LAUNCHABLE + 1;
    /** Custom feature priority. */
    int CUSTOM = LAUNCHER + 1;
    /** Body feature priority. */
    int BODY = CUSTOM + 1;
    /** Pathfindable feature priority. */
    int PATHFINDABLE = BODY + 1;
    /** Collidable feature priority. */
    int COLLIDABLE = PATHFINDABLE + 1;
    /** Tile collidable feature priority. */
    int TILECOLLIDABLE = COLLIDABLE + 1;
    /** Mirrorable feature priority. */
    int MIRRORABLE = TILECOLLIDABLE + 1;
    /** Animatable feature priority. */
    int ANIMATABLE = MIRRORABLE + 1;
    /** Transformable feature priority. */
    int TRANSFORMABLE = ANIMATABLE + 1;

    /**
     * Compare routine priority.
     * 
     * @param render1 The first.
     * @param render2 The other.
     * @return 1 if render1 over render2, -1 if render2 over render1, 0 if same.
     */
    static int compare(RoutineRender render1, RoutineRender render2)
    {
        final int priority1 = render1.getPriotityRender();
        final int priority2 = render2.getPriotityRender();
        final int compare;

        if (priority1 > priority2)
        {
            compare = 1;
        }
        else if (priority1 < priority2)
        {
            compare = -1;
        }
        else
        {
            compare = 0;
        }
        return compare;
    }

    /**
     * Get the ordering priority (higher means high priority, lower means low priority, 0 is default).
     * 
     * @return The ordering priority.
     */
    default int getPriotityRender()
    {
        return CUSTOM;
    }
}
