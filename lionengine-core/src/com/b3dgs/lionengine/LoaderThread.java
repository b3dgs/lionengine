package com.b3dgs.lionengine;

/**
 * Loader thread.
 */
final class LoaderThread
        extends Thread
{
    /** Loader reference. */
    private final Loader loader;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    LoaderThread(final Loader loader)
    {
        super(Engine.getProgramName());
        this.loader = loader;
    }

    @Override
    public void run()
    {
        loader.run();
    }
}
