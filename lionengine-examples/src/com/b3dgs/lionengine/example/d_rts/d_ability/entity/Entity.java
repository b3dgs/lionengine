package com.b3dgs.lionengine.example.d_rts.d_ability.entity;

import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.Map;
import com.b3dgs.lionengine.example.d_rts.d_ability.TypeEntity;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.rts.EntityRts;

/**
 * Abstract entity implementation
 */
public abstract class Entity
        extends EntityRts
{
    /** Entity type. */
    public final TypeEntity type;
    /** Entity life. */
    public final Alterable life;
    /** Map reference. */
    protected final Map map;

    /**
     * Constructor.
     * 
     * @param id The entity type enum.
     * @param context The context reference.
     */
    protected Entity(TypeEntity id, Context context)
    {
        super(context.factoryEntity.getSetup(id), context.map);
        type = id;
        map = context.map;
        life = new Alterable(getDataInteger("life", "attributes"));
    }

    /**
     * Get the current life.
     * 
     * @return The current life.
     */
    public int getLife()
    {
        return life.getCurrent();
    }
}
