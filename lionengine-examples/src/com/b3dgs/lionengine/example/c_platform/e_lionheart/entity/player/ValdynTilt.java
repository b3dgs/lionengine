package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityAction;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityCollisionTile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Tile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TypeTileCollision;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TypeTileCollisionGroup;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Movement;

/**
 * Handle all Valdyn tilts (slide, slope, liana...).
 */
final class ValdynTilt
{
    /** Ungrip time max. */
    private static final int LIANA_UNGRIP_TIME = 400;
    /** Valdyn reference. */
    private final Valdyn valdyn;
    /** Valdyn movement. */
    private final Movement movement;
    /** Map reference. */
    private final Map map;
    /** Liana ungrip timer. */
    private final Timing timerLianaUnGrip;
    /** Slide state. */
    private Align slide;
    /** Liana state. */
    private Align liana;

    /**
     * Constructor.
     * 
     * @param valdyn The valdyn reference.
     * @param movement The movement reference.
     * @param map The map reference.
     */
    ValdynTilt(Valdyn valdyn, Movement movement, Map map)
    {
        this.valdyn = valdyn;
        this.movement = movement;
        this.map = map;
        timerLianaUnGrip = new Timing();
    }

    /**
     * Update the movement speed on a slope.
     * 
     * @param speed The current movement speed.
     * @param sensibility The current movement sensibility.
     * @param movementSpeedMax The max speed.
     * @param movementSmooth The movement smooth
     * @return The new movement speed.
     */
    double updateMovementSlope(double speed, double sensibility, double movementSpeedMax, double movementSmooth)
    {
        final boolean keyLeft = valdyn.isEnabled(TypeEntityAction.MOVE_LEFT);
        final boolean keyRight = valdyn.isEnabled(TypeEntityAction.MOVE_RIGHT);
        final double forceH = movement.getForce().getForceHorizontal();
        if (movement.isDecreasingHorizontal()
                && (forceH > movementSpeedMax && keyRight || forceH < -movementSpeedMax && keyLeft))
        {
            movement.setVelocity(0.01);
            movement.setSensibility(sensibility / 4);
        }
        double newSpeed = speed;
        final Tile tile = map.getTile(valdyn, 0, 0);
        if (valdyn.isOnGround() && tile != null && tile.isGroup(TypeTileCollisionGroup.SLOPE))
        {
            if (valdyn.isGoingDown() && (keyLeft && tile.isSlopeLeft() || keyRight && tile.isSlopeRight()))
            {
                newSpeed *= 1.3;
            }
            else if (valdyn.isGoingUp())
            {
                newSpeed *= 0.75;
                movement.setVelocity(movementSmooth / 2);
                movement.setSensibility(sensibility / 2);
            }
        }
        return newSpeed;
    }

    /**
     * Update the action movement on slide.
     * 
     * @param speed The current speed.
     * @return The new speed.
     */
    double updateActionMovementSlide(double speed)
    {
        double newSpeed = speed;
        if (slide == Align.LEFT)
        {
            newSpeed = newSpeed * 0.15;
            newSpeed -= 0.6;
            if (valdyn.getDiffHorizontal() > 0)
            {
                movement.reset();
            }
        }
        else if (slide == Align.RIGHT)
        {
            newSpeed = newSpeed * 0.15;
            newSpeed += 0.6;
            if (valdyn.getDiffHorizontal() < 0)
            {
                movement.reset();
            }
        }
        return newSpeed;
    }

    /**
     * Update the action movement on liana.
     * 
     * @param speed The current speed.
     * @return The new speed.
     */
    double updateActionMovementLiana(double speed)
    {
        double newSpeed = speed;
        if (liana == Align.CENTER)
        {
            newSpeed = newSpeed * 0.5;
        }
        else if (liana == Align.LEFT)
        {
            newSpeed = newSpeed * 0.5 - 1.5;
        }
        else if (liana == Align.RIGHT)
        {
            newSpeed = newSpeed * 0.5 + 1.5;
        }
        // Exit liana
        if (liana != null && valdyn.isEnabled(TypeEntityAction.MOVE_DOWN) && !timerLianaUnGrip.isStarted())
        {
            timerLianaUnGrip.start();
            liana = null;
        }
        return newSpeed;
    }

    /**
     * Update the jump action on the slide.
     * 
     * @param jumpForce The jump force reference.
     * @param jumpHeightMax The jump height max.
     */
    void updateActionJumpSlide(Force jumpForce, double jumpHeightMax)
    {
        jumpForce.setForce(0.0, jumpHeightMax * 0.6);
        if (slide == Align.RIGHT)
        {
            movement.getForce().setForce(jumpHeightMax * 0.6, 0.0);
            movement.setForceToReach(jumpHeightMax * 0.6, 0.0);
        }
        else if (slide == Align.LEFT)
        {
            movement.getForce().setForce(-jumpHeightMax * 0.6, 0.0);
            movement.setForceToReach(-jumpHeightMax * 0.6, 0.0);
        }
    }

    /**
     * Update the states.
     * 
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    boolean updateStates()
    {
        final boolean keyLeft = valdyn.isEnabled(TypeEntityAction.MOVE_LEFT);
        final boolean keyRight = valdyn.isEnabled(TypeEntityAction.MOVE_RIGHT);
        final double diffHorizontal = valdyn.getDiffHorizontal();
        final boolean updated;
        if (slide == Align.LEFT && keyLeft || slide == Align.RIGHT && keyRight)
        {
            valdyn.status.setState(TypeValdynState.SLIDE_FAST);
            updated = true;
        }
        else if (slide == Align.RIGHT && keyLeft || slide == Align.LEFT && keyRight)
        {
            valdyn.status.setState(TypeValdynState.SLIDE_SLOW);
            updated = true;
        }
        else if (slide != null)
        {
            valdyn.status.setState(TypeValdynState.SLIDE);
            updated = true;
        }
        else if (liana == Align.LEFT || liana == Align.RIGHT)
        {
            valdyn.status.setState(TypeValdynState.LIANA_SLIDE);
            updated = true;
        }
        else if (liana == Align.CENTER && diffHorizontal != 0.0)
        {
            valdyn.status.setState(TypeValdynState.LIANA_WALK);
            updated = true;
        }
        else if (liana == Align.CENTER && diffHorizontal == 0.0)
        {
            valdyn.status.setState(TypeValdynState.LIANA_IDLE);
            updated = true;
        }
        else
        {
            updated = false;
        }
        slide = null;
        return updated;
    }

    /**
     * Update the tilt collisions.
     * 
     * @param found If a vertical collision if found.
     * @param tile The tile found.
     */
    void updateCollisions(boolean found, Tile tile)
    {
        final boolean keyDown = valdyn.isEnabled(TypeEntityAction.MOVE_DOWN);
        if (!keyDown && valdyn.getDiffVertical() < 0.0
                && (timerLianaUnGrip.elapsed(ValdynTilt.LIANA_UNGRIP_TIME) || !timerLianaUnGrip.isStarted()))
        {
            timerLianaUnGrip.stop();
            valdyn.setCollisionOffset(0, 45);
            checkCollisionLiana(map.getFirstTileHit(valdyn, TypeTileCollision.COLLISION_LIANA));
        }
        if (found)
        {
            final double x = valdyn.getLocationX() - tile.getX() - valdyn.getWidth();
            final TypeTileCollision collision = tile.getCollision();
            if (tile.isSlideLeft() || collision == TypeTileCollision.SLIDE_LEFT_GROUND_SLIDE && x > -11)
            {
                slide = Align.LEFT;
            }
            else if (tile.isSlideRight() || collision == TypeTileCollision.SLIDE_RIGHT_GROUND_SLIDE && x > -11)
            {
                slide = Align.RIGHT;
            }
        }
        if (valdyn.status.collisionChangedFromTo(TypeEntityCollisionTile.NONE, TypeEntityCollisionTile.LIANA))
        {
            movement.reset();
        }
    }

    /**
     * Update the transition between the slide and ground.
     * 
     * @param tileLeft The left tile.
     * @param tileRight The right tile.
     * @param tile The center tile.
     */
    void updateCollisionSlideGroundTransition(Tile tileLeft, Tile tileRight, Tile tile)
    {
        final double diffHorizontal = valdyn.getDiffHorizontal();
        if (tileLeft != null && tileRight != null)
        {
            if (tileLeft.isGroup(TypeTileCollisionGroup.FLAT) || tileRight.isGroup(TypeTileCollisionGroup.FLAT))
            {
                if (tileLeft.getCollision() == TypeTileCollision.SLIDE_RIGHT_2 && diffHorizontal <= 0)
                {
                    movement.reset();
                    valdyn.teleportX(tileLeft.getX() + Map.TILE_WIDTH);
                }
                if (tileRight.getCollision() == TypeTileCollision.SLIDE_LEFT_2 && diffHorizontal >= 0)
                {
                    movement.reset();
                    valdyn.teleportX(tileRight.getX() - 1);
                }
            }
        }
        if (tile != null)
        {
            final double x = valdyn.getLocationX() - tile.getX() - valdyn.getWidth();
            if (x > -13 && tile.getCollision() == TypeTileCollision.SLIDE_LEFT_GROUND_SLIDE && diffHorizontal >= 0)
            {
                movement.reset();
                valdyn.teleportX(tile.getX() + 7);
            }
        }
    }

    /**
     * Check vertical axis on liana.
     * 
     * @param tile The tile collision.
     */
    void checkCollisionLiana(Tile tile)
    {
        liana = null;
        if (tile != null)
        {
            final Double y = tile.getCollisionY(valdyn);
            if (valdyn.applyVerticalCollision(y))
            {
                valdyn.resetGravity();
                valdyn.status.setCollision(TypeEntityCollisionTile.LIANA);
                valdyn.resetJump();
                liana = Align.CENTER;
                if (tile.isLianaSteepLeft())
                {
                    liana = Align.LEFT;
                }
                else if (tile.isLianaSteepRight())
                {
                    liana = Align.RIGHT;
                }
            }
        }
    }

    /**
     * Get the liana status.
     * 
     * @return The liana status.
     */
    Align getLiana()
    {
        return liana;
    }

    /**
     * Get the slide status.
     * 
     * @return The slide status.
     */
    Align getSlide()
    {
        return slide;
    }
}
