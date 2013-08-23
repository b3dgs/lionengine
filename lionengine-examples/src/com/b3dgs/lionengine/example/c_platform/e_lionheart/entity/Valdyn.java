package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Tile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TileCollision;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Valdyn implementation (player).
 */
public final class Valdyn
        extends Entity
{
    /** Divisor for walk speed animation. */
    private static final double ANIM_WALK_SPEED_DIVISOR = 9.0;
    /** The width of the tile extremity. */
    private static final int TILE_EXTREMITY_WIDTH = 2;
    /** The fall time margin (in milli). */
    private static final int FALL_TIME_MARGIN = 100;
    /** Minimum jump time (in milli). */
    private static final int JUMP_TIME_MIN = 100;
    /** Maximum jump time (in milli). */
    private static final int JUMP_TIME_MAX = 200;
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Jump timer (accurate precision of jump force). */
    private final Timing timerJump;
    /** Fall timer (used to determinate the falling state). */
    private final Timing timerFall;
    /** Fallen timer (duration of fallen state, when hit the ground after fall). */
    private final Timing timerFallen;
    /** Fallen duration in milli. */
    private final int fallenDuration;
    /** Sensibility increase value. */
    private final double sensibilityIncrease;
    /** Sensibility decrease value. */
    private final double sensibilityDecrease;
    /** Movement max speed. */
    private final double movementSpeedMax;
    /** Movement smooth. */
    private final double movementSmooth;
    /** Extremity state (used for border state). */
    private boolean extremity;
    /** Jumped state. */
    private boolean jumped;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param camera The camera reference.
     * @param map The map reference.
     * @param desiredFps The desired fps.
     */
    Valdyn(SetupEntityGame setup, CameraPlatform camera, Map map, int desiredFps)
    {
        super(setup, map, desiredFps);
        this.camera = camera;
        timerJump = new Timing();
        timerFall = new Timing();
        timerFallen = new Timing();
        fallenDuration = getDataInteger("fallenDuration", "data");
        movementSpeedMax = getDataDouble("speedMax", "data", "movement");
        movementSmooth = getDataDouble("smooth", "data", "movement");
        sensibilityIncrease = getDataDouble("sensibilityIncrease", "data", "movement");
        sensibilityDecrease = getDataDouble("sensibilityDecrease", "data", "movement");
        extremity = false;
    }

    /**
     * Update the controls.
     * 
     * @param keyboard The keyboard reference.
     */
    public void updateControl(Keyboard keyboard)
    {
        if (!isDead())
        {
            for (final EntityAction action : EntityAction.VALUES)
            {
                actions.put(action, Boolean.valueOf(keyboard.isPressed(action.getKey())));
            }
        }
    }

    /**
     * Update the fall calculation (timer used to know when the entity is truly falling).
     */
    private void updateFall()
    {
        final double diffVertical = getDiffVertical();
        if (!timerFall.isStarted())
        {
            if (diffVertical < 0.0)
            {
                timerFall.start();
            }
        }
        else if (diffVertical >= 0.0)
        {
            timerFall.stop();
        }
    }

    /**
     * Update the fallen calculation (timer used to know the fallen time duration).
     */
    private void updateFallen()
    {
        if (!timerFallen.isStarted())
        {
            if (status.collisionChangedFromTo(EntityCollision.NONE, EntityCollision.GROUND))
            {
                timerFallen.start();
            }
        }
        else if (timerFallen.elapsed(fallenDuration))
        {
            timerFallen.stop();
        }
    }

    /**
     * Update the movement speed on a slope.
     * 
     * @param speed The current movement speed.
     * @param sensibility The current movement sensibility.
     * @return The new movement speed.
     */
    private double updateMovementSlope(double speed, double sensibility)
    {
        double newSpeed = speed;
        if (isOnGround())
        {
            if (isGoingDown())
            {
                newSpeed *= 1.3;
            }
            else if (isGoingUp())
            {
                newSpeed *= 0.75;
            }
        }

        final double forceH = movement.getForce().getForceHorizontal();
        if (!isGoingUp()
                && (forceH > movementSpeedMax && isEnabled(EntityAction.MOVE_RIGHT) || forceH < -movementSpeedMax
                        && isEnabled(EntityAction.MOVE_LEFT)))
        {
            movement.setVelocity(0.02);
            movement.setSensibility(sensibility / 4);
        }
        else
        {
            movement.setSensibility(sensibility);
            movement.setVelocity(movementSmooth);
        }
        return newSpeed;
    }

    /**
     * Update the jump with a sufficient accuracy.
     */
    private void updateJump()
    {
        if (isEnabled(EntityAction.JUMP) && !jumped)
        {
            if (!timerJump.isStarted())
            {
                timerJump.start();
            }
            if (canJump())
            {
                jumpForce.setForce(0.0, jumpHeightMax);
                status.setCollision(EntityCollision.NONE);
            }
        }
        else
        {
            jumped = true;
            if (timerJump.elapsed(Valdyn.JUMP_TIME_MIN))
            {
                final double factor = (timerJump.elapsed() / (double) Valdyn.JUMP_TIME_MAX);
                jumpForce.setForce(0.0, UtilityMath.fixBetween(jumpHeightMax * factor, 0.0, jumpHeightMax));
                timerJump.stop();
            }
        }
        if (isOnGround())
        {
            jumped = false;
        }
    }

    /**
     * Check the vertical collision in border case.
     * 
     * @param offsetX The horizontal offset.
     */
    private void checkCollisionVerticalBorder(int offsetX)
    {
        final Tile tile = map.getTile(this, offsetX, 0);
        if (tile != null && tile.isBorder())
        {
            checkCollisionVertical(offsetX);
        }
    }

    /**
     * Check if the entity is over a left extremity.
     * 
     * @return <code>true</code> if over a left extremity, <code>false</code> else.
     */
    private boolean isOnLeftExtremity()
    {
        final Tile tile = map.getTile(this, 0, 0);
        if (tile != null && tile.isBorder())
        {
            final int tx = getLocationIntX() - tile.getX() - Valdyn.TILE_EXTREMITY_WIDTH;
            final Tile left = map.getTile(tile.getX() / map.getTileWidth() - 1, tile.getY() / map.getTileHeight());
            final boolean noLeft = left == null || TileCollision.NONE == left.getCollision();
            final boolean extremity = noLeft && tx <= Valdyn.TILE_EXTREMITY_WIDTH;
            return extremity;
        }
        return false;
    }

    /**
     * Check if the entity is over a right extremity.
     * 
     * @return <code>true</code> if over a right extremity, <code>false</code> else.
     */
    private boolean isOnRightExtremity()
    {
        final Tile tile = map.getTile(this, 0, 0);
        if (tile != null && tile.isBorder())
        {
            final int tx = getLocationIntX() - tile.getX() + Valdyn.TILE_EXTREMITY_WIDTH;
            final Tile right = map.getTile(tile.getX() / map.getTileWidth() + 1, tile.getY() / map.getTileHeight());
            final boolean noRight = right == null || TileCollision.NONE == right.getCollision();
            final boolean extremity = noRight && tx >= map.getTileWidth() - Valdyn.TILE_EXTREMITY_WIDTH;
            return extremity;
        }
        return false;
    }

    /*
     * Entity
     */

    @Override
    public void respawn()
    {
        super.respawn();
        timerJump.stop();
        jumpForce.setForce(Force.ZERO);
        teleport(512, 55);
        camera.resetInterval(this);
    }

    @Override
    public void hitBy(Entity entity)
    {
        if (!isDead())
        {
            kill();
        }
    }

    @Override
    public void hitThat(Entity entity)
    {
        if (!isJumping())
        {
            jumpForce.setForce(0.0, jumpHeightMax * 1.5);
        }
    }

    @Override
    public boolean canJump()
    {
        return super.canJump() || timerJump.isStarted() && !timerJump.elapsed(Valdyn.JUMP_TIME_MAX);
    }

    @Override
    public boolean isFalling()
    {
        return super.isFalling() && timerFall.elapsed(Valdyn.FALL_TIME_MARGIN);
    }

    @Override
    protected void updateActions()
    {
        final double sensibility;
        double speed;
        if (isEnabled(EntityAction.MOVE_RIGHT) && !isEnabled(EntityAction.MOVE_LEFT))
        {
            speed = movementSpeedMax;
            sensibility = sensibilityIncrease;
        }
        else if (isEnabled(EntityAction.MOVE_LEFT) && !isEnabled(EntityAction.MOVE_RIGHT))
        {
            speed = -movementSpeedMax;
            sensibility = sensibilityIncrease;
        }
        else
        {
            speed = 0.0;
            sensibility = sensibilityDecrease;
        }
        movement.setSensibility(sensibility);

        speed = updateMovementSlope(speed, sensibility);
        movement.setForceToReach(speed, 0.0);

        updateJump();
    }

    @Override
    protected void updateStates()
    {
        final double diffHorizontal = getDiffHorizontal();
        if (!extremity && !isDead() && diffHorizontal != 0.0)
        {
            mirror(diffHorizontal < 0.0);
        }
        final boolean mirror = getMirror();

        if (isFalling())
        {
            status.setState(EntityState.FALL);
        }
        else if (isJumping())
        {
            status.setState(EntityState.JUMP);
        }
        else if (timerFallen.isStarted())
        {
            status.setState(EntityState.FALLEN);
        }
        else if (isOnGround())
        {
            if (mirror && isEnabled(EntityAction.MOVE_RIGHT) && diffHorizontal < 0.0 || !mirror
                    && isEnabled(EntityAction.MOVE_LEFT) && diffHorizontal > 0.0)
            {
                status.setState(EntityState.TURN);
            }
            else if (diffHorizontal != 0.0)
            {
                status.setState(EntityState.WALK);
            }
            else if (extremity)
            {
                status.setState(EntityState.BORDER);
            }
            else
            {
                status.setState(EntityState.IDLE);
            }
        }
        if (isDead())
        {
            if (stepDie == 0 || getLocationY() < 0)
            {
                status.setState(EntityState.DIE);
            }
            else
            {
                status.setState(EntityState.DEAD);
            }
        }
        updateFall();
        updateFallen();
    }

    @Override
    protected void updateDead()
    {
        if (getLocationY() < 0)
        {
            movement.reset();
            jumpForce.setForce(0.0, -0.3);
            stepDie = 1;
            resetGravity();
        }
        if (timerDie.elapsed(500))
        {
            resetGravity();
            if (stepDie == 1)
            {
                if (timerDie.elapsed(1500))
                {
                    respawn();
                }
            }
        }
    }

    @Override
    protected void updateCollisions()
    {
        extremity = false;
        if (getLocationY() < getLocationOldY() && timerFall.elapsed(50))
        {
            status.setCollision(EntityCollision.NONE);
        }

        // Vertical collision
        if (getDiffVertical() < 0 || isOnGround())
        {
            checkCollisionVertical(0);
            checkCollisionVerticalBorder(Valdyn.TILE_EXTREMITY_WIDTH); // Left leg;
            if (isOnLeftExtremity())
            {
                mirror(true);
                extremity = true;
            }
            checkCollisionVerticalBorder(-Valdyn.TILE_EXTREMITY_WIDTH); // Right leg
            if (isOnRightExtremity())
            {
                mirror(false);
                extremity = true;
            }
        }

        // Kill when fall down
        if (getLocationY() < 0)
        {
            kill();
            setLocationY(0);
        }
    }

    @Override
    protected void updateAnimations()
    {
        final EntityState state = status.getState();
        if (state == EntityState.WALK || state == EntityState.TURN)
        {
            final double speed = Math.abs(getHorizontalForce()) / Valdyn.ANIM_WALK_SPEED_DIVISOR;
            setAnimSpeed(speed);
        }
    }
}
