package com.b3dgs.lionengine;

/**
 * Utility class check.
 */
public final class Check
{
    /**
     * Private constructor.
     */
    private Check()
    {
        throw new RuntimeException();
    }

    /**
     * Check if the condition if <code>true</code>. Throws a {@link LionEngineException} if <code>false</code>.
     * 
     * @param condition The condition to check.
     * @param messages The messages to put in the exception.
     */
    public static void argument(boolean condition, String... messages)
    {
        if (!condition)
        {
            throw new LionEngineException(messages);
        }
    }

    /**
     * Check if the object is not <code>null</code>. Throws a {@link LionEngineException} if <code>null</code>.
     * 
     * @param object The object to check.
     * @param messages The messages to put in the exception.
     */
    public static void notNull(Object object, String... messages)
    {
        if (object == null)
        {
            throw new LionEngineException(messages);
        }
    }
}
