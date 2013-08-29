package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import java.util.EnumMap;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Tile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TileCollision;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TileCollisionGroup;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Movement;
import com.b3dgs.lionengine.game.entity.SetupEntityGame;

/**
 * Abstract entity base implementation designed to move around the map.
 */
public abstract class EntityMover
        extends Entity
{
    /** Entity actions. */
    protected final EnumMap<EntityAction, Boolean> actions;
    /** Movement force. */
    protected final Movement movement;
    /** Movement jump force. */
    protected final Force jumpForce;
    /** Jump max force. */
    protected final double jumpHeightMax;
    /** Extra gravity force. */
    private final Force extraGravityForce;
    /** Forces list used. */
    private final Force[] forces;

    /**
     * Constructor.
     * 
     * @param type The entity type.
     * @param setup The setup reference.
     * @param map The map reference.
     * @param desiredFps The desired fps.
     */
    public EntityMover(TypeEntity type, SetupEntityGame setup, Map map, int desiredFps)
    {
        super(type, setup, map, desiredFps);
        movement = new Movement();
        actions = new EnumMap<>(EntityAction.class);
        extraGravityForce = new Force();
        jumpForce = new Force();
        jumpHeightMax = getDataDouble("heightMax", "data", "jump");
        setMass(getDataDouble("mass", "data"));
        setGravityMax(getDataDouble("gravityMax", "data"));
        setFrameOffsets(getWidth() / 2, -8);
        forces = new Force[]
        {
                jumpForce, extraGravityForce, movement.getForce()
        };
    }

    /**
     * Update the actions.
     * 
     * @see EntityAction
     */
    protected abstract void updateActions();

    /**
     * Check if entity can jump.
     * 
     * @return <code>true</code> if can jump, <code>false</code> else.
     */
    public boolean canJump()
    {
        return isOnGround();
    }

    /**
     * Check if entity is jumping.
     * 
     * @return <code>true</code> if jumping, <code>false</code> else.
     */
    public boolean isJumping()
    {
        return !isOnGround() && getLocationY() > getLocationOldY();
    }

    /**
     * Check if entity is falling.
     * 
     * @return <code>true</code> if falling, <code>false</code> else.
     */
    public boolean isFalling()
    {
        return !isOnGround() && getLocationY() < getLocationOldY();
    }

    /**
     * Check if entity is on ground.
     * 
     * @return <code>true</code> if on ground, <code>false</code> else.
     */
    public boolean isOnGround()
    {
        return status.getCollision() == EntityCollision.GROUND;
    }

    /**
     * Check vertical axis.
     * 
     * @param offset The offset value.
     */
    protected void checkCollisionVertical(int offset)
    {
        final Tile tile = collisionCheck(offset, 0, TileCollision.COLLISION_VERTICAL);
        if (tile != null)
        {
            final Double y = tile.getCollisionY(this);
            if (applyVerticalCollision(y))
            {
                resetGravity();
                jumpForce.setForce(Force.ZERO);
                status.setCollision(EntityCollision.GROUND);
            }
        }
    }

    /**
     * Check horizontal axis.
     * 
     * @param offset The offset value.
     */
    protected void checkCollisionHorizontal(int offset)
    {
        final Tile tile = collisionCheck(offset, 1, TileCollision.COLLISION_HORIZONTAL);
        if (tile != null)
        {
            final Double x = tile.getCollisionX(this);
            if (applyHorizontalCollision(x))
            {
                movement.reset();
            }
        }
    }

    /**
     * Get the horizontal force.
     * 
     * @return The horizontal force.
     */
    protected double getHorizontalForce()
    {
        return movement.getForce().getForceHorizontal();
    }

    /**
     * Check if the specified action is enabled.
     * 
     * @param action The action to check.
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    protected boolean isEnabled(EntityAction action)
    {
        return actions.get(action).booleanValue();
    }

    /**
     * Check the map limit and apply collision if necessary.
     */
    private void checkMapLimit()
    {
        final int limitLeft = 0;
        if (getLocationX() < limitLeft)
        {
            teleportX(limitLeft);
            movement.reset();
        }
        final int limitRight = map.getWidthInTile() * map.getTileWidth();
        if (getLocationX() > limitRight)
        {
            teleportX(limitRight);
            movement.reset();
        }
    }

    /**
     * Adjust gravity in case of slope (to stay on collision).
     */
    private void gravitySlopeAdjuster()
    {
        final int h = (int) Math.ceil(getHorizontalForce());
        final Tile nextTile = map.getTile(this, h, 0);
        final double v;
        if (isOnGround() && nextTile != null && nextTile.isGroup(TileCollisionGroup.SLOPE))
        {
            v = -Math.abs(h) * 1.5;
        }
        else
        {
            v = 0.0;
        }
        extraGravityForce.setForce(0.0, v);
    }

    @Override
    protected Force[] getForces()
    {
        return forces;
    }

    /*
     * Entity
     */

    @Override
    public void kill()
    {
        super.kill();
        movement.reset();
    }

    @Override
    public void respawn()
    {
        super.respawn();
        movement.reset();
    }

    @Override
    protected void updateCollisions()
    {
        checkMapLimit();
    }

    @Override
    protected void handleActions(double extrp)
    {
        if (!isDead())
        {
            updateActions();
        }
        super.handleActions(extrp);
    }

    @Override
    protected void handleMovements(double extrp)
    {
        movement.update(extrp);
        gravitySlopeAdjuster();
        super.handleMovements(extrp);
    }
}
