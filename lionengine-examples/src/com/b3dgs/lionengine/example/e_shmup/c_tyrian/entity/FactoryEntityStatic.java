package com.b3dgs.lionengine.example.e_shmup.c_tyrian.entity;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;

/**
 * Factory entity.
 */
public class FactoryEntityStatic
        extends FactoryEntityGame<EntityStaticType, SetupSurfaceGame, Entity>
{
    /**
     * Constructor
     */
    public FactoryEntityStatic()
    {
        super(EntityStaticType.class);
        loadAll(EntityStaticType.values());
    }

    @Override
    public Entity createEntity(EntityStaticType id)
    {
        switch (id)
        {
            default:
                return new Entity(getSetup(id));
        }
    }

    @Override
    protected SetupSurfaceGame createSetup(EntityStaticType id)
    {
        return new SetupSurfaceGame(Media.get("entities", "static", id.toString() + ".xml"));
    }
}
