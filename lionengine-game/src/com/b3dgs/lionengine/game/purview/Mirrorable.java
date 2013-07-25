package com.b3dgs.lionengine.game.purview;

/**
 * Purview representing an object with the ability of being mirrored. Mainly used with sprites (horizontal axis).
 */
public interface Mirrorable
{
    /**
     * Set the next mirror state and apply it on next turn.
     * 
     * @param state The next mirror state.
     */
    void mirror(boolean state);

    /**
     * Update mirror and apply it if necessary.
     */
    void updateMirror();

    /**
     * Set cancel state for the mirror operation.
     * 
     * @param state The state.
     */
    void setMirrorCancel(boolean state);

    /**
     * Get mirror cancel state.
     * 
     * @return The mirror cancel state.
     */
    boolean getMirrorCancel();

    /**
     * Get current mirror state.
     * 
     * @return The current mirror state.
     */
    boolean getMirror();
}
