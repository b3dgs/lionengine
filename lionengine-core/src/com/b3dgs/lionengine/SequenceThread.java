package com.b3dgs.lionengine;

/**
 * Thread sequence.
 */
final class SequenceThread
        extends Thread
{
    /** Sequence reference. */
    private final Sequence sequence;

    /**
     * Constructor.
     * 
     * @param sequence The sequence reference.
     */
    SequenceThread(final Sequence sequence)
    {
        this.sequence = sequence;
    }

    @Override
    public void run()
    {
        sequence.run();
    }
}
