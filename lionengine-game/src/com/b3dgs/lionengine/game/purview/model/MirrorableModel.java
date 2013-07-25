package com.b3dgs.lionengine.game.purview.model;

import com.b3dgs.lionengine.game.purview.Mirrorable;

/**
 * Default mirrorable implementation.
 */
public class MirrorableModel
        implements Mirrorable
{
    /** Mirror state. */
    private boolean mirror;
    /** Mirror requested flag. */
    private boolean requested;
    /** Mirror next state flag. */
    private boolean nextState;
    /** Mirror cancel flags. */
    private boolean cancel;

    /**
     * Create a mirrorable model.
     */
    public MirrorableModel()
    {
        mirror = false;
        requested = false;
        nextState = false;
        cancel = false;
    }

    /*
     * MirrorableModel
     */

    @Override
    public void mirror(boolean state)
    {
        requested = true;
        nextState = state;
    }

    @Override
    public void updateMirror()
    {
        if (requested)
        {
            if (!cancel)
            {
                mirror = nextState;
                requested = false;
            }
            else
            {
                cancel = false;
            }
        }
    }

    @Override
    public void setMirrorCancel(boolean state)
    {
        cancel = state;
    }

    @Override
    public boolean getMirrorCancel()
    {
        return cancel;
    }

    @Override
    public boolean getMirror()
    {
        return mirror;
    }
}
