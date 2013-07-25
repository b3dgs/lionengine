package com.b3dgs.lionengine.example.d_rts.c_controlpanel;

/**
 * Context container.
 */
final class Context
{
    /** The map reference. */
    final Map map;
    /** The factory reference. */
    final FactoryEntity factory;

    /**
     * Constructor.
     * 
     * @param map The map reference.
     * @param factory The factory reference.
     */
    Context(Map map, FactoryEntity factory)
    {
        this.map = map;
        this.factory = factory;
    }
}
