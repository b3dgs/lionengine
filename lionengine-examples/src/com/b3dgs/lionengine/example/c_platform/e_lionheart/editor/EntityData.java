package com.b3dgs.lionengine.example.c_platform.e_lionheart.editor;

import java.io.IOException;

import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;

public class EntityData
{
    public static final int NONE_MOV = 0;
    public static final int HORI_MOV = 1;
    public static final int VERT_MOV = 2;
    public static final int ROTA_MOV = 3;
    protected boolean over;
    protected boolean selected;
    private int movement;
    private int firstMove;
    private int moveSpeed;
    private int patrolLeft;
    private int patrolRight;

    public EntityData()
    {
        movement = EntityData.NONE_MOV;
        moveSpeed = 10;
        patrolLeft = 0;
        patrolRight = 0;
    }

    public void setOver(boolean over)
    {
        this.over = over;
    }

    public boolean isOver()
    {
        return over;
    }

    public void setSelection(boolean selected)
    {
        this.selected = selected;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setMovement(int movement)
    {
        this.movement = movement;
    }

    public final int getMovement()
    {
        return movement;
    }

    public void setFirstMove(int firstMove)
    {
        this.firstMove = firstMove;
    }

    public int getFirstMove()
    {
        return firstMove;
    }

    public void setMoveSpeed(int speed)
    {
        moveSpeed = speed;
    }

    public int getMoveSpeed()
    {
        return moveSpeed;
    }

    public void setPatrolLeft(int left)
    {
        patrolLeft = left;
    }

    public void setPatrolRight(int right)
    {
        patrolRight = right;
    }

    public int getPatrolLeft()
    {
        return patrolLeft;
    }

    public int getPatrolRight()
    {
        return patrolRight;
    }

    public void save(FileWriting file) throws IOException
    {
        file.writeByte((byte) getMovement());
        if (movement != EntityData.NONE_MOV)
        {
            file.writeByte((byte) getMoveSpeed());
            file.writeByte((byte) getFirstMove());
            file.writeByte((byte) getPatrolLeft());
            file.writeByte((byte) getPatrolRight());
        }
    }

    public void load(FileReading file) throws IOException
    {
        setMovement(file.readByte());
        if (movement != EntityData.NONE_MOV)
        {
            setMoveSpeed(file.readByte());
            setFirstMove(file.readByte());
            setPatrolLeft(file.readByte());
            setPatrolRight(file.readByte());
        }
    }
}
