package com.b3dgs.lionengine.example.f_network;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.network.message.NetworkMessage;

/**
 * Implementation of our controllable entity.
 */
class Mario
        extends Entity
{
    /** Animation turn. */
    private final Animation animTurn;
    /** Animation jump. */
    private final Animation animJump;
    /** Dead timer. */
    private final Timing timerDie;
    /** Dead step. */
    private int stepDie;
    /** Die location. */
    private double locationDie;
    /** Name. */
    private String name;

    /**
     * Standard constructor.
     * 
     * @param setup setup reference.
     * @param map The map reference.
     * @param desiredFps desired fps.
     * @param server <code>true</code> if is server, <code>false</code> if client.
     */
    public Mario(SetupEntityGame setup, Map map, int desiredFps, boolean server)
    {
        super(setup, TypeEntity.mario, map, desiredFps, server);
        animTurn = getAnimation("turn");
        animJump = getAnimation("jump");
        timerDie = new Timing();
        jumpForceValue = 8.0;
        movementSpeedValue = 3.0;
    }

    /**
     * Set the displayed name.
     * 
     * @param name The name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the displayed name.
     * 
     * @return The name.
     */
    public String getName()
    {
        return name;
    }

    @Override
    protected void handleMovements(double extrp)
    {
        super.handleMovements(extrp);
        if (!dead)
        {
            // Smooth walking speed, but quick stop
            if (right || left)
            {
                movementForce.reachForce(extrp, movementForceDest, 0.3, 0.1);
            }
            else
            {
                movementForce.reachForce(extrp, movementForceDest, 0.5, 0.1);
            }
        }
        // Die
        if (dead)
        {
            if (timerDie.elapsed(500))
            {
                // Die effect
                if (stepDie == 0)
                {
                    jumpForce.setForce(0.0, jumpForceValue * 1.5);
                    stepDie = 1;
                }
                // Respawn
                if (stepDie == 1 && timerDie.elapsed(2000))
                {
                    doRespawn();
                }
            }
            // Lock mario
            else
            {
                resetGravity();
                setLocationY(locationDie);
            }
        }
    }

    @Override
    protected void selectAnimCur(EntityState state)
    {
        super.selectAnimCur(state);
        switch (state)
        {
            case TURN:
                animCur = animTurn;
                break;
            case JUMP:
                animCur = animJump;
                break;
            case WALK:
                setAnimSpeed(Math.abs(getDiffHorizontal() / 12.0));
                break;
            default:
                break;
        }
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        if (!dead)
        {
            super.handleCollisions(extrp);

            // Kill when fall down
            if (getLocationY() < 0)
            {
                doKill();
            }
        }
        // Send correct location if moving, or just idle
        if (server)
        {
            // Network location timer
            if (networkLocation.elapsed(250) && (state == EntityState.WALK || state == EntityState.IDLE))
            {
                final MessageEntity message = new MessageEntity(getClientId());
                message.addAction(MessageEntityElement.LOCATION_X, getLocationIntX());
                message.addAction(MessageEntityElement.LOCATION_Y, getLocationIntY());
                addNetworkMessage(message);
                networkLocation.start();
            }
        }
    }

    @Override
    public void onHurtBy(EntityGame entity, int damages)
    {
        if (!dead)
        {
            doKill();
        }
    }

    @Override
    public void onHitThat(Entity entity)
    {
        if (!isJumping())
        {
            final MessageEntity message = new MessageEntity(getClientId());
            message.addAction(MessageEntityElement.LOCATION_X, getLocationIntX());
            message.addAction(MessageEntityElement.LOCATION_Y, getLocationIntY());
            message.addAction(MessageEntityElement.JUMP, true);
            addNetworkMessage(message);
            doJump();
        }
    }

    /**
     * Kill mario.
     */
    public void doKill()
    {
        dead = true;
        resetMovementSpeed();
        locationDie = getLocationY();
        stepDie = 0;
        timerDie.start();
    }

    /**
     * Respawn mario.
     */
    public void doRespawn()
    {
        mirror(false);
        setLocation(80, 32);
        dead = false;
    }

    /**
     * Jump mario.
     */
    public void doJump()
    {
        jumpForce.setForce(0.0, jumpForceValue / 1.5);
        resetGravity();
    }

    @Override
    public void applyMessage(NetworkMessage message)
    {
        if (!(message instanceof MessageEntity))
        {
            return;
        }
        final MessageEntity msg = (MessageEntity) message;
        if (message.getClientId() == getClientId().byteValue())
        {
            super.applyMessage(message);
            if (msg.hasAction(MessageEntityElement.JUMP))
            {
                doJump();
            }
        }
    }
}
