package com.b3dgs.lionengine.example.d_rts.d_ability.entity;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.EntityType;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;

/**
 * Factory entity implementation, creating entity from simple function. This allows to set correctly the new instance,
 * depending of what we need. For example, if we want to link another component to any of our entities, we just have to
 * perform the link in the corresponding function (here createPeon), and that's all. Any object that want to create new
 * instances just need a reference to this factory.
 */
public final class FactoryEntity
        extends FactoryEntityGame<EntityType, SetupSurfaceGame, Entity>
{
    /** Directory name from our resources directory containing our entities. */
    public static final String ENTITY_PATH = "entities";
    /** Context. */
    private Context context;

    /**
     * Constructor. Entity data are stored in a .txt file and the surface in .png file.
     */
    public FactoryEntity()
    {
        super(EntityType.class);
        // This function will perform an auto mapping between the types and their data + surface
        // It is recommended to use the same name between the type and the entity directory contains its data with the
        // same name
        // In our case, we only have a peon, with peon.txt (data) and peon.png (surface)
        loadAll(EntityType.values());
    }

    /**
     * Set the context.
     * 
     * @param context The context
     */
    public void setContext(Context context)
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
            case GRUNT:
                return new Grunt(context);
            case SPEARMAN:
                return new Spearman(context);
            case TOWNHALL_ORC:
                return new TownHall(context);
            case FARM_ORC:
                return new Farm(context);
            case BARRACKS_ORC:
                return new Barracks(context);
            case GOLD_MINE:
                return new GoldMine(context);
            default:
                throw new LionEngineException("Entity not found: " + type.name());
        }
    }

    @Override
    protected SetupSurfaceGame createSetup(EntityType id)
    {
        return new SetupSurfaceGame(new ConfigurableModel(), Media.get(FactoryEntity.ENTITY_PATH, id + ".xml"), false);
    }
}
