package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import java.util.EnumMap;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.AppLionheart;
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
    /** Shade surface. */
    private final SpriteAnimated shade;
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Animations shade list. */
    private final EnumMap<EntityState, Animation> shades;
    /** List of starting frames when shades are enabled. */
    private final EnumMap<EntityState, Integer> shadesEnabled;
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
    /** Crouch state. */
    private boolean crouch;
    /** Attack state. */
    private EntityState attack;
    /** Attack prepared. */
    private boolean attackPrepared;
    /** Attacking state. */
    private boolean attacking;
    /** Attacked state. */
    private boolean attacked;
    /** Shade can be played. */
    private boolean shadeCanBePlayed;
    /** Left key state. */
    private boolean keyLeft;
    /** Right key state. */
    private boolean keyRight;
    /** Up key state. */
    private boolean keyUp;
    /** Down key state. */
    private boolean keyDown;
    /** Attack key state. */
    private boolean keyAttack;

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
        shade = Drawable.loadSpriteAnimated(Media.get(AppLionheart.ENTITIES_DIR, "shade.png"), 7, 7);
        shade.load(false);
        shades = new EnumMap<>(EntityState.class);
        shadesEnabled = new EnumMap<>(EntityState.class);
        timerJump = new Timing();
        timerFall = new Timing();
        timerFallen = new Timing();
        fallenDuration = getDataInteger("fallenDuration", "data");
        movementSpeedMax = getDataDouble("speedMax", "data", "movement");
        movementSmooth = getDataDouble("smooth", "data", "movement");
        sensibilityIncrease = getDataDouble("sensibilityIncrease", "data", "movement");
        sensibilityDecrease = getDataDouble("sensibilityDecrease", "data", "movement");
        addShadeAnimation(EntityState.ATTACK_UP, 1);
        addShadeAnimation(EntityState.ATTACK_HORIZONTAL, 1);
        addShadeAnimation(EntityState.ATTACK_TURNING, 2);
        addShadeAnimation(EntityState.ATTACK_JUMP, 1);
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
            keyAttack = isEnabled(EntityAction.ATTACK);
        }
        else
        {
            keyLeft = false;
            keyRight = false;
            keyUp = false;
            keyDown = false;
            keyAttack = false;
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
        if (!isGoingUp() && (forceH > movementSpeedMax && keyRight || forceH < -movementSpeedMax && keyLeft))
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
            sensibility = sensibilityIncrease;
        }
        else if (keyLeft && !keyRight)
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
        if (keyUp && !jumped && attack == null)
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
                final double factor = timerJump.elapsed() / (double) Valdyn.JUMP_TIME_MAX;
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
     * Update the attack action.
     */
    private void updateActionAttack()
    {
        // Attack ended
        final boolean attackFinished = getAnimState() == AnimState.FINISHED;
        final boolean fallAttack = status.getState() == EntityState.ATTACK_FALL;
        // Stop if attack finished and if no longer attacking, or fall attack (special case)
        if (attacking && attackFinished && !fallAttack || (!attacking || fallAttack) && !keyAttack)
        {
            updateActionAttackFinished();
        }
        if (keyAttack)
        {
            // Prepare attack
            if (!attacking && isOnGround())
            {
                updateActionAttackPrepare(attackFinished);
            }
            updateActionAttackSelect(attackPrepared);
        }
        if (attack != null && isOnGround())
        {
            movement.reset();
            timerFallen.stop();
        }
    }

    /**
     * Update the prepare attack action.
     * 
     * @param attackFinished The attack finished state.
     */
    private void updateActionAttackPrepare(boolean attackFinished)
    {
        if (attackFinished && (attack == EntityState.ATTACK_PREPARING_DOWN || attack == EntityState.ATTACK_PREPARING))
        {
            attackPrepared = true;
        }
        if (attackPrepared)
        {
            computeAttackPrepare(EntityState.ATTACK_PREPARED, EntityState.ATTACK_PREPARED_DOWN);
        }
        else
        {
            computeAttackPrepare(EntityState.ATTACK_PREPARING, EntityState.ATTACK_PREPARING_DOWN);
        }
        attacking = false;
    }

    /**
     * List of implemented attacks
     * 
     * @param attackPrepared <code>true</code> if attack need to be prepared, <code>false</code> else.
     */
    private void updateActionAttackSelect(boolean attackPrepared)
    {
        final boolean mirror = getMirror();
        final boolean goodWay = !mirror && keyLeft || mirror && keyRight;
        final boolean wrongWay = mirror && keyLeft || !mirror && keyRight;
        if (!isOnGround() && keyDown)
        {
            setAttack(EntityState.ATTACK_FALL);
            attacked = false;
        }
        else if (!isOnGround())
        {
            setAttack(EntityState.ATTACK_JUMP);
        }
        else if (attackPrepared && isOnGround() && keyDown && wrongWay)
        {
            setAttack(EntityState.ATTACK_DOWN_LEG);
        }
        else if (attackPrepared && isOnGround() && !keyDown && keyUp)
        {
            setAttack(EntityState.ATTACK_UP);
        }
        else if (attackPrepared && isOnGround() && !keyDown && wrongWay)
        {
            setAttack(EntityState.ATTACK_HORIZONTAL);
        }
        else if (attackPrepared && isOnGround() && !keyDown && goodWay)
        {
            setAttack(EntityState.ATTACK_TURNING);
        }
        else
        {
            attacked = false;
        }
    }

    /**
     * Update the attack finished state.
     */
    private void updateActionAttackFinished()
    {
        if (attack == EntityState.ATTACK_TURNING)
        {
            mirror(!getMirror());
            updateMirror();
        }
        if (keyAttack)
        {
            if (isOnGround())
            {
                attack = EntityState.ATTACK_PREPARED;
            }
            else
            {
                attack = null;
            }
            attackPrepared = true;
        }
        else
        {
            attack = null;
            attackPrepared = false;
            attacked = false;
        }
        attacking = false;
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
        shade.setMirror(getMirror());
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
            status.setState(EntityState.CROUCH);
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
            status.setState(EntityState.BORDER);
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
     * Update the shade animation.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAnimationShade(double extrp)
    {
        final EntityState state = status.getState();
        if (status.stateChanged())
        {
            if (shades.containsKey(state))
            {
                shadeCanBePlayed = true;
            }
            else
            {
                shade.stopAnimation();
            }
        }
        if (shadeCanBePlayed && shadesEnabled.containsKey(state))
        {
            final int index = getFrame() - getAnimation(state.getAnimationName()).getFirst();
            if (index >= shadesEnabled.get(state).intValue())
            {
                shade.play(shades.get(state));
                shadeCanBePlayed = false;
            }
        }
        shade.updateAnimation(extrp);
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
     * Add a shade animation for the sword attack effect.
     * 
     * @param state The state enum.
     * @param startAtFrame The frame index (relative to current animation) where it should start.
     */
    private void addShadeAnimation(EntityState state, int startAtFrame)
    {
        shades.put(state, getAnimation("shade_" + state.getAnimationName()));
        shadesEnabled.put(state, Integer.valueOf(startAtFrame));
    }

    /**
     * Compute the prepare attack state (function used for refactoring).
     * 
     * @param prepare Prepare state.
     * @param prepareDown Prepare down state.
     */
    private void computeAttackPrepare(EntityState prepare, EntityState prepareDown)
    {
        if (keyDown)
        {
            attack = prepareDown;
        }
        else
        {
            attack = prepare;
        }
    }

    /**
     * Set the next attack. To be called only by {@link #updateActionAttack()}
     * 
     * @param nextAttack The next attack type.
     */
    private void setAttack(EntityState nextAttack)
    {
        if (!attacked)
        {
            attack = nextAttack;
            attacking = true;
            attacked = true;
            attackPrepared = false;
        }
    }

    /**
     * Check the collision on the extremity.
     * 
     * @param offsetX The offset collision.
     * @param mirror The mirror to apply.
     */
    private void checkCollisionExtremity(int offsetX, boolean mirror)
    {
        final Tile tile = map.getTile(this, offsetX, 0);
        if (tile != null && tile.isBorder())
        {
            checkCollisionVertical(offsetX);
        }
        if (isOnExtremity(-UtilityMath.getSign(offsetX)))
        {
            if (!crouch)
            {
                mirror(mirror);
            }
            extremity = true;
        }
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
    public void render(Graphic g, CameraPlatform camera)
    {
        super.render(g, camera);
        if (shade.getAnimState() == AnimState.PLAYING)
        {
            renderAnim(g, shade, camera);
        }
    }

    @Override
    public void respawn()
    {
        super.respawn();
        timerJump.stop();
        jumpForce.setForce(Force.ZERO);
        attack = null;
        attacking = false;
        attackPrepared = false;
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
        updateActionMovement();
        updateActionAttack();
        updateActionJump();
    }

    @Override
    protected void updateStates()
    {
        final double diffHorizontal = getDiffHorizontal();
        if (!attacking || status.getState() == EntityState.ATTACK_FALL)
        {
            updateStateMirror(diffHorizontal);
        }

        // Update the states
        final boolean mirror = getMirror();
        if (attack != null)
        {
            status.setState(attack);
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
            checkCollisionExtremity(Valdyn.TILE_EXTREMITY_WIDTH, true); // Left leg;
            checkCollisionExtremity(-Valdyn.TILE_EXTREMITY_WIDTH, false); // Left leg;
        }

        // Stop attack if collide
        if (status.collisionChangedFromTo(EntityCollision.NONE, EntityCollision.GROUND))
        {
            attack = null;
            attacking = false;
            attackPrepared = false;
        }

        // Kill when fall down
        if (getLocationY() < 0)
        {
            kill();
            setLocationY(0);
        }
    }

    @Override
    protected void updateAnimations(double extrp)
    {
        final EntityState state = status.getState();
        if (state == EntityState.WALK || state == EntityState.TURN)
        {
            final double speed = Math.abs(getHorizontalForce()) / Valdyn.ANIM_WALK_SPEED_DIVISOR;
            setAnimSpeed(speed);
        }
        updateAnimationShade(extrp);
    }
}
