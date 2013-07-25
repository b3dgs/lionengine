package com.b3dgs.lionengine.example.f_network;

import java.util.Collection;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.platform.EntityPlatform;
import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.purview.Networkable;
import com.b3dgs.lionengine.network.purview.NetworkableModel;

/**
 * Abstract entity base implementation.
 */
public abstract class Entity
        extends EntityPlatform<TileCollision, Tile>
        implements Networkable
{
    /** Zero force instance. */
    protected static final Force ZERO_FORCE = new Force(0.0, 0.0);
    /** Desired fps value. */
    protected final int desiredFps;
    /** Jump force. */
    protected double jumpForceValue;
    /** Movement max speed. */
    protected double movementSpeedValue;
    /** Movement force force. */
    protected final Force movementForce;
    /** Movement force destination force. */
    protected final Force movementForceDest;
    /** Movement jump force. */
    protected final Force jumpForce;
    /** Extra time for jump before fall. */
    protected final Timing timerExtraJump;
    /** Network model. */
    private final NetworkableModel networkableModel;
    /** Send correct location timer. */
    protected final Timing networkLocation;
    /** Animation idle. */
    private final Animation animIdle;
    /** Animation walk. */
    private final Animation animWalk;
    /** Animation jump. */
    private final Animation animDie;
    /** Entity type. */
    private final TypeEntity type;
    /** Client flag. */
    protected final boolean server;
    /** Key right state. */
    protected boolean right;
    /** Key left state. */
    protected boolean left;
    /** Key up state. */
    protected boolean up;
    /** Current animation. */
    protected Animation animCur;
    /** Entity state. */
    protected EntityState state;
    /** Old state. */
    protected EntityState stateOld;
    /** Collision state. */
    protected EntityCollision coll;
    /** Dead flag. */
    protected boolean dead;

    /**
     * Standard constructor.
     * 
     * @param setup The setup reference.
     * @param type The entity type.
     * @param map The map reference.
     * @param desiredFps The desired fps.
     * @param server <code>true</code> if is server, <code>false</code> if client.
     */
    public Entity(SetupEntityGame setup, TypeEntity type, Map map, int desiredFps, boolean server)
    {
        super(setup, map);
        this.type = type;
        animIdle = getAnimation("idle");
        animWalk = getAnimation("walk");
        animDie = getAnimation("die");
        this.desiredFps = desiredFps;
        this.server = server;
        networkableModel = new NetworkableModel();
        movementForce = new Force();
        movementForceDest = new Force();
        jumpForce = new Force();
        timerExtraJump = new Timing();
        networkLocation = new Timing();
        networkLocation.start();
        state = EntityState.IDLE;
        setSize(sprite.getFrameWidth(), sprite.getFrameHeight());
        setMass(2.5);
        setFrameOffsets(-getWidth() / 2, getHeight());
    }

    /**
     * Get the entity type.
     * 
     * @return The entity type.
     */
    public TypeEntity getType()
    {
        return type;
    }

    @Override
    protected void handleActions(double extrp)
    {
        // Update movement key
        movementForceDest.setForce(0.0, 0.0);
        if (right)
        {
            movementForceDest.setForce(movementSpeedValue, 0.0);
        }
        if (left)
        {
            movementForceDest.setForce(-movementSpeedValue, 0.0);
        }

        // Check mirror
        if (getDiffHorizontal() != 0.0)
        {
            mirror(getDiffHorizontal() < 0.0);
        }

        // Update jump key
        if (up && canJump())
        {
            jumpForce.setForce(0.0, jumpForceValue);
            resetGravity();
            coll = EntityCollision.NONE;
            timerExtraJump.stop();
        }

        // Update states
        stateOld = state;
        if (!isOnGround())
        {
            state = EntityState.JUMP;
        }
        else if (getMirror() && right && getDiffHorizontal() < 0.0)
        {
            state = EntityState.TURN;
        }
        else if (!getMirror() && left && getDiffHorizontal() > 0.0)
        {
            state = EntityState.TURN;
        }
        else if (getDiffHorizontal() != 0.0)
        {
            state = EntityState.WALK;
        }
        else
        {
            state = EntityState.IDLE;
        }
        if (dead)
        {
            state = EntityState.DEAD;
        }
    }

    @Override
    protected void handleMovements(double extrp)
    {
        updateGravity(extrp, desiredFps, movementForce, jumpForce);
        updateMirror();
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        // Vertical checks
        checkCollisionVertical(collisionCheck(-6, 0, TileCollision.GROUND, TileCollision.BLOCK));
        checkCollisionVertical(collisionCheck(0, 0, TileCollision.GROUND, TileCollision.BLOCK));
        checkCollisionVertical(collisionCheck(6, 0, TileCollision.GROUND, TileCollision.BLOCK));

        // Horizontal checks
        if (getDiffHorizontal() < 0.0)
        {
            checkCollisionHorizontal(
                    collisionCheck(-6, 0, TileCollision.WALL, TileCollision.GROUND, TileCollision.BLOCK), -6);
        }
        if (getDiffHorizontal() > 0.0)
        {
            checkCollisionHorizontal(
                    collisionCheck(6, 0, TileCollision.WALL, TileCollision.GROUND, TileCollision.BLOCK), 6);
        }

        // Entity collide box
        updateCollision(0, 0, getWidth(), getHeight());
    }

    /**
     * Check vertical axis.
     * 
     * @param tile The tile to check.
     */
    private void checkCollisionVertical(Tile tile)
    {
        if (tile != null)
        {
            final Integer c = tile.getCollisionLocationY(getLocationOldY(), getLocationY(), getLocationX());
            if (c != null)
            {
                resetGravity();
                jumpForce.setForce(Entity.ZERO_FORCE);
                applyVerticalCollision(c);
                coll = EntityCollision.GROUND;
                // Start timer to allow entity to have an extra jump area before falling
                timerExtraJump.start();
            }
        }
    }

    /**
     * Check horizontal axis.
     * 
     * @param tile The tile to check.
     * @param offset The offset value.
     */
    private void checkCollisionHorizontal(Tile tile, int offset)
    {
        if (tile != null)
        {
            final Integer c = tile.getCollisionLocationX(getLocationOldX(), getLocationX(), getLocationY(), offset);
            if (c != null)
            {
                stopMovement();
                applyHorizontalCollision(c);
                onHorizontalCollision();
            }
        }
    }

    @Override
    protected void handleAnimations(double extrp)
    {
        // Assign an animation for each state
        selectAnimCur(state);
        // Play the assigned animation
        if (animCur != null && stateOld != state)
        {
            play(animCur);
        }
        updateAnimation(extrp);
    }

    /**
     * Select the animation from the state.
     * 
     * @param state The current state
     */
    protected void selectAnimCur(EntityState state)
    {
        switch (state)
        {
            case IDLE:
                animCur = animIdle;
                break;
            case WALK:
                animCur = animWalk;
                break;
            case DEAD:
                animCur = animDie;
                break;
            default:
                break;
        }
    }

    /**
     * Called when hurt.
     * 
     * @param entity Entity that hurt this.
     * @param damages Hurt damages.
     */
    public abstract void onHurtBy(EntityGame entity, int damages);

    /**
     * Check if hero can jump.
     * 
     * @return <code>true</code> if can jump, <code>false</code> else.
     */
    public boolean canJump()
    {
        return isOnGround();
    }

    /**
     * Check if hero is jumping.
     * 
     * @return <code>true</code> if jumping, <code>false</code> else.
     */
    public boolean isJumping()
    {
        return getLocationY() > getLocationOldY();
    }

    /**
     * Check if hero is falling.
     * 
     * @return <code>true</code> if falling, <code>false</code> else.
     */
    public boolean isFalling()
    {
        return getLocationY() < getLocationOldY();
    }

    /**
     * Check if hero is on ground.
     * 
     * @return <code>true</code> if on ground, <code>false</code> else.
     */
    public boolean isOnGround()
    {
        return coll == EntityCollision.GROUND && !isFalling() && !isJumping();
    }

    /**
     * Check if entity is dead.
     * 
     * @return <code>true</code> if dead, <code>false</code> else.
     */
    public boolean isDead()
    {
        return dead;
    }

    /**
     * Called when hit this entity.
     * 
     * @param entity The entity hit.
     */
    public abstract void onHitThat(Entity entity);

    /**
     * Set to zero movement speed force.
     */
    protected void stopMovement()
    {
        movementForce.setForce(Entity.ZERO_FORCE);
        movementForceDest.setForce(Entity.ZERO_FORCE);
    }

    /**
     * Called when an horizontal collision occurred.
     */
    protected void onHorizontalCollision()
    {
        // Nothing to do by default
    }

    /*
     * Networkable
     */

    @Override
    public void applyMessage(NetworkMessage message)
    {
        final MessageEntity msg = (MessageEntity) message;
        // States
        if (msg.hasAction(MessageEntityElement.RIGHT))
        {
            right = msg.getActionBoolean(MessageEntityElement.RIGHT);
        }
        if (msg.hasAction(MessageEntityElement.LEFT))
        {
            left = msg.getActionBoolean(MessageEntityElement.LEFT);
        }
        if (msg.hasAction(MessageEntityElement.UP))
        {
            up = msg.getActionBoolean(MessageEntityElement.UP);
        }
        // Location correction
        if (msg.hasAction(MessageEntityElement.LOCATION_X))
        {
            setLocationX(msg.getActionInteger(MessageEntityElement.LOCATION_X));
        }
        if (msg.hasAction(MessageEntityElement.LOCATION_Y))
        {
            setLocationY(msg.getActionInteger(MessageEntityElement.LOCATION_Y));
        }
    }

    @Override
    public void addNetworkMessage(NetworkMessage message)
    {
        networkableModel.addNetworkMessage(message);
    }

    @Override
    public Collection<NetworkMessage> getNetworkMessages()
    {
        return networkableModel.getNetworkMessages();
    }

    @Override
    public void clearNetworkMessages()
    {
        networkableModel.clearNetworkMessages();
    }

    @Override
    public void setClientId(Byte id)
    {
        networkableModel.setClientId(id);
    }

    @Override
    public Byte getClientId()
    {
        return networkableModel.getClientId();
    }
}
