package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityMover;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityAction;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityCollisionTile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster.EntityMonster;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape.Landscape;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Tile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TypeTileCollision;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.purview.Collidable;
import com.b3dgs.lionengine.game.purview.model.CollidableModel;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Valdyn implementation (player).
 */
public final class Valdyn
        extends EntityMover
{
    /** The width of the tile extremity. */
    public static final int TILE_EXTREMITY_WIDTH = 3;
    /** Hurt effect value (lower is faster). */
    private static final int HURT_EFFECT_FREQ = 6;
    /** Hurt time before effect. */
    private static final int HURT_TIME_BEFORE_EFFECT = 500;
    /** Hurt time max. */
    private static final int HURT_TIME_MAX = 2000;
    /** Divisor for walk speed animation. */
    private static final double ANIM_WALK_SPEED_DIVISOR = 9.0;
    /** The fall time margin (in milli). */
    private static final int FALL_TIME_MARGIN = 100;
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
     * @param context The context reference.
     */
    public Valdyn(Context context)
    {
        super(context, TypeEntity.VALDYN);
        camera = context.camera;
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
        loadCollisions(TypeValdynCollision.values());
        loadAnimations(TypeValdynState.values());
        legCollision = new CollidableModel(this);
        legCollision.setCollision(getDataCollision("leg"));
        stats = new Stats(this);
        tilt = new ValdynTilt(this, movement, context.map);
        attack = new ValdynAttack(this, movement);
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
            for (final TypeEntityAction action : TypeEntityAction.VALUES)
            {
                actions.put(action, Boolean.valueOf(keyboard.isPressed(action.getKey())));
            }
            keyLeft = isEnabled(TypeEntityAction.MOVE_LEFT);
            keyRight = isEnabled(TypeEntityAction.MOVE_RIGHT);
            keyUp = isEnabled(TypeEntityAction.JUMP);
            keyDown = isEnabled(TypeEntityAction.MOVE_DOWN);
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
        if (!crouch && status.getState() == TypeValdynState.BORDER)
        {
            mirror(mirror);
        }
        extremity = true;
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
        if (isOnGround() && keyDown)
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
                if (canJump())
                {
                    if (tilt.getSlide() == null)
                    {
                        jumpForce.setForce(0.0, jumpHeightMax);
                    }
                    else
                    {
                        tilt.updateActionJumpSlide(jumpForce, jumpHeightMax);
                        jumped = true;
                    }
                    status.setCollision(TypeEntityCollisionTile.NONE);
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
        if (isOnGround())
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
        attack.updateMirror(getMirror());
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
            status.setState(TypeValdynState.CROUCH);
        }
        else if (mirror && keyRight && diffHorizontal < 0.0 || !mirror && keyLeft && diffHorizontal > 0.0)
        {
            status.setState(TypeEntityState.TURN);
        }
        else if (diffHorizontal != 0.0)
        {
            status.setState(TypeEntityState.WALK);
        }
        else if (extremity)
        {
            status.setState(TypeValdynState.BORDER);
        }
        else
        {
            status.setState(TypeEntityState.IDLE);
        }
    }

    /**
     * Update the dead state case.
     */
    private void updateStateDead()
    {
        if (stepDie == 0 || getLocationY() < 0)
        {
            status.setState(TypeEntityState.DIE);
        }
        else
        {
            status.setState(TypeEntityState.DEAD);
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
            if (status.collisionChangedFromTo(TypeEntityCollisionTile.NONE, TypeEntityCollisionTile.GROUND))
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
     * @param offsetX The offset collision.
     * @param mirror The mirror to apply.
     * @return The tile hit.
     */
    private Tile checkCollisionExtremity(int offsetX, boolean mirror)
    {
        setCollisionOffset(offsetX, 0);
        final Tile tile = map.getFirstTileHit(this, TypeTileCollision.COLLISION_VERTICAL);
        if (tile != null && tile.isBorder())
        {
            checkCollisionVertical(tile);
        }
        if (isOnExtremity(-UtilityMath.getSign(offsetX)))
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
            final boolean noNext = next == null || TypeTileCollision.NONE == next.getCollision();
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
        teleport(2100, 300);
        camera.resetInterval(this);
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
        if (!timerHurt.isStarted() && entity instanceof EntityMonster)
        {
            resetGravity();
            jumpForce.setForce(0.0, jumpHeightMax * 0.8);
            stats.decreaseHeart();
            timerHurt.start();
        }
    }

    @Override
    public void hitThat(Entity entity)
    {
        if (entity instanceof EntityMonster && status.getState() == TypeValdynState.ATTACK_FALL)
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
        updateActionMovement();
        attack.updateActionAttack();
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
        if (!attack.isAttacking() || status.getState() == TypeValdynState.ATTACK_FALL)
        {
            updateStateMirror(diffHorizontal);
        }

        // Update the states
        final boolean mirror = getMirror();
        if (timerHurt.isStarted() && !timerHurt.elapsed(Valdyn.HURT_TIME_BEFORE_EFFECT))
        {
            status.setState(TypeEntityState.HURT);
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
            status.setState(TypeEntityState.FALL);
        }
        else if (isJumping())
        {
            status.setState(TypeEntityState.JUMP);
        }
        else if (timerFallen.isStarted())
        {
            status.setState(TypeEntityState.FALLEN);
        }
        else if (isOnGround())
        {
            updateStateOnGround(diffHorizontal, mirror);
        }
        if (isDead())
        {
            updateStateDead();
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
    }

    @Override
    protected void updateCollisions()
    {
        extremity = false;
        if (getLocationY() < getLocationOldY() && timerFall.elapsed(50))
        {
            status.setCollision(TypeEntityCollisionTile.NONE);
        }

        // Vertical collision
        final Tile tileRight = checkCollisionExtremity(Valdyn.TILE_EXTREMITY_WIDTH, true);
        final Tile tileLeft = checkCollisionExtremity(-Valdyn.TILE_EXTREMITY_WIDTH, false);
        setCollisionOffset(0, 0);
        final Tile tile = map.getFirstTileHit(this, TypeTileCollision.COLLISION_VERTICAL);
        final boolean found = checkCollisionVertical(tile);
        tilt.updateCollisions(found, tile);
        setCollisionOffset(0, 0);

        // Special fix for slide from ground
        tilt.updateCollisionSlideGroundTransition(tileLeft, tileRight, tile);

        // Horizontal collision
        // TODO: horizontal collisions

        attack.updateCollisions();
        legCollision.updateCollision();

        // Kill when fall down
        final double waterHeight = landscape.getWaterHeight() - getHeight() * 1.5;
        if (getLocationY() < waterHeight)
        {
            kill();
            teleportY(waterHeight);
        }
    }

    @Override
    protected void updateAnimations(double extrp)
    {
        final TypeState state = status.getState();
        if (state == TypeEntityState.WALK || state == TypeEntityState.TURN || state == TypeValdynState.LIANA_SLIDE)
        {
            final double speed = Math.abs(getHorizontalForce()) / Valdyn.ANIM_WALK_SPEED_DIVISOR;
            setAnimSpeed(speed);
        }
        attack.updateAnimationShade(extrp);
    }
}
