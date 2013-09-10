package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Tile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TypeTileCollision;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TypeTileCollisionGroup;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Movement;

/**
 * Abstract entity base implementation designed to move around the map.
 */
public abstract class EntityMover
        extends Entity
{
    /** Move left side. */
    private static final int MOVE_LEFT = -1;
    /** Move right side. */
    private static final int MOVE_RIGHT = 1;
    /** Entity actions. */
    protected final EnumMap<TypeEntityAction, Boolean> actions;
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
    /** Movement enabled used in patrol. */
    private final Set<TypeEntityMovement> enableMovement;
    /** Movement max speed. */
    protected double movementSpeedMax;
    /** Patrol current movement side. */
    protected int side;
    /** Patrol minimum position. */
    protected int posMin;
    /** Patrol maximum position. */
    protected int posMax;
    /** Patrol existence flag. */
    protected boolean hasPatrol;
    /** Movement type. */
    private TypeEntityMovement movementType;
    /** First move flag. */
    private int firstMove;
    /** Movement speed. */
    private int moveSpeed;
    /** Patrol left value. */
    private int patrolLeft;
    /** Patrol right value. */
    private int patrolRight;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param type The entity type.
     */
    public EntityMover(Context context, TypeEntity type)
    {
        super(context, type);
        movement = new Movement();
        actions = new EnumMap<>(TypeEntityAction.class);
        extraGravityForce = new Force();
        jumpForce = new Force();
        jumpHeightMax = getDataDouble("heightMax", "data", "jump");
        movementType = TypeEntityMovement.NONE;
        enableMovement = new HashSet<>(4);
        forces = new Force[]
        {
                jumpForce, extraGravityForce, movement.getForce()
        };
        setMass(getDataDouble("mass", "data"));
        setGravityMax(getDataDouble("gravityMax", "data"));
        enableMovement(TypeEntityMovement.NONE);
        enableMovement(TypeEntityMovement.HORIZONTAL);
    }

    /**
     * Update the actions.
     * 
     * @see TypeEntityAction
     */
    protected abstract void updateActions();

    /**
     * Check vertical axis.
     * 
     * @param y The y location.
     */
    public void checkCollisionVertical(Double y)
    {
        if (applyVerticalCollision(y))
        {
            resetGravity();
            jumpForce.setForce(Force.ZERO);
            status.setCollision(TypeEntityCollision.GROUND);
        }
    }

    /**
     * Set the movement type.
     * 
     * @param movement The movement type.
     */
    public void setMovementType(TypeEntityMovement movement)
    {
        movementType = movement;
    }

    /**
     * Set the first move.
     * 
     * @param firstMove The first move.
     */
    public void setFirstMove(int firstMove)
    {
        this.firstMove = firstMove;
    }

    /**
     * Set the movement speed.
     * 
     * @param speed The movement speed.
     */
    public void setMoveSpeed(int speed)
    {
        moveSpeed = speed;
    }

    /**
     * Set the left patrol.
     * 
     * @param left The left patrol.
     */
    public void setPatrolLeft(int left)
    {
        patrolLeft = left;
    }

    /**
     * Set the right patrol.
     * 
     * @param right The right patrol.
     */
    public void setPatrolRight(int right)
    {
        patrolRight = right;
    }

    /**
     * Get the movement type.
     * 
     * @return The movement type.
     */
    public TypeEntityMovement getMovementType()
    {
        return movementType;
    }

    /**
     * Get the first move.
     * 
     * @return The first move.
     */
    public int getFirstMove()
    {
        return firstMove;
    }

    /**
     * Get the movement speed.
     * 
     * @return The movement speed.
     */
    public int getMoveSpeed()
    {
        return moveSpeed;
    }

    /**
     * Get the left patrol.
     * 
     * @return The left patrol.
     */
    public int getPatrolLeft()
    {
        return patrolLeft;
    }

    /**
     * Get the right patrol.
     * 
     * @return The right patrol.
     */
    public int getPatrolRight()
    {
        return patrolRight;
    }

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
     * Check if movement is enabled.
     * 
     * @param type The movement type.
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    public boolean isMovementEnabled(TypeEntityMovement type)
    {
        return enableMovement.contains(type);
    }

    /**
     * Check if patrol is enabled.
     * 
     * @return <code>true</code> if patrol enabled, <code>false</code> else.
     */
    public boolean isPatrolEnabled()
    {
        return movementType != TypeEntityMovement.NONE;
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
        return status.getCollision() == TypeEntityCollision.GROUND;
    }

    /**
     * Check vertical axis.
     * 
     * @param tile The tile collision.
     */
    protected void checkCollisionVertical(Tile tile)
    {
        if (tile != null)
        {
            final Double y = tile.getCollisionY(this);
            checkCollisionVertical(y);
        }
    }

    /**
     * Check horizontal axis.
     * 
     * @param offset The offset value.
     */
    protected void checkCollisionHorizontal(int offset)
    {
        collisionCheck(offset, 1);
        final Tile tile = map.getFirstTileHit(this, TypeTileCollision.COLLISION_HORIZONTAL);
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
    protected boolean isEnabled(TypeEntityAction action)
    {
        return actions.get(action).booleanValue();
    }

    /**
     * Enable a movement.
     * 
     * @param type The movement to enable.
     */
    protected void enableMovement(TypeEntityMovement type)
    {
        enableMovement.add(type);
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
        if (isOnGround() && nextTile != null && nextTile.isGroup(TypeTileCollisionGroup.SLOPE))
        {
            v = -Math.abs(h) * 1.5;
        }
        else
        {
            v = 0.0;
        }
        extraGravityForce.setForce(0.0, v);
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
    public void save(FileWriting file) throws IOException
    {
        super.save(file);
        file.writeByte((byte) movementType.getIndex());
        if (movementType != TypeEntityMovement.NONE)
        {
            file.writeByte((byte) getMoveSpeed());
            file.writeByte((byte) getFirstMove());
            file.writeByte((byte) getPatrolLeft());
            file.writeByte((byte) getPatrolRight());
        }
    }

    @Override
    public void load(FileReading file) throws IOException
    {
        super.load(file);
        setMovementType(TypeEntityMovement.get(file.readByte()));
        final TypeEntityMovement movementType = getMovementType();
        if (movementType != TypeEntityMovement.NONE)
        {
            setMoveSpeed(file.readByte());
            setFirstMove(file.readByte());
            setPatrolLeft(file.readByte());
            setPatrolRight(file.readByte());

            hasPatrol = getPatrolLeft() != 0 || getPatrolRight() != 0;
            movementSpeedMax = getMoveSpeed() / 5.0;

            // Set side
            if (firstMove == 0)
            {
                side = EntityMover.MOVE_LEFT;
            }
            else if (firstMove == 1)
            {
                side = EntityMover.MOVE_RIGHT;
            }

            // Set position interval
            if (movementType == TypeEntityMovement.HORIZONTAL)
            {
                posMin = getLocationIntX() - getPatrolLeft() * Map.TILE_WIDTH;
                posMax = getLocationIntX() + (getPatrolRight() - 1) * Map.TILE_WIDTH;
                movement.getForce().setForce(movementSpeedMax * side, 0.0);
            }
            else if (movementType == TypeEntityMovement.VERTICAL)
            {
                posMin = getLocationIntY() - getPatrolLeft() * Map.TILE_WIDTH;
                posMax = getLocationIntY() + getPatrolRight() * Map.TILE_WIDTH;
                movement.getForce().setForce(0.0, movementSpeedMax * side);
            }

            // Locate at the opposite of the direction
            final int offsetX = (int) Math.ceil(movement.getForce().getForceHorizontal());
            if (side == EntityMover.MOVE_LEFT)
            {
                mirror(true);
                teleport(posMax - offsetX - 1, getLocationIntY());
            }
            else if (side == EntityMover.MOVE_RIGHT)
            {
                teleport(posMin + offsetX, getLocationIntY());
            }

            // Move straight on
            if (posMin == posMax)
            {
                posMin = -1;
                posMax = Integer.MAX_VALUE;
            }
        }
    }

    @Override
    protected void updateCollisions()
    {
        checkMapLimit();
        // Vertical collision
        if (getDiffVertical() < 0 || isOnGround())
        {
            checkCollisionVertical(map.getFirstTileHit(this, TypeTileCollision.COLLISION_VERTICAL));
        }
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

    @Override
    protected Force[] getForces()
    {
        return forces;
    }
}
