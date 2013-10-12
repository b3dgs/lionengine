/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.example.lionheart.entity.player;

import java.util.Collection;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Keyboard;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.example.lionheart.Level;
import com.b3dgs.lionengine.example.lionheart.Sfx;
import com.b3dgs.lionengine.example.lionheart.entity.Entity;
import com.b3dgs.lionengine.example.lionheart.entity.EntityAction;
import com.b3dgs.lionengine.example.lionheart.entity.EntityCollisionTile;
import com.b3dgs.lionengine.example.lionheart.entity.EntityCollisionTileCategory;
import com.b3dgs.lionengine.example.lionheart.entity.EntityMover;
import com.b3dgs.lionengine.example.lionheart.entity.EntityState;
import com.b3dgs.lionengine.example.lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.lionheart.entity.State;
import com.b3dgs.lionengine.example.lionheart.entity.monster.EntityMonster;
import com.b3dgs.lionengine.example.lionheart.landscape.Landscape;
import com.b3dgs.lionengine.example.lionheart.map.Tile;
import com.b3dgs.lionengine.example.lionheart.map.TileCollision;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.CoordTile;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.platform.CollisionTileCategory;
import com.b3dgs.lionengine.game.purview.Collidable;
import com.b3dgs.lionengine.game.purview.model.CollidableModel;

/**
 * Valdyn implementation (player).
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Valdyn
        extends EntityMover
{
    /** The width of the tile extremity. */
    public static final int TILE_EXTREMITY_WIDTH = 3;
    /** The fall time margin (in milli). */
    static final int FALL_TIME_MARGIN = 100;
    /** Hurt effect value (lower is faster). */
    private static final int HURT_EFFECT_FREQ = 5;
    /** Hurt time before effect. */
    private static final int HURT_TIME_BEFORE_EFFECT = 500;
    /** Hurt time max. */
    private static final int HURT_TIME_MAX = 2000;
    /** Divisor for walk speed animation. */
    private static final double ANIM_WALK_SPEED_DIVISOR = 7.0;
    /** Minimum jump time (in milli). */
    private static final int JUMP_TIME_MIN = 100;
    /** Maximum jump time (in milli). */
    private static final int JUMP_TIME_MAX = 200;
    /** Valdyn stats. */
    public final Stats stats;
    /** Valdyn tilt. */
    private final ValdynTilt tilt;
    /** Valdyn attack. */
    private final ValdynAttack attack;
    /** Leg collision with scenery. */
    private final Collidable legCollision;
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Jump timer (accurate precision of jump force). */
    private final Timing timerJump;
    /** Fall timer (used to determinate the falling state). */
    private final Timing timerFall;
    /** Fallen timer (duration of fallen state, when hit the ground after fall). */
    private final Timing timerFallen;
    /** Hurt timer. */
    private final Timing timerHurt;
    /** Fallen duration in milli. */
    private final int fallenDuration;
    /** Sensibility increase value. */
    private final double sensibilityIncrease;
    /** Sensibility decrease value. */
    private final double sensibilityDecrease;
    /** Movement smooth. */
    private final double movementSmooth;
    /** Checkpoints. */
    private Collection<CoordTile> checkpoints;
    /** Last checkpoint. */
    private CoordTile lastCheckpoint;
    /** Landscape reference. */
    private Landscape landscape;
    /** Extremity state (used for border state). */
    private boolean extremity;
    /** Jumped state. */
    private boolean jumped;
    /** Crouch state. */
    private boolean crouch;
    /** Hurt effect counter. */
    private int hurtEffectCounter;
    /** Drowned death. */
    private boolean drownedDeath;
    /** Left key state. */
    private boolean keyLeft;
    /** Right key state. */
    private boolean keyRight;
    /** Up key state. */
    private boolean keyUp;
    /** Down key state. */
    private boolean keyDown;

    /**
     * Constructor.
     * 
     * @param level The level reference.
     */
    public Valdyn(Level level)
    {
        super(level, EntityType.VALDYN);
        camera = level.camera;
        timerJump = new Timing();
        timerFall = new Timing();
        timerFallen = new Timing();
        timerHurt = new Timing();
        fallenDuration = getDataInteger("fallenDuration", "data");
        movementSpeedMax = getDataDouble("speedMax", "data", "movement");
        movementSmooth = getDataDouble("smooth", "data", "movement");
        sensibilityIncrease = getDataDouble("sensibilityIncrease", "data", "movement");
        sensibilityDecrease = getDataDouble("sensibilityDecrease", "data", "movement");
        setFrameOffsets(0, -2);
        loadCollisions(ValdynCollision.values());
        loadAnimations(ValdynState.values());
        legCollision = new CollidableModel(this);
        legCollision.setCollision(getDataCollision("leg"));
        stats = new Stats(this);
        tilt = new ValdynTilt(this, movement, level.map);
        attack = new ValdynAttack(this, movement);
        addCollisionTile(ValdynCollisionTileCategory.LEG_LEFT, -Valdyn.TILE_EXTREMITY_WIDTH, 0);
        addCollisionTile(ValdynCollisionTileCategory.LEG_RIGHT, Valdyn.TILE_EXTREMITY_WIDTH, 0);
        addCollisionTile(ValdynCollisionTileCategory.KNEE_LEFT, -Valdyn.TILE_EXTREMITY_WIDTH * 2, 2);
        addCollisionTile(ValdynCollisionTileCategory.KNEE_RIGHT, Valdyn.TILE_EXTREMITY_WIDTH * 2, 2);
        addCollisionTile(ValdynCollisionTileCategory.HAND_LIANA_LEANING, 0, 57);
        addCollisionTile(ValdynCollisionTileCategory.HAND_LIANA_STEEP, 0, 44);
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
            keyLeft = isEnabled(EntityAction.MOVE_LEFT);
            keyRight = isEnabled(EntityAction.MOVE_RIGHT);
            keyUp = isEnabled(EntityAction.JUMP);
            keyDown = isEnabled(EntityAction.MOVE_DOWN);
        }
        else
        {
            keyLeft = false;
            keyRight = false;
            keyUp = false;
            keyDown = false;
        }
        attack.updateControl(keyboard);
    }

    /**
     * Update the extremity state.
     * 
     * @param mirror The mirror to apply.
     */
    public void updateExtremity(boolean mirror)
    {
        if (!crouch && status.getState() == ValdynState.BORDER)
        {
            mirror(mirror);
        }
        extremity = true;
    }

    /**
     * Respawn valdyn at specified location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void respawn(int x, int y)
    {
        teleport(x, y);
        respawn();
        if (lastCheckpoint == null)
        {
            lastCheckpoint = new CoordTile(x, y);
        }
    }

    /**
     * Set the landscape reference.
     * 
     * @param landscape The landscape reference.
     */
    public void setLandscape(Landscape landscape)
    {
        this.landscape = landscape;
    }

    /**
     * Set the checkpoints list.
     * 
     * @param checkpoints The checkpoints list.
     */
    public void setCheckpoints(Collection<CoordTile> checkpoints)
    {
        this.checkpoints = checkpoints;
    }

    /**
     * Get the collision on leg level.
     * 
     * @return The leg collision.
     */
    public Collidable getCollisionLeg()
    {
        return legCollision;
    }

    /**
     * Get the attack collision.
     * 
     * @return The attack collision.
     */
    public Collidable getCollisionAttack()
    {
        return attack.getCollisionAttack();
    }

    /**
     * Get the death time elapsed.
     * 
     * @return The death time elapsed.
     */
    public long getDeathTime()
    {
        return timerDie.elapsed();
    }

    /**
     * Reset the timer fallen.
     */
    void stopTimerFallen()
    {
        timerFallen.stop();
    }

    /**
     * Reset the jump.
     */
    void resetJump()
    {
        jumpForce.setForce(Force.ZERO);
        jumped = true;
        timerJump.stop();
    }

    /**
     * Set the fall time.
     * 
     * @param time The fall time.
     */
    void setTimerFall(long time)
    {
        timerFall.set(time);
    }

    /**
     * Get a collision data from its key.
     * 
     * @param key The collision key.
     * @return The collision data.
     */
    CollisionData getCollisionData(Enum<?> key)
    {
        return collisions.get(key);
    }

    /**
     * Check if valdyn is sliding.
     * 
     * @return <code>true</code> if sliding, <code>false</code> else.
     */
    boolean isSliding()
    {
        return tilt.getSlide() != null;
    }

    /**
     * Check if valdyn is on liana.
     * 
     * @return <code>true</code> if liana, <code>false</code> else.
     */
    boolean isLiana()
    {
        return tilt.getLiana() != null;
    }

    /**
     * Check if valdyn is attacking.
     * 
     * @return <code>true</code> if attacking, <code>false</code> else.
     */
    boolean isAttacking()
    {
        return attack.isAttacking();
    }

    /**
     * Update the action movement.
     */
    private void updateActionMovement()
    {
        final double sensibility;
        double speed;
        // Horizontal movement
        if (keyRight && !keyLeft)
        {
            speed = movementSpeedMax;
        }
        else if (keyLeft && !keyRight)
        {
            speed = -movementSpeedMax;
        }
        else
        {
            speed = 0.0;
        }
        if (isOnGround() && (keyRight || keyLeft))
        {
            sensibility = sensibilityIncrease;
        }
        else
        {
            sensibility = sensibilityDecrease;
        }
        speed = tilt.updateActionMovementSlide(speed);
        speed = tilt.updateActionMovementLiana(speed);
        movement.setSensibility(sensibility);
        movement.setVelocity(movementSmooth);
        speed = tilt.updateMovementSlope(speed, sensibility, movementSpeedMax, movementSmooth);
        movement.setForceToReach(speed, 0.0);

        // Crouch
        if (isOnGround() && !isSliding() && keyDown)
        {
            movement.reset();
            timerFallen.stop();
            crouch = true;
        }
        else
        {
            crouch = false;
        }
    }

    /**
     * Update the jump action with a sufficient accuracy.
     */
    private void updateActionJump()
    {
        if (tilt.getLiana() == null)
        {
            if (keyUp && !jumped && attack.getState() == null)
            {
                if (!timerJump.isStarted())
                {
                    timerJump.start();
                }
                if (canJump() || tilt.getLianaSoared())
                {
                    if (tilt.getSlide() == null)
                    {
                        jumpForce.setForce(0.0, jumpHeightMax);
                        tilt.stopLianaSoar();
                    }
                    else
                    {
                        tilt.updateActionJumpSlide(jumpForce, jumpHeightMax);
                        jumped = true;
                    }
                    status.setCollision(EntityCollisionTile.NONE);
                }
            }
            else
            {
                jumped = true;
                if (timerJump.elapsed(Valdyn.JUMP_TIME_MIN))
                {
                    final double factor = timerJump.elapsed() / (double) Valdyn.JUMP_TIME_MAX;
                    jumpForce.setForce(0.0, UtilityMath.fixBetween(jumpHeightMax * factor, 0.0, jumpHeightMax));
                    timerJump.stop();
                }
            }
        }
        if (isOnGround() || tilt.getLianaSoared())
        {
            jumped = false;
        }
    }

    /**
     * Update the mirror state.
     * 
     * @param diffHorizontal The horizontal diff movement.
     */
    private void updateStateMirror(double diffHorizontal)
    {
        if (!extremity && !isDead() && diffHorizontal != 0.0)
        {
            mirror(diffHorizontal < 0.0);
        }
        else if (crouch)
        {
            if (keyRight)
            {
                mirror(false);
            }
            else if (keyLeft)
            {
                mirror(true);
            }
        }
    }

    /**
     * Update the states for on ground case.
     * 
     * @param diffHorizontal The horizontal diff movement.
     * @param mirror The mirror state.
     */
    private void updateStateOnGround(double diffHorizontal, boolean mirror)
    {
        if (crouch)
        {
            status.setState(ValdynState.CROUCH);
        }
        else if (mirror && keyRight && diffHorizontal < 0.0 || !mirror && keyLeft && diffHorizontal > 0.0)
        {
            status.setState(EntityState.TURN);
        }
        else if (diffHorizontal != 0.0)
        {
            status.setState(EntityState.WALK);
        }
        else if (extremity)
        {
            status.setState(ValdynState.BORDER);
        }
        else
        {
            status.setState(EntityState.IDLE);
        }
    }

    /**
     * Update the dead state case.
     */
    private void updateStateDead()
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
            if (status.collisionChangedFromTo(EntityCollisionTile.NONE, EntityCollisionTile.GROUND))
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
     * Check the collision on the extremity.
     * 
     * @param category The collision category.
     * @param mirror The mirror to apply.
     * @return The tile hit.
     */
    private Tile checkCollisionExtremity(CollisionTileCategory<TileCollision> category, boolean mirror)
    {
        final Tile tile = getCollisionTile(map, category);
        if (tile != null && tile.isBorder())
        {
            checkCollisionVertical(tile);
        }
        if (isOnExtremity(-UtilityMath.getSign(getCollisionTileOffset(category).getX())))
        {
            updateExtremity(mirror);
        }
        return tile;
    }

    /**
     * Check if there is a tile with a collision next to the player.
     * 
     * @param side -1 to check left, 1 to check right.
     * @return <code>true</code> if there is a tile with a collision, <code>false</code> else.
     */
    private boolean isOnExtremity(int side)
    {
        final Tile tile = map.getTile(this, 0, 0);
        if (tile != null && tile.isBorder())
        {
            final int tx = getLocationIntX() - tile.getX() + Valdyn.TILE_EXTREMITY_WIDTH * side;
            final Tile next = map.getTile(tile.getX() / map.getTileWidth() + side, tile.getY() / map.getTileHeight());
            final boolean noNext = next == null || TileCollision.NONE == next.getCollision();
            if (side == -1)
            {
                return noNext && tx <= Valdyn.TILE_EXTREMITY_WIDTH;
            }
            else if (side == 1)
            {
                return noNext && tx >= map.getTileWidth() - Valdyn.TILE_EXTREMITY_WIDTH;
            }
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
        attack.respawn();
        stats.fillHeart();
        hurtEffectCounter = 0;
        timerHurt.stop();
        drownedDeath = false;
        status.setCollision(EntityCollisionTile.GROUND);
        status.setState(EntityState.IDLE);
        if (lastCheckpoint != null)
        {
            teleport(lastCheckpoint.getX(), lastCheckpoint.getY());
        }
        camera.resetInterval(this);
    }

    @Override
    public void kill()
    {
        super.kill();
        Sfx.VALDYN_DIE.play();
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        attack.updateMirror(getMirror());
        for (final CoordTile coord : checkpoints)
        {
            if (getLocationIntX() - getWidth() > coord.getX() && getLocationIntX() < coord.getX() + 64
                    && getLocationIntY() > coord.getY() - 64 && getLocationIntY() < coord.getY() + 64)
            {
                lastCheckpoint = coord;
            }
        }
    }

    @Override
    public void render(Graphic g, CameraPlatform camera)
    {
        final boolean render = timerHurt.isStarted() && timerHurt.elapsed(Valdyn.HURT_TIME_BEFORE_EFFECT)
                && hurtEffectCounter % Valdyn.HURT_EFFECT_FREQ == 0;
        if (render || hurtEffectCounter == 0)
        {
            super.render(g, camera);
            attack.render(g, camera);
        }
    }

    @Override
    public void renderCollision(Graphic g, CameraGame camera)
    {
        super.renderCollision(g, camera);
        legCollision.renderCollision(g, camera);
        attack.renderCollision(g, camera);
    }

    @Override
    public void hitBy(Entity entity)
    {
        if (!isDead() && !timerHurt.isStarted())
        {
            Sfx.VALDYN_HURT.play();
            resetGravity();
            jumpForce.setForce(0.0, jumpHeightMax * 0.8);
            stats.decreaseHeart();
            timerHurt.start();
        }
    }

    @Override
    public void hitThat(Entity entity)
    {
        if (!isDead() && entity instanceof EntityMonster && status.getState() == ValdynState.ATTACK_FALL)
        {
            resetGravity();
            jumpForce.setForce(0.0, jumpHeightMax * 0.8);
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
        updateFall();
        updateFallen();
        updateActionMovement();
        if (!tilt.getLianaSoar())
        {
            attack.updateActionAttack();
        }
        updateActionJump();
        if (timerHurt.isStarted() && timerHurt.elapsed(Valdyn.HURT_TIME_BEFORE_EFFECT))
        {
            hurtEffectCounter++;
        }
        if (timerHurt.elapsed(Valdyn.HURT_TIME_MAX))
        {
            timerHurt.stop();
            hurtEffectCounter = 0;
        }
    }

    @Override
    protected void updateStates()
    {
        final double diffHorizontal = getHorizontalForce();
        if (!attack.isAttacking() || status.getState() == ValdynState.ATTACK_FALL || isSliding() || isLiana())
        {
            updateStateMirror(diffHorizontal);
        }

        // Update the states
        final boolean mirror = getMirror();
        if (timerHurt.isStarted() && !timerHurt.elapsed(Valdyn.HURT_TIME_BEFORE_EFFECT))
        {
            status.setState(EntityState.HURT);
        }
        else if (attack.updateStates())
        {
            // Attack updated
        }
        else if (tilt.updateStates())
        {
            // Tilt updated
        }
        else if (isFalling())
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
            updateStateOnGround(diffHorizontal, mirror);
        }
        if (isDead())
        {
            updateStateDead();
        }
    }

    @Override
    protected void updateDead()
    {
        timerHurt.stop();
        hurtEffectCounter = 0;
        if (drownedDeath)
        {
            if (getLocationY() < 0)
            {
                movement.reset();
                jumpForce.setForce(0.0, -0.4);
                stepDie = 1;
                resetGravity();
            }
        }
        else
        {
            jumpForce.setForce(-1.25, 2.75);
            resetGravity();
        }
        if (timerDie.elapsed(500))
        {
            stepDie = 1;
            if (!drownedDeath)
            {
                resetGravity();
                jumpForce.setForce(Force.ZERO);
                teleportY(getLocationOldY());
            }
            if (stepDie == 1 && timerDie.elapsed(2000))
            {
                stats.decreaseLife();
                if (stats.getLife() > 0)
                {
                    respawn();
                }
                else
                {
                    destroy();
                }
            }
        }
    }

    @Override
    protected void updateCollisions()
    {
        if (tilt.getLianaSoared())
        {
            extremity = true;
            status.setCollision(EntityCollisionTile.GROUND);
            teleportY(getLocationOldY());
            resetGravity();
            return;
        }
        extremity = false;
        if (getLocationY() < getLocationOldY() && timerFall.elapsed(50))
        {
            status.setCollision(EntityCollisionTile.NONE);
        }

        // Vertical collision
        if (getLocationY() <= getLocationOldY())
        {
            final Tile tileRight = checkCollisionExtremity(ValdynCollisionTileCategory.LEG_RIGHT, true);
            final Tile tileLeft = checkCollisionExtremity(ValdynCollisionTileCategory.LEG_LEFT, false);
            final Tile tile = getCollisionTile(map, EntityCollisionTileCategory.GROUND_CENTER);
            final boolean found = checkCollisionVertical(tile);
            tilt.updateCollisions(found, tile);

            // Special fix for slide from ground
            tilt.updateCollisionSlideGroundTransition(tileLeft, tileRight, tile);
        }

        // Horizontal collision
        checkCollisionHorizontal(ValdynCollisionTileCategory.KNEE_RIGHT);
        checkCollisionHorizontal(ValdynCollisionTileCategory.KNEE_LEFT);
        final Tile tile = getCollisionTile(map, EntityCollisionTileCategory.GROUND_CENTER);
        if (tile != null && tile.getCollision() == TileCollision.GROUND_SPIKE)
        {
            hitBy(null);
        }

        attack.updateCollisions();
        legCollision.updateCollision();

        // Kill when fall down
        final double waterHeight = landscape.getWaterHeight() - getHeight() * 1.5;
        if (getLocationY() < waterHeight)
        {
            kill();
            teleportY(waterHeight);
            drownedDeath = true;
        }
    }

    @Override
    protected void updateAnimations(double extrp)
    {
        final State state = status.getState();
        if (state == EntityState.WALK || state == EntityState.TURN || state == ValdynState.LIANA_SLIDE)
        {
            final double speed = Math.abs(getHorizontalForce()) / Valdyn.ANIM_WALK_SPEED_DIVISOR;
            setAnimSpeed(speed);
        }
        attack.updateAnimationShade(extrp);
    }

    @Override
    protected void handleAnimations(double extrp)
    {
        super.handleAnimations(extrp);
        tilt.updateAnimation(extrp);
    }
}
