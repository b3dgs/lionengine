package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.AppLionheart;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityMover;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityAction;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityCollision;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster.EntityMonster;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Tile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TypeTileCollision;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Valdyn implementation (player).
 */
public final class Valdyn
        extends EntityMover
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
    /** Valdyn stats. */
    public final Stats stats;
    /** Shade surface. */
    private final SpriteAnimated shade;
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Animations shade list. */
    private final EnumMap<TypeEntityState, Animation> shades;
    /** List of starting frames when shades are enabled. */
    private final EnumMap<TypeEntityState, Integer> shadesEnabled;
    /** Attack data. */
    private final EnumMap<TypeEntityState, Set<Attack>> attacks;
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
    /** Extremity state (used for border state). */
    private boolean extremity;
    /** Jumped state. */
    private boolean jumped;
    /** Crouch state. */
    private boolean crouch;
    /** Attack state. */
    private TypeEntityState attack;
    /** Attack prepared. */
    private boolean attackPrepared;
    /** Attacking state. */
    private boolean attacking;
    /** Attacked state. */
    private boolean attacked;
    /** Can hurt monster. */
    private boolean canHurtMonster;
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
     * @param context The context reference.
     */
    public Valdyn(Context context)
    {
        super(context, TypeEntity.VALDYN);
        camera = context.camera;
        shade = Drawable.loadSpriteAnimated(Media.get(AppLionheart.ENTITIES_DIR, "players", "shade.png"), 7, 7);
        shade.load(false);
        shades = new EnumMap<>(TypeEntityState.class);
        shadesEnabled = new EnumMap<>(TypeEntityState.class);
        attacks = new EnumMap<>(TypeEntityState.class);
        timerJump = new Timing();
        timerFall = new Timing();
        timerFallen = new Timing();
        timerHurt = new Timing();
        fallenDuration = getDataInteger("fallenDuration", "data");
        movementSpeedMax = getDataDouble("speedMax", "data", "movement");
        movementSmooth = getDataDouble("smooth", "data", "movement");
        sensibilityIncrease = getDataDouble("sensibilityIncrease", "data", "movement");
        sensibilityDecrease = getDataDouble("sensibilityDecrease", "data", "movement");
        setFrameOffsets(40, -2);
        addShadeAnimation(TypeEntityState.ATTACK_UP, 1);
        addShadeAnimation(TypeEntityState.ATTACK_HORIZONTAL, 1);
        addShadeAnimation(TypeEntityState.ATTACK_TURNING, 2);
        addShadeAnimation(TypeEntityState.ATTACK_JUMP, 1);
        stats = new Stats(this);
        loadAttacks();
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
            keyAttack = isEnabled(TypeEntityAction.ATTACK);
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
     * Get the attacking flag.
     * 
     * @return <code>true</code> if attacking, <code>false</code> else.
     */
    public boolean canHurtMonster()
    {
        return canHurtMonster;
    }

    /**
     * Load all attacks data.
     */
    private void loadAttacks()
    {
        for (XmlNode animation : getDataRoot().getChildren("animation"))
        {
            final Set<Attack> set = new HashSet<>(1);
            for (XmlNode attack : animation.getChildren("attack"))
            {
                set.add(new Attack(attack));
            }
            if (!set.isEmpty())
            {
                attacks.put(TypeEntityState.valueOf(animation.readString("name").toUpperCase(Locale.ENGLISH)), set);
            }
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
        final double forceH = movement.getForce().getForceHorizontal();
        if (movement.isDecreasingHorizontal()
                && (forceH > movementSpeedMax && keyRight || forceH < -movementSpeedMax && keyLeft))
        {
            movement.setVelocity(0.01);
            movement.setSensibility(sensibility / 4);
        }
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
                movement.setVelocity(movementSmooth);
                movement.setSensibility(sensibility);
            }
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
        movement.setSensibility(sensibility);
        movement.setVelocity(movementSmooth);
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
                status.setCollision(TypeEntityCollision.NONE);
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
        final boolean fallAttack = status.getState() == TypeEntityState.ATTACK_FALL;
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
        if (attackFinished
                && (attack == TypeEntityState.ATTACK_PREPARING_DOWN || attack == TypeEntityState.ATTACK_PREPARING))
        {
            attackPrepared = true;
        }
        if (attackPrepared)
        {
            computeAttackPrepare(TypeEntityState.ATTACK_PREPARED, TypeEntityState.ATTACK_PREPARED_DOWN);
        }
        else
        {
            computeAttackPrepare(TypeEntityState.ATTACK_PREPARING, TypeEntityState.ATTACK_PREPARING_DOWN);
        }
        attacking = false;
    }

    /**
     * List of implemented attacks.
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
            setAttack(TypeEntityState.ATTACK_FALL);
            attacked = false;
        }
        else if (!isOnGround())
        {
            setAttack(TypeEntityState.ATTACK_JUMP);
        }
        else if (attackPrepared && isOnGround() && keyDown && wrongWay)
        {
            setAttack(TypeEntityState.ATTACK_DOWN_LEG);
        }
        else if (attackPrepared && isOnGround() && !keyDown && keyUp)
        {
            setAttack(TypeEntityState.ATTACK_UP);
        }
        else if (attackPrepared && isOnGround() && !keyDown && wrongWay)
        {
            setAttack(TypeEntityState.ATTACK_HORIZONTAL);
        }
        else if (attackPrepared && isOnGround() && !keyDown && goodWay)
        {
            setAttack(TypeEntityState.ATTACK_TURNING);
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
        if (attack == TypeEntityState.ATTACK_TURNING)
        {
            mirror(!getMirror());
            updateMirror();
        }
        if (keyAttack)
        {
            if (isOnGround())
            {
                attack = TypeEntityState.ATTACK_PREPARED;
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
            status.setState(TypeEntityState.CROUCH);
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
            status.setState(TypeEntityState.BORDER);
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
     * Update the shade animation.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAnimationShade(double extrp)
    {
        final TypeEntityState state = status.getState();
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
            if (status.collisionChangedFromTo(TypeEntityCollision.NONE, TypeEntityCollision.GROUND))
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
     * Update the collision during attack.
     * 
     * @param x The horizontal offset from entity.
     * @param y The vertical offset from entity.
     * @param width The entity collision width.
     * @param height The entity collision height.
     * @param frame The current frame index.
     * @return <code>true</code> if attacking, <code>false</code> else.
     */
    private boolean updateAttackCollision(int x, int y, int width, int height, int frame)
    {
        final Set<Attack> set = attacks.get(status.getState());
        if (set != null)
        {
            for (Attack attack : set)
            {
                if (frame == attack.frame)
                {
                    final int attackX = (getMirror() ? -attack.x : attack.x);
                    super.updateCollision(x + attackX, y + attack.y, attack.width, attack.height);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Add a shade animation for the sword attack effect.
     * 
     * @param state The state enum.
     * @param startAtFrame The frame index (relative to current animation) where it should start.
     */
    private void addShadeAnimation(TypeEntityState state, int startAtFrame)
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
    private void computeAttackPrepare(TypeEntityState prepare, TypeEntityState prepareDown)
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
    private void setAttack(TypeEntityState nextAttack)
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
    public void updateCollision(int x, int y, int width, int height)
    {
        final int frame = getFrameAnim();
        if (crouch)
        {
            super.updateCollision(x, y, width, height - 18);
        }
        else
        {
            super.updateCollision(x, y, width, height);
        }
        if (!status.stateChanged() && getAnimState() == AnimState.PLAYING)
        {
            canHurtMonster = updateAttackCollision(x, y, width, height, frame);
        }
    }

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
        teleport(900, 100);
        camera.resetInterval(this);
    }

    @Override
    public void hitBy(Entity entity)
    {
        if (!timerHurt.isStarted() && entity instanceof EntityMonster)
        {
            jumpForce.setForce(0.0, jumpHeightMax);
            stats.decreaseHeart();
            timerHurt.start();
        }
    }

    @Override
    public void hitThat(Entity entity)
    {
        if (entity instanceof EntityMonster)
        {
            // TODO: Force jump
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
        if (timerHurt.elapsed(2000))
        {
            timerHurt.stop();
        }
    }

    @Override
    protected void updateStates()
    {
        final double diffHorizontal = getDiffHorizontal();
        if (!attacking || status.getState() == TypeEntityState.ATTACK_FALL)
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
            status.setCollision(TypeEntityCollision.NONE);
        }

        // Vertical collision
        if (getDiffVertical() < 0 || isOnGround())
        {
            checkCollisionVertical(0);
            checkCollisionExtremity(Valdyn.TILE_EXTREMITY_WIDTH, true); // Left leg;
            checkCollisionExtremity(-Valdyn.TILE_EXTREMITY_WIDTH, false); // Left leg;
        }

        // Stop attack if collide
        if (status.collisionChangedFromTo(TypeEntityCollision.NONE, TypeEntityCollision.GROUND))
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
        final TypeEntityState state = status.getState();
        if (state == TypeEntityState.WALK || state == TypeEntityState.TURN)
        {
            final double speed = Math.abs(getHorizontalForce()) / Valdyn.ANIM_WALK_SPEED_DIVISOR;
            setAnimSpeed(speed);
        }
        updateAnimationShade(extrp);
    }
}
