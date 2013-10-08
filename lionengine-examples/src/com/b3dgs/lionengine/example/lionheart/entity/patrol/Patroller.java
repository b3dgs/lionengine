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
package com.b3dgs.lionengine.example.lionheart.entity.patrol;

import java.io.IOException;

import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;

/**
 * Patroller interface, describing the ability of moving autonomously.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Patroller
{
    /** Move left side. */
    static final int MOVE_LEFT = -1;
    /** Move right side. */
    static final int MOVE_RIGHT = 1;

    /**
     * Save the patrol.
     * 
     * @param file The file output.
     * @throws IOException If error.
     */
    void save(FileWriting file) throws IOException;

    /**
     * Load the patrol.
     * 
     * @param file The file input.
     * @throws IOException If error.
     */
    void load(FileReading file) throws IOException;

    /**
     * Enable a movement.
     * 
     * @param type The movement to enable.
     */
    void enableMovement(Patrol type);

    /**
     * Set the movement side.
     * 
     * @param side The movement side.
     */
    void setSide(int side);

    /**
     * Set the movement type.
     * 
     * @param movement The movement type.
     */
    void setPatrolType(Patrol movement);

    /**
     * Set the first move.
     * 
     * @param firstMove The first move.
     */
    void setFirstMove(int firstMove);

    /**
     * Set the movement speed.
     * 
     * @param speed The movement speed.
     */
    void setMoveSpeed(int speed);

    /**
     * Set the left patrol.
     * 
     * @param left The left patrol.
     */
    void setPatrolLeft(int left);

    /**
     * Set the right patrol.
     * 
     * @param right The right patrol.
     */
    void setPatrolRight(int right);

    /**
     * Get the movement side.
     * 
     * @return The movement side.
     */
    int getSide();

    /**
     * Get the movement type.
     * 
     * @return The movement type.
     */
    Patrol getPatrolType();

    /**
     * Get the first move.
     * 
     * @return The first move.
     */
    int getFirstMove();

    /**
     * Get the movement speed.
     * 
     * @return The movement speed.
     */
    int getMoveSpeed();

    /**
     * Get the left patrol.
     * 
     * @return The left patrol.
     */
    int getPatrolLeft();

    /**
     * Get the right patrol.
     * 
     * @return The right patrol.
     */
    int getPatrolRight();

    /**
     * Get the minimum reachable position.
     * 
     * @return The minimum reachable position.
     */
    int getPositionMin();

    /**
     * Get the maximum reachable position.
     * 
     * @return The maximum reachable position.
     */
    int getPositionMax();

    /**
     * Check if entity have patrol
     * 
     * @return <code>true</code> if have patrol, <code>false</code> else.
     */
    boolean hasPatrol();

    /**
     * Check if patrol is enabled.
     * 
     * @return <code>true</code> if patrol enabled, <code>false</code> else.
     */
    boolean isPatrolEnabled();

    /**
     * Check if patrol type is enabled.
     * 
     * @param type The movement type.
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    boolean isPatrolEnabled(Patrol type);
}
