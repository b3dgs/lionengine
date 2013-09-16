package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;

/**
 * Patroller default implementation.
 */
public class PatrollerModel
        implements Patroller
{
    /** Entity owner. */
    private final Patrollable owner;
    /** Movement enabled used in patrol. */
    private final Set<TypePatrol> enableMovement;
    /** Patrol current movement side. */
    private int side;
    /** Patrol minimum position. */
    private int posMin;
    /** Patrol maximum position. */
    private int posMax;
    /** Patrol existence flag. */
    private boolean hasPatrol;
    /** Movement type. */
    private TypePatrol movementType;
    /** First move flag. */
    private int firstMove;
    /** Movement speed. */
    private int moveSpeed;
    /** Patrol left value. */
    private int patrolLeft;
    /** Patrol right value. */
    private int patrolRight;

    /**
     * Constructor.
     * 
     * @param owner The owner reference.
     */
    public PatrollerModel(Patrollable owner)
    {
        this.owner = owner;
        enableMovement = new HashSet<>(4);
        movementType = TypePatrol.NONE;
        enableMovement(TypePatrol.NONE);
    }

    /**
     * Prepare the patrol.
     */
    public void prepare()
    {
        hasPatrol = getPatrolLeft() != 0 || getPatrolRight() != 0;
        owner.setMovementSpeedMax(getMoveSpeed() / 5.0);

        // Set side
        if (firstMove == 0)
        {
            side = Patroller.MOVE_LEFT;
        }
        else if (firstMove == 1)
        {
            side = Patroller.MOVE_RIGHT;
        }

        // Set position interval
        if (movementType == TypePatrol.HORIZONTAL)
        {
            posMin = owner.getLocationIntX() - getPatrolLeft() * Map.TILE_WIDTH;
            posMax = owner.getLocationIntX() + (getPatrolRight() - 1) * Map.TILE_WIDTH;
            owner.setMovementForce(owner.getMovementSpeedMax() * side, 0.0);
            if (side == Patroller.MOVE_LEFT)
            {
                owner.mirror(true);
                owner.teleport(posMax - 1, owner.getLocationIntY());
            }
            else if (side == Patroller.MOVE_RIGHT)
            {
                owner.teleport(posMin + 1, owner.getLocationIntY());
            }
        }
        else if (movementType == TypePatrol.VERTICAL)
        {
            posMin = owner.getLocationIntY() - getPatrolLeft() * Map.TILE_WIDTH;
            posMax = owner.getLocationIntY() + getPatrolRight() * Map.TILE_WIDTH;
            owner.setMovementForce(0.0, owner.getMovementSpeedMax() * side);
            if (side == Patroller.MOVE_LEFT)
            {
                owner.mirror(true);
                owner.teleport(owner.getLocationIntX(), posMax - 1);
            }
            else if (side == Patroller.MOVE_RIGHT)
            {
                owner.teleport(owner.getLocationIntX(), posMin + 1);
            }
        }
        side = -side;

        // Move straight on
        if (posMin == posMax)
        {
            posMin = -1;
            posMax = Integer.MAX_VALUE;
        }
    }

    @Override
    public void save(FileWriting file) throws IOException
    {
        file.writeByte((byte) movementType.getIndex());
        if (movementType != TypePatrol.NONE)
        {
            file.writeByte((byte) getMoveSpeed());
            file.writeByte((byte) getFirstMove());
            file.writeByte((byte) getPatrolLeft());
            file.writeByte((byte) getPatrolRight());
        }
    }

    @Override
    public void load(FileReading file) throws IOException
    {
        setPatrolType(TypePatrol.get(file.readByte()));
        final TypePatrol movementType = getPatrolType();
        if (movementType != TypePatrol.NONE)
        {
            setMoveSpeed(file.readByte());
            setFirstMove(file.readByte());
            setPatrolLeft(file.readByte());
            setPatrolRight(file.readByte());
        }
    }

    @Override
    public void setSide(int side)
    {
        this.side = side;
    }

    @Override
    public void enableMovement(TypePatrol type)
    {
        enableMovement.add(type);
    }

    @Override
    public void setPatrolType(TypePatrol movement)
    {
        movementType = movement;
    }

    @Override
    public void setFirstMove(int firstMove)
    {
        this.firstMove = firstMove;
    }

    @Override
    public void setMoveSpeed(int speed)
    {
        moveSpeed = speed;
    }

    @Override
    public void setPatrolLeft(int left)
    {
        patrolLeft = left;
    }

    @Override
    public void setPatrolRight(int right)
    {
        patrolRight = right;
    }

    @Override
    public int getSide()
    {
        return side;
    }

    @Override
    public TypePatrol getPatrolType()
    {
        return movementType;
    }

    @Override
    public int getFirstMove()
    {
        return firstMove;
    }

    @Override
    public int getMoveSpeed()
    {
        return moveSpeed;
    }

    @Override
    public int getPatrolLeft()
    {
        return patrolLeft;
    }

    @Override
    public int getPatrolRight()
    {
        return patrolRight;
    }

    @Override
    public int getPositionMin()
    {
        return posMin;
    }

    @Override
    public int getPositionMax()
    {
        return posMax;
    }

    @Override
    public boolean hasPatrol()
    {
        return hasPatrol;
    }

    @Override
    public boolean isPatrolEnabled()
    {
        return movementType != TypePatrol.NONE;
    }

    @Override
    public boolean isPatrolEnabled(TypePatrol type)
    {
        return enableMovement.contains(type);
    }
}
