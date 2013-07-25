package com.b3dgs.lionengine.example.d_rts.e_skills.entity;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.d_rts.e_skills.Context;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeEntity;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;

/**
 * Factory entity implementation, creating entity from simple function. This allows to set correctly the new instance,
 * depending of what we need. For example, if we want to link another component to any of our entities, we just have to
 * perform the link in the corresponding function (here createPeon), and that's all. Any object that want to create new
 * instances just need a reference to this factory.
 */
public class FactoryEntity
        extends FactoryEntityGame<TypeEntity, SetupEntityGame, Entity>
{
    /** Directory name from our resources directory containing our entities. */
    public static final String ENTITY_PATH = "entities";
    /** Context reference. */
    private Context context;

    /**
     * Constructor. Entity data are stored in a .txt file and the surface in .png file.
     */
    public FactoryEntity()
    {
        super(TypeEntity.class);
        loadAll(TypeEntity.values());
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

    @Override
    protected SetupEntityGame createSetup(TypeEntity id)
    {
        return new SetupEntityGame(new ConfigurableModel(), Media.get(FactoryEntity.ENTITY_PATH, id + ".xml"), false);
    }

    @Override
    public Entity createEntity(TypeEntity type)
    {
        switch (type)
        {
            case peon:
                return new Peon(context);
            case grunt:
                return new Grunt(context);
            case spearman:
                return new Spearman(context);
            case townhall_orc:
                return new TownHall(context);
            case barracks_orc:
                return new Barracks(context);
            case gold_mine:
                return new GoldMine(context);
            default:
                throw new LionEngineException("Entity not found: " + type.name());
        }
    }
}
