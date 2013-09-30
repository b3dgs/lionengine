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
package com.b3dgs.lionengine.example.d_rts.c_controlpanel;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;

/**
 * Factory entity implementation, creating entity from simple function. This allows to set correctly the new instance,
 * depending of what we need. For example, if we want to link another component to any of our entities, we just have to
 * perform the link in the corresponding function (here createPeon), and that's all. Any object that want to create new
 * instances just need a reference to this factory.
 */
final class FactoryEntity
        extends FactoryEntityGame<EntityType, SetupSurfaceGame, Entity>
{
    /** Directory name from our resources directory containing our entities. */
    private static final String ENTITY_PATH = "entities";
    /** Context. */
    private Context context;

    /**
     * Constructor. Entity data are stored in a .txt file and the surface in .png file.
     */
    FactoryEntity()
    {
        super(EntityType.class);

        // This function will perform an auto mapping between the types and their data + surface.
        // It is recommended to use the same name between the type and the entity directory containing its data with the
        // same name. In our case, we only have a peon, with peon.xml (data) and peon.png (surface)
        loadAll(EntityType.values());
    }

    /**
     * Set the context.
     * 
     * @param context The context reference.
     */
    void setContext(Context context)
    {
        this.context = context;
    }

    /*
     * FactoryEntityGame
     */

    @Override
    public Entity createEntity(EntityType type)
    {
        Check.notNull(context, "The context must not be null !");
        switch (type)
        {
            case PEON:
                return new Peon(context);
            default:
                throw new LionEngineException("Entity not found: " + type.name());
        }
    }

    @Override
    protected SetupSurfaceGame createSetup(EntityType id)
    {
        return new SetupSurfaceGame(Media.get(FactoryEntity.ENTITY_PATH, id + ".xml"));
    }
}
