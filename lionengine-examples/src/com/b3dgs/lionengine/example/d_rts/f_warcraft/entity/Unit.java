package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity;

import java.util.Set;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.ResourcesLoader;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.ability.mover.MoverModel;
import com.b3dgs.lionengine.game.rts.ability.mover.MoverServices;
import com.b3dgs.lionengine.game.rts.ability.mover.MoverUsedServices;

/**
 * Abstract mover entity implementation.
 */
public abstract class Unit
        extends Entity
        implements MoverUsedServices, MoverServices
{
    /** Die states. */
    private static enum Die
    {
        /** No death. */
        NONE,
        /** Dyeing. */
        CURRENT,
        /** Dead. */
        DONE;
    }

    /** Idle animation. */
    protected final Animation animIdle;
    /** Walk animation. */
    protected final Animation animWalk;
    /** Walk animation. */
    protected final Animation animDead;
    /** Mover model. */
    private final MoverModel mover;
    /** Dead animation. */
    private final SpriteAnimated corpse;
    /** Horizontal death offset. */
    private final int deathOffsetX;
    /** Horizontal death offset. */
    private final int deathOffsetY;
    /** Dyeing flag. */
    private Die die;

    /**
     * Constructor.
     * 
     * @param id The entity type enum.
     * @param context The context reference.
     */
    protected Unit(TypeEntity id, Context context)
    {
        super(id, context);
        deathOffsetX = getDataInteger("x", "offsetDeath");
        deathOffsetY = getDataInteger("y", "offsetDeath");
        animIdle = getAnimation("idle");
        animWalk = getAnimation("walk");
        animDead = getAnimation("die");
        mover = new MoverModel(this, context.map);
        corpse = ResourcesLoader.getCorpse(type.race);
        die = Die.NONE;
        setLayer(1);
        play(animIdle);
    }

    /**
     * Update the entity death.
     */
    private void updateDeath()
    {
        switch (die)
        {
            case NONE:
                play(animDead);
                die = Die.CURRENT;
                break;
            case CURRENT:
                if (AnimState.FINISHED == getAnimState())
                {
                    die = Die.DONE;
                    corpse.setFrame(5);
                }
                break;
            case DONE:
                if (AnimState.FINISHED == corpse.getAnimState())
                {
                    destroy();
                }
                break;
            default:
                throw new RuntimeException();
        }
    }

    /*
     * Entity
     */

    @Override
    public void update(double extrp)
    {
        if (isDead())
        {
            updateDeath();
        }
        else
        {
            mover.updateMoves(extrp);
        }
        mirror(getOrientation().ordinal() > Orientation.MID_LENGTH);
        super.update(extrp);
    }

    @Override
    public void render(Graphic g, CameraRts camera)
    {
        if (isDead() && Die.DONE == die)
        {
            render(g, corpse, camera, deathOffsetX, deathOffsetY);
        }
        else
        {
            super.render(g, camera);
        }
    }

    @Override
    public void stop()
    {
        stopMoves();
    }

    /*
     * MoverServices
     */

    @Override
    public Orientation getMovementOrientation()
    {
        return mover.getMovementOrientation();
    }

    @Override
    public void pointTo(int tx, int ty)
    {
        mover.pointTo(tx, ty);
    }

    @Override
    public void pointTo(Tiled entity)
    {
        mover.pointTo(entity);
    }

    @Override
    public void setDestination(Tiled entity)
    {
        mover.setDestination(entity);
    }

    /*
     * MoverUsedServices
     */

    @Override
    public void setDestination(double extrp, double dx, double dy)
    {
        mover.setDestination(extrp, dx, dy);
    }

    @Override
    public boolean setDestination(int tx, int ty)
    {
        return mover.setDestination(tx, ty);
    }

    @Override
    public void setLocation(int tx, int ty)
    {
        super.setLocation(tx, ty);
        mover.setLocation(tx, ty);
    }

    @Override
    public boolean isPathAvailable(int tx, int ty)
    {
        return mover.isPathAvailable(tx, ty);
    }

    @Override
    public void setIgnoreId(Integer id, boolean state)
    {
        mover.setIgnoreId(id, state);
    }

    @Override
    public boolean isIgnoredId(Integer id)
    {
        return mover.isIgnoredId(id);
    }

    @Override
    public void clearIgnoredId()
    {
        mover.clearIgnoredId();
    }

    @Override
    public void setSharedPathIds(Set<Integer> ids)
    {
        mover.setSharedPathIds(ids);
    }

    @Override
    public void clearSharedPathIds()
    {
        mover.clearSharedPathIds();
    }

    @Override
    public void updateMoves(double extrp)
    {
        mover.updateMoves(extrp);
    }

    @Override
    public void stopMoves()
    {
        mover.stopMoves();
    }

    @Override
    public void setSpeed(double speedX, double speedY)
    {
        mover.setSpeed(speedX, speedY);
    }

    @Override
    public boolean isMoving()
    {
        return mover.isMoving();
    }

    @Override
    public boolean isDestinationReached()
    {
        return mover.isDestinationReached();
    }

    @Override
    public double getSpeedX()
    {
        return mover.getSpeedX();
    }

    @Override
    public double getSpeedY()
    {
        return mover.getSpeedY();
    }

    @Override
    public double getMoveX()
    {
        return mover.getMoveX();
    }

    @Override
    public double getMoveY()
    {
        return mover.getMoveY();
    }

    /*
     * MoverListener
     */

    @Override
    public void notifyStartMove()
    {
        play(animWalk);
    }

    @Override
    public void notifyMoving()
    {
        // Nothing to do
    }

    @Override
    public void notifyArrived()
    {
        play(animIdle);
    }
}
