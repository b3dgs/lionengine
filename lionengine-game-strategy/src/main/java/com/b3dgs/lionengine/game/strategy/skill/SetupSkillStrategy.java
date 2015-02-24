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
package com.b3dgs.lionengine.game.strategy.skill;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.object.Setup;

/**
 * Minimum skill requirement definition, used to create skills.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class SetupSkillStrategy
        extends Setup
{
    /** Displayed name. */
    public final String name;
    /** Description. */
    public final String description;

    /**
     * Constructor base. The minimum XML data needed.
     * 
     * <pre>
     * {@code
     * <skill name="Name" description="Description">
     * </skill>
     * }
     * </pre>
     * 
     * @param config The config media.
     * @throws LionEngineException If error when opening the media.
     */
    public SetupSkillStrategy(Media config) throws LionEngineException
    {
        super(config);
        name = configurer.getText("name");
        description = configurer.getText("description");
    }
}
