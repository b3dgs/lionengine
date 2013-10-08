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
package com.b3dgs.lionengine.example.lionheart.entity.monster;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.example.lionheart.Level;
import com.b3dgs.lionengine.example.lionheart.entity.EntityCollisionTile;
import com.b3dgs.lionengine.example.lionheart.entity.EntityState;
import com.b3dgs.lionengine.example.lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.lionheart.entity.patrol.Patrol;
import com.b3dgs.lionengine.example.lionheart.map.Map;
import com.b3dgs.lionengine.example.lionheart.map.Tile;
import com.b3dgs.lionengine.example.lionheart.map.TileCollision;
import com.b3dgs.lionengine.game.Force;

/**
 * Crawling monster implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Crawling
        extends EntityMonster
{
    /** Time before jump. */
    private static final int TIME_BEFORE_JUMP = 500;
    /** Jump timer. */
    private final Timing timerJump;
    /** Prepare jump flag. */
    private boolean prepareJump;
    /** Jumping flag. */
    private boolean jumping;

    /**
     * Constructor.
     * 
     * @param level The level reference.
     */
    public Crawling(Level level)
    {
        super(level, EntityType.CRAWLING);
        timerJump = new Timing();
        setFrameOffsets(0, -4);
        enableMovement(Patrol.HORIZONTAL);
    }

    /*
     * EntityMonster
     */

    @Override
    protected void updateActions()
    {
        super.updateActions();
        // Jump when slope
        final int side = -getSide();
        final Tile tile = map.getTile(this, side * Map.TILE_WIDTH, 0);
        final boolean jumpTile = tile == null || tile.getCollision() == TileCollision.NONE
                || tile.getCollision() == TileCollision.GROUND_SPIKE;
        if (!jumping && jumpTile)
        {
            jumping = true;
            timerJump.start();
        }
        if (jumping)
        {
            if (timerJump.elapsed(Crawling.TIME_BEFORE_JUMP))
            {
                prepareJump = false;
                jumpForce.setForce(0.0, 4.75);
                setMovementForce(1.75 * side, 0.0);
                if (status.collisionChangedFromTo(EntityCollisionTile.NONE, EntityCollisionTile.GROUND))
                {
                    jumping = false;
                    jumpForce.setForce(Force.ZERO);
                    timerJump.stop();
                    movement.setForceToReach(getMovementSpeedMax() * side, 0.0);
                    setMovementForce(getMovementSpeedMax() * side, 0.0);
                }
            }
            else
            {
                prepareJump = true;
                movement.reset();
            }
        }
    }

    @Override
    protected void updateStates()
    {
        if (isFalling())
        {
            status.setState(EntityState.FALL);
        }
        else if (prepareJump)
        {
            status.setState(EntityState.PREPARE_JUMP);
        }
        else if (isJumping())
        {
            status.setState(EntityState.JUMP);
        }
        else
        {
            super.updateStates();
        }
    }
}
