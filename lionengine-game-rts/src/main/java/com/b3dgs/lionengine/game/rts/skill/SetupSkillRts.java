/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.rts.skill;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.SetupGame;

/**
 * Minimum skill requirement definition, used to create skills.
 */
public abstract class SetupSkillRts
        extends SetupGame
{
    /** Displayed name. */
    public final String name;
    /** Description. */
    public final String description;

    /**
     * Constructor. The minimum XML data needed.
     * 
     * <pre>
     * {@code
     * <skill name="Name" description="Description">
     * </skill>
     * }
     * </pre>
     * 
     * @param config The config media.
     */
    public SetupSkillRts(Media config)
    {
        super(config);
        name = configurable.getDataString("name");
        description = configurable.getDataString("description");
    }
}
