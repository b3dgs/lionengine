package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import java.io.IOException;

import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.FactoryEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.TypeEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityMover;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.Patrollable;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.PatrollerModel;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.TypePatrol;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;

/**
 * Monster base implementation.
 */
public class EntityMonster
        extends EntityMover implements Patrollable
{
    /** Effect factory. */
    private final FactoryEffect factoryEffect;
    /** Patrol model. */
    private final PatrollerModel patroller;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param type The entity type.
     */
    public EntityMonster(Context context, TypeEntity type)
    {
        super(context, type);
        factoryEffect = context.factoryEffect;
        patroller = new PatrollerModel(this);
    }

    /*
     * EntityMover
     */
    
    @Override
    public void prepare()
    {
        super.prepare();
        patroller.prepare();
    }
    
    @Override
    public void save(FileWriting file) throws IOException
    {
        super.save(file);
        patroller.save(file);
    }
    
    @Override
    public void load(FileReading file) throws IOException
    {
        super.load(file);
        patroller.load(file);
    }
    
    @Override
    protected void updateActions()
    {
        final TypeState state = status.getState();
        if (state == TypeEntityState.TURN)
        {
            if (getAnimState() == AnimState.FINISHED)
            {
                final int side = getSide();
                setSide(-side);
                mirror(side < 0);
                if (getPatrolType() == TypePatrol.HORIZONTAL)
                {
                    setMovementForce(movementSpeedMax * side, 0.0);
                    teleportX(getLocationIntX() + side);
                }
                else if (getPatrolType() == TypePatrol.VERTICAL)
                {
                    setMovementForce(0.0, movementSpeedMax * side);
                    teleportY(getLocationIntY() + side);
                }
            }
            else
            {
                movement.reset();
            }
        }
        else if (state == TypeEntityState.WALK)
        {
            if (getPatrolType() == TypePatrol.HORIZONTAL)
            {
                final int x = getLocationIntX();
                if (x > getPositionMax())
                {
                    teleportX(getPositionMax());
                }
                if (x < getPositionMin())
                {
                    teleportX(getPositionMin());
                }
            }
            else if (getPatrolType() == TypePatrol.VERTICAL)
            {
                final int y = getLocationIntY();
                if (y > getPositionMax())
                {
                    teleportY(getPositionMax());
                }
                if (y < getPositionMin())
                {
                    teleportY(getPositionMin());
                }
            }
        }
    }

    @Override
    public void hitBy(Entity entity)
    {
        kill();
    }

    @Override
    public void hitThat(Entity entity)
    {
        // Nothing to do
    }

    @Override
    protected void updateStates()
    {
        boolean willTurn = false;
        if (getPatrolType() == TypePatrol.HORIZONTAL)
        {
            final int x = getLocationIntX();
            willTurn = x == getPositionMin() || x == getPositionMax();
        }
        else if (getPatrolType() == TypePatrol.VERTICAL)
        {
            final int y = getLocationIntY();
            willTurn = y == getPositionMin() || y == getPositionMax();
        }
        
        final double diffHorizontal = getDiffHorizontal();
        final double diffVertical = getDiffVertical();
        if (hasPatrol() && willTurn)
        {
            status.setState(TypeEntityState.TURN);
        }
        else if (diffHorizontal != 0.0 || diffVertical != 0.0)
        {
            status.setState(TypeEntityState.WALK);
        }
        else
        {
            status.setState(TypeEntityState.IDLE);
        }
    }

    @Override
    protected void updateDead()
    {
        factoryEffect.startEffect(TypeEffect.EXPLODE, (int) dieLocation.getX() - getCollisionData().getWidth() / 2 - 5,
                (int) dieLocation.getY() - 5);
        // TODO: Play explode sound
        destroy();
    }

    @Override
    protected void updateAnimations(double extrp)
    {
        // Nothing to do
    }

    /*
     * Patrollable
     */

    @Override
    public void setMovementForce(double fh, double fv)
    {
        movement.getForce().setForce(fh, fv);
    }
    
    @Override
    public void setMovementSpeedMax(double speed)
    {
        movementSpeedMax = speed;
    }

    @Override
    public double getMovementSpeedMax()
    {
        return movementSpeedMax;
    }

    @Override
    public double getForceHorizontal()
    {
        return movement.getForce().getForceHorizontal();
    }

    /*
     * Patroller
     */
    
    @Override
    public void enableMovement(TypePatrol type)
    {
        patroller.enableMovement(type);
    }

    @Override
    public void setSide(int side)
    {
        patroller.setSide(side);
    }

    @Override
    public void setPatrolType(TypePatrol movement)
    {
        patroller.setPatrolType(movement);
    }

    @Override
    public void setFirstMove(int firstMove)
    {
        patroller.setFirstMove(firstMove);
    }

    @Override
    public void setMoveSpeed(int speed)
    {
        patroller.setMoveSpeed(speed);
    }

    @Override
    public void setPatrolLeft(int left)
    {
        patroller.setPatrolLeft(left);
    }

    @Override
    public void setPatrolRight(int right)
    {
        patroller.setPatrolRight(right);
    }

    @Override
    public int getSide()
    {
        return patroller.getSide();
    }

    @Override
    public TypePatrol getPatrolType()
    {
        return patroller.getPatrolType();
    }

    @Override
    public int getFirstMove()
    {
        return patroller.getFirstMove();
    }

    @Override
    public int getMoveSpeed()
    {
        return patroller.getMoveSpeed();
    }

    @Override
    public int getPatrolLeft()
    {
        return patroller.getPatrolLeft();
    }

    @Override
    public int getPatrolRight()
    {
        return patroller.getPatrolRight();
    }

    @Override
    public int getPositionMin()
    {
        return patroller.getPositionMin();
    }

    @Override
    public int getPositionMax()
    {
        return patroller.getPositionMax();
    }

    @Override
    public boolean hasPatrol()
    {
        return patroller.hasPatrol();
    }

    @Override
    public boolean isPatrolEnabled()
    {
        return patroller.isPatrolEnabled();
    }

    @Override
    public boolean isPatrolEnabled(TypePatrol type)
    {
        return patroller.isPatrolEnabled(type);
    }
}
