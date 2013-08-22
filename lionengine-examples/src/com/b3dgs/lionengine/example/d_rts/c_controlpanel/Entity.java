package com.b3dgs.lionengine.example.d_rts.c_controlpanel;

import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.rts.EntityRts;

/**
 * Abstract entity implementation.
 */
abstract class Entity
        extends EntityRts
{
    /** Entity life. */
    protected final Alterable life;
    /** Idle animation. */
    protected final Animation animIdle;
    /** Map reference. */
    protected final Map map;

    /**
     * Constructor.
     * 
     * @param id The entity type enum.
     * @param context The context reference.
     */
    Entity(TypeEntity id, Context context)
    {
        super(context.factory.getSetup(id), context.map);
        map = context.map;
        animIdle = getAnimation("idle");
        life = new Alterable(1);
        play(animIdle);
    }

    /**
     * Get the current life.
     * 
     * @return The current life.
     */
    int getLife()
    {
        return life.getCurrent();
    }

    /*
     * EntityRts
     */

    @Override
    public void update(double extrp)
    {
        mirror(getOrientation().ordinal() > Orientation.MID_LENGTH);
        super.update(extrp);
    }

    @Override
    public void stop()
    {
        // Nothing for the moment
    }
}
