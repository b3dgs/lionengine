package com.b3dgs.lionengine.example.d_rts.d_ability.entity;

import java.util.Set;

import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.TypeEntity;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.rts.ability.mover.MoverModel;
import com.b3dgs.lionengine.game.rts.ability.mover.MoverServices;
import com.b3dgs.lionengine.game.rts.ability.mover.MoverUsedServices;

/**
 * Abstract mover entity implementation.
 */
abstract class Unit
        extends Entity
        implements MoverUsedServices, MoverServices
{
    /** Idle animation. */
    protected final Animation animIdle;
    /** Walk animation. */
    protected final Animation animWalk;
    /** Mover model. */
    private final MoverModel mover;

    /**
     * Constructor.
     * 
     * @param id The entity type enum.
     * @param context The context reference.
     */
    protected Unit(TypeEntity id, Context context)
    {
        super(id, context);
        animIdle = getAnimation("idle");
        animWalk = getAnimation("walk");
        mover = new MoverModel(this, context.map);
        setLayer(1);
        play(animIdle);
    }

    /*
     * Entity
     */

    @Override
    public void update(double extrp)
    {
        mover.updateMoves(extrp);
        mirror(getOrientation().ordinal() > Orientation.ORIENTATIONS_NUMBER_HALF);
        super.update(extrp);
    }

    @Override
    public void stop()
    {
        mover.stopMoves();
    }

    /*
     * Mover model
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
    public void setDestination(Tiled tiled)
    {
        mover.setDestination(tiled);
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
     * Mover listener
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
