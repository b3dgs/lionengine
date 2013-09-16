package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol;

import java.io.IOException;

import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;

/**
 * Patroller interface, describing the ability of moving autonomously.
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
    void enableMovement(TypePatrol type);

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
    void setPatrolType(TypePatrol movement);

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
    TypePatrol getPatrolType();

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
    boolean isPatrolEnabled(TypePatrol type);
}
