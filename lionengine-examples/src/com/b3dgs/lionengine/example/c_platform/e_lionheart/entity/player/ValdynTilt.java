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
package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityAction;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityCollisionTile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Tile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TileCollision;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TileCollisionGroup;
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
    /** Liana soar. */
    private boolean lianaSoar;
    /** Liana soared. */
    private boolean lianaSoared;
    /** Liana soar start y. */
    private double lianaSoarY;

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
        final boolean keyLeft = valdyn.isEnabled(EntityAction.MOVE_LEFT);
        final boolean keyRight = valdyn.isEnabled(EntityAction.MOVE_RIGHT);
        final double forceH = movement.getForce().getForceHorizontal();
        if (movement.isDecreasingHorizontal()
                && (forceH > movementSpeedMax && keyRight || forceH < -movementSpeedMax && keyLeft))
        {
            movement.setVelocity(0.01);
            movement.setSensibility(sensibility / 4);
        }
        double newSpeed = speed;
        final Tile tile = map.getTile(valdyn, 0, 0);
        if (valdyn.isOnGround() && tile != null && tile.isGroup(TileCollisionGroup.SLOPE))
        {
            final TileCollisionGroup groupSlope = TileCollisionGroup.SLOPE;
            if (valdyn.isGoingDown() && (keyLeft && tile.isLeft(groupSlope) || keyRight && tile.isRight(groupSlope)))
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
            newSpeed = newSpeed * 0.2 - 1.5;
        }
        else if (liana == Align.RIGHT)
        {
            newSpeed = newSpeed * 0.2 + 1.5;
        }

        if (liana != null)
        {
            if (valdyn.isAttacking())
            {
                newSpeed = 0.0;
            }
            // Soar liana
            if (!lianaSoar && liana == Align.CENTER && valdyn.isEnabled(EntityAction.JUMP))
            {
                lianaSoar = true;
                lianaSoarY = valdyn.getLocationY();
            }
            // Exit liana
            if (valdyn.isEnabled(EntityAction.MOVE_DOWN) && !timerLianaUnGrip.isStarted())
            {
                timerLianaUnGrip.start();
                valdyn.setTimerFall(Valdyn.FALL_TIME_MARGIN);
                liana = null;
            }
        }
        // No movement while soaring
        if (lianaSoar)
        {
            newSpeed = 0.0;
        }
        // Exit liana soared
        if (lianaSoared)
        {
            if (valdyn.getDiffHorizontal() != 0 || valdyn.isEnabled(EntityAction.MOVE_DOWN))
            {
                valdyn.status.setCollision(EntityCollisionTile.NONE);
                valdyn.setTimerFall(Valdyn.FALL_TIME_MARGIN);
                lianaSoared = false;
            }
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
     * Terminate the liana soar.
     */
    void stopLianaSoar()
    {
        lianaSoared = false;
    }

    /**
     * Update the states.
     * 
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    boolean updateStates()
    {
        final boolean keyLeft = valdyn.isEnabled(EntityAction.MOVE_LEFT);
        final boolean keyRight = valdyn.isEnabled(EntityAction.MOVE_RIGHT);
        final double diffHorizontal = valdyn.getDiffHorizontal();
        final boolean updated;
        if (lianaSoared)
        {
            valdyn.status.setState(ValdynState.BORDER);
            updated = true;
        }
        else if (slide == Align.LEFT && keyLeft || slide == Align.RIGHT && keyRight)
        {
            valdyn.status.setState(ValdynState.SLIDE_FAST);
            updated = true;
        }
        else if (slide == Align.RIGHT && keyLeft || slide == Align.LEFT && keyRight)
        {
            valdyn.status.setState(ValdynState.SLIDE_SLOW);
            updated = true;
        }
        else if (slide != null)
        {
            valdyn.status.setState(ValdynState.SLIDE);
            updated = true;
        }
        else if (liana == Align.LEFT || liana == Align.RIGHT)
        {
            valdyn.status.setState(ValdynState.LIANA_SLIDE);
            updated = true;
        }
        else if (liana == Align.CENTER && diffHorizontal != 0.0)
        {
            valdyn.status.setState(ValdynState.LIANA_WALK);
            updated = true;
        }
        else if (lianaSoar)
        {
            valdyn.status.setState(ValdynState.LIANA_SOAR);
            updated = true;
        }
        else if (liana == Align.CENTER && diffHorizontal == 0.0)
        {
            valdyn.status.setState(ValdynState.LIANA_IDLE);
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
        final boolean keyDown = valdyn.isEnabled(EntityAction.MOVE_DOWN);
        if (!keyDown && valdyn.getDiffVertical() < 0.0
                && (timerLianaUnGrip.elapsed(ValdynTilt.LIANA_UNGRIP_TIME) || !timerLianaUnGrip.isStarted()))
        {
            timerLianaUnGrip.stop();
            liana = null;
            checkCollisionLiana(valdyn.getCollisionTile(map, ValdynCollisionTileCategory.HAND_LIANA_STEEP));
            checkCollisionLiana(valdyn.getCollisionTile(map, ValdynCollisionTileCategory.HAND_LIANA_LEANING));
        }
        if (found)
        {
            final double x = valdyn.getLocationX() - tile.getX() - valdyn.getWidth();
            final TileCollisionGroup groupSlide = TileCollisionGroup.SLIDE;
            final TileCollision collision = tile.getCollision();
            if (tile.isLeft(groupSlide) && collision != TileCollision.SLIDE_LEFT_GROUND_SLIDE
                    || collision == TileCollision.SLIDE_LEFT_GROUND_SLIDE && x > -12)
            {
                slide = Align.LEFT;
            }
            else if (tile.isRight(groupSlide) && collision != TileCollision.SLIDE_RIGHT_GROUND_SLIDE
                    || collision == TileCollision.SLIDE_RIGHT_GROUND_SLIDE && x > -12)
            {
                slide = Align.RIGHT;
            }
        }
        if (valdyn.status.collisionChangedFromTo(EntityCollisionTile.NONE, EntityCollisionTile.LIANA))
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
            if (tileLeft.isGroup(TileCollisionGroup.FLAT) || tileRight.isGroup(TileCollisionGroup.FLAT))
            {
                if (tileLeft.getCollision() == TileCollision.SLIDE_RIGHT_2 && diffHorizontal <= 0)
                {
                    movement.reset();
                    valdyn.teleportX(tileLeft.getX() + Map.TILE_WIDTH);
                }
                if (tileRight.getCollision() == TileCollision.SLIDE_LEFT_2 && diffHorizontal >= 0)
                {
                    movement.reset();
                    valdyn.teleportX(tileRight.getX() - 1);
                }
            }
        }
        if (tile != null)
        {
            final double x = valdyn.getLocationX() - tile.getX() - valdyn.getWidth();
            if (x > -13 && tile.getCollision() == TileCollision.SLIDE_LEFT_GROUND_SLIDE && diffHorizontal >= 0)
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
        if (tile != null)
        {
            final Double y = tile.getCollisionY(valdyn);
            if (valdyn.applyVerticalCollision(y))
            {
                valdyn.resetGravity();
                valdyn.status.setCollision(EntityCollisionTile.LIANA);
                valdyn.resetJump();
                liana = Align.CENTER;
                final TileCollisionGroup groupLianaSteep = TileCollisionGroup.LIANA_STEEP;
                final TileCollisionGroup groupLianaLeaning = TileCollisionGroup.LIANA_LEANING;
                if (tile.isLeft(groupLianaSteep) || tile.isLeft(groupLianaLeaning))
                {
                    liana = Align.LEFT;
                }
                else if (tile.isRight(groupLianaSteep) || tile.isRight(groupLianaLeaning))
                {
                    liana = Align.RIGHT;
                }
            }
        }
    }

    /**
     * Update the animation.
     * 
     * @param extrp The extrapolation value.
     */
    void updateAnimation(double extrp)
    {
        if (lianaSoar && valdyn.status.getState() == ValdynState.LIANA_SOAR)
        {
            final int frame = valdyn.getFrameAnim();
            if (frame > 0 && frame <= 3)
            {
                valdyn.setFrameOffsets(0, (int) (-lianaSoarY * 0.15) + 15);
            }
            else if (frame > 3 && frame <= 6)
            {
                valdyn.setFrameOffsets(0, (int) (-lianaSoarY * 0.15) + 0);
            }
            else if (frame == 7)
            {
                valdyn.setFrameOffsets(0, (int) (-lianaSoarY * 0.15) + 30);
            }
            else if (frame > 7 && frame <= 9)
            {
                valdyn.setFrameOffsets(0, (int) (-lianaSoarY * 0.30) + 55);
            }
            else if (frame > 9)
            {
                valdyn.setFrameOffsets(0, (int) (-lianaSoarY * 0.50) + 118);
            }
            if (valdyn.getAnimState() == AnimState.PLAYING)
            {
                lianaSoarY += 1.15 * extrp;
                valdyn.teleportY(lianaSoarY);
            }
            if (valdyn.getAnimState() == AnimState.FINISHED)
            {
                lianaSoar = false;
                lianaSoared = true;
                liana = null;
                valdyn.teleportY(lianaSoarY);
                valdyn.resetGravity();
            }
        }
        else
        {
            valdyn.setFrameOffsets(0, -2);
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
     * Get the liana soar state.
     * 
     * @return The liana soar state.
     */
    boolean getLianaSoar()
    {
        return lianaSoar;
    }

    /**
     * Get the liana soared state.
     * 
     * @return The liana soared state.
     */
    boolean getLianaSoared()
    {
        return lianaSoared;
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
