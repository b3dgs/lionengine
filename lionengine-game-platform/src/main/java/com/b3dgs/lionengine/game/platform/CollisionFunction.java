/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.platform;

/**
 * Describe the collision function used.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollisionFunction
{
    /** The input used. */
    private CollisionInput input;
    /** Operation. */
    private CollisionOperation operation;
    /** Value. */
    private int value;
    /** Offset operation. */
    private CollisionOperation operationOffset;
    /** Offset value. */
    private int offset;

    /**
     * Constructor.
     */
    public CollisionFunction()
    {
        input = CollisionInput.X;
        operation = CollisionOperation.MULTIPLY;
        value = 1;
        operationOffset = CollisionOperation.ADD;
        offset = 0;
    }

    /**
     * Set the input type used.
     * 
     * @param input The input to set
     */
    public void setInput(CollisionInput input)
    {
        this.input = input;
    }

    /**
     * Set the operation used.
     * 
     * @param operation The operation to set
     */
    public void setOperation(CollisionOperation operation)
    {
        this.operation = operation;
    }

    /**
     * Set the value.
     * 
     * @param value The value to set
     */
    public void setValue(int value)
    {
        this.value = value;
    }

    /**
     * Set the operation offset.
     * 
     * @param operationOffset The operationOffset to set
     */
    public void setOperationOffset(CollisionOperation operationOffset)
    {
        this.operationOffset = operationOffset;
    }

    /**
     * Set the offset value.
     * 
     * @param offset The offset to set
     */
    public void setOffset(int offset)
    {
        this.offset = offset;
    }

    /**
     * Get the input.
     * 
     * @return the input
     */
    public CollisionInput getInput()
    {
        return input;
    }

    /**
     * Get the operation.
     * 
     * @return the operation
     */
    public CollisionOperation getOperation()
    {
        return operation;
    }

    /**
     * Get the value.
     * 
     * @return the value
     */
    public int getValue()
    {
        return value;
    }

    /**
     * Get the operation offset.
     * 
     * @return the operationOffset
     */
    public CollisionOperation getOperationOffset()
    {
        return operationOffset;
    }

    /**
     * Get the offset.
     * 
     * @return the offset
     */
    public int getOffset()
    {
        return offset;
    }
}
