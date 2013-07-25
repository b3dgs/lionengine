package com.b3dgs.lionengine.game.entity;

/**
 * This exception is thrown when a entity is not found.
 */
public class EntityNotFoundException
        extends Exception
{
    /** Serial uid. */
    private static final long serialVersionUID = -1407285329398682159L;

    /**
     * Constructor.
     */
    public EntityNotFoundException()
    {
        super();
    }

    /**
     * Constructor.
     * 
     * @param message The message.
     */
    public EntityNotFoundException(String message)
    {
        super(message);
    }
}
