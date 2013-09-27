package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityCollisionTile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.Patrol;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Tile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TileCollision;
import com.b3dgs.lionengine.game.Force;

/**
 * Crawling monster implementation.
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
