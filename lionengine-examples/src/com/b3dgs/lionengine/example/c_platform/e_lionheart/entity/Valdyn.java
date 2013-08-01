package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.EntityCollision;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.EntityState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Tile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TileCollision;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.input.Keyboard;

/**
 * Valdyn entity implementation.
 */
public class Valdyn
        extends Entity
{
    /** Divisor for walk speed animation. */
    private static final double ANIM_WALK_SPEED_DIVISOR = 9.0;
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Fall timer (timer used to know when its the falling state). */
    private final Timing timerFall;
    /** Fallen timer. */
    private final Timing timerFallen;
    /** Fallen duration in milli. */
    private final int fallenDuration;
    /** Extremity state. */
    private boolean extremity;

    /**
     * Standard constructor.
     * 
     * @param setup The setup reference.
     * @param map The map reference.
     * @param camera The camera reference.
     * @param desiredFps The desired fps.
     */
    public Valdyn(SetupEntityGame setup, Map map, CameraPlatform camera, int desiredFps)
    {
        super(setup, map, desiredFps);
        this.camera = camera;
        fallenDuration = getDataInteger("fallenDuration", "data");
        timerFall = new Timing();
        timerFallen = new Timing();
        extremity = false;
    }

    /**
     * Update the mario controls.
     * 
     * @param keyboard The keyboard reference.
     */
    public void updateControl(Keyboard keyboard)
    {
        if (!isDead())
        {
            right = keyboard.isPressed(Keyboard.RIGHT);
            left = keyboard.isPressed(Keyboard.LEFT);
            up = keyboard.isPressed(Keyboard.UP);
        }
    }

    /**
     * Respawn player.
     */
    public void respawn()
    {
        setDead(false);
        resetGravity();
        resetMovementSpeed();
        mirror(false);
        updateMirror();
        state = EntityState.IDLE;
        coll = EntityCollision.GROUND;
        collOld = coll;
        setLocation(512, 55);
        camera.resetInterval(this);
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
            if (collOld == EntityCollision.NONE && coll == EntityCollision.GROUND)
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
     * Check if the entity is over a left extremity.
     * 
     * @return <code>true</code> if over a left extremity, <code>false</code> else.
     */
    private boolean isOnLeftExtremity()
    {
        final Tile tile = map.getTile(this, 0, 0);
        if (tile != null && tile.isBorder())
        {
            final int tx = getLocationIntX() - tile.getX() - Map.TILE_EXTREMITY_WIDTH;
            final Tile left = map.getTile(tile.getX() / map.getTileWidth() - 1, tile.getY() / map.getTileHeight());
            final boolean noLeft = left == null || TileCollision.NONE == left.getCollision();
            final boolean extremity = noLeft && tx <= Map.TILE_EXTREMITY_WIDTH;
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
            final int tx = getLocationIntX() - tile.getX() + Map.TILE_EXTREMITY_WIDTH;
            final Tile right = map.getTile(tile.getX() / map.getTileWidth() + 1, tile.getY() / map.getTileHeight());
            final boolean noRight = right == null || TileCollision.NONE == right.getCollision();
            final boolean extremity = noRight && tx >= map.getTileWidth() - Map.TILE_EXTREMITY_WIDTH;
            return extremity;
        }
        return false;
    }

    /*
     * Entity
     */

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
    public boolean isFalling()
    {
        return super.isFalling() && timerFall.elapsed(100);
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
            state = EntityState.FALL;
        }
        else if (isJumping())
        {
            state = EntityState.JUMP;
        }
        else if (timerFallen.isStarted())
        {
            state = EntityState.FALLEN;
        }
        else if (isOnGround())
        {
            if (mirror && right && diffHorizontal < 0.0 || !mirror && left && diffHorizontal > 0.0)
            {
                state = EntityState.TURN;
            }
            else if (diffHorizontal != 0.0)
            {
                state = EntityState.WALK;
            }
            else if (extremity)
            {
                state = EntityState.BORDER;
            }
            else
            {
                state = EntityState.IDLE;
            }
        }
        if (isDead())
        {
            if (stepDie == 0 || getLocationY() < 0)
            {
                state = EntityState.DIE;
            }
            else
            {
                state = EntityState.DEAD;
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
            resetMovementSpeed();
            jumpForce.setForce(0.0, -0.3);
            stepDie = 1;
            resetGravity();
        }
        if (timerDie.elapsed(500))
        {
            resetGravity();
            // Respawn
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
        if (getLocationY() < getLocationOldY() && timerFall.elapsed(100))
        {
            coll = EntityCollision.NONE;
        }

        // Vertical collision
        if (getDiffVertical() < 0 || isOnGround())
        {
            checkCollisionVertical(0);
            checkCollisionVerticalBorder(Map.TILE_EXTREMITY_WIDTH); // Left leg;
            if (isOnLeftExtremity())
            {
                mirror(true);
                extremity = true;
            }
            checkCollisionVerticalBorder(-Map.TILE_EXTREMITY_WIDTH); // Right leg
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

    @Override
    protected void updateAnimations()
    {
        if (state == EntityState.WALK || state == EntityState.TURN)
        {
            final double speed = Math.abs(getHorizontalForce()) / Valdyn.ANIM_WALK_SPEED_DIVISOR;
            setAnimSpeed(speed);
        }
    }
}
