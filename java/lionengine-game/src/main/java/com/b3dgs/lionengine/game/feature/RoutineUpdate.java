/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Feature;

/**
 * Represents a feature aimed to be updated.
 */
public interface RoutineUpdate extends Feature, Updatable
{
    /** State handler feature priority. */
    int P_STATE = 0;
    /** Actionable feature priority. */
    int P_ACTIONABLE = P_STATE + 1;
    /** Assignable feature priority. */
    int P_ASSIGNABLE = P_ACTIONABLE + 1;
    /** Attacker feature priority. */
    int P_ATTACKER = P_ACTIONABLE + 1;
    /** Extractor feature priority. */
    int P_EXTRACTOR = P_ATTACKER + 1;
    /** Producer feature priority. */
    int P_PRODUCER = P_EXTRACTOR + 1;
    /** Launchable feature priority. */
    int P_LAUNCHABLE = P_PRODUCER + 1;
    /** Launcher feature priority. */
    int P_LAUNCHER = P_LAUNCHABLE + 1;
    /** Custom feature priority. */
    int P_CUSTOM = P_LAUNCHER + 1;
    /** Body feature priority. */
    int P_BODY = P_CUSTOM + 1;
    /** Pathfindable feature priority. */
    int P_PATHFINDABLE = P_BODY + 1;
    /** Collidable feature priority. */
    int P_COLLIDABLE = P_PATHFINDABLE + 1;
    /** Tile collidable feature priority. */
    int P_TILECOLLIDABLE = P_COLLIDABLE + 1;
    /** Mirrorable feature priority. */
    int P_MIRRORABLE = P_TILECOLLIDABLE + 1;
    /** Animatable feature priority. */
    int P_ANIMATABLE = P_MIRRORABLE + 1;
    /** Rasterable feature priority. */
    int P_RASTERABLE = P_ANIMATABLE + 1;
    /** Transformable feature priority. */
    int P_TRANSFORMABLE = P_RASTERABLE + 1;

    /**
     * Compare routine priority.
     * 
     * @param update1 The first.
     * @param update2 The other.
     * @return 1 if update1 over update2, -1 if update2 over update1, 0 if same.
     */
    static int compare(RoutineUpdate update1, RoutineUpdate update2)
    {
        final int priority1 = update1.getPriotityUpdate();
        final int priority2 = update2.getPriotityUpdate();
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
     * Update before main loop. Does nothing by default.
     */
    default void updateBefore()
    {
        // Nothing
    }

    /**
     * Update after main loop. Does nothing by default.
     */
    default void updateAfter()
    {
        // Nothing
    }

    /**
     * Get the ordering priority (higher means high priority, lower means low priority, {@link #P_CUSTOM} is default).
     * 
     * @return The ordering priority.
     */
    default int getPriotityUpdate()
    {
        return P_CUSTOM;
    }
}
