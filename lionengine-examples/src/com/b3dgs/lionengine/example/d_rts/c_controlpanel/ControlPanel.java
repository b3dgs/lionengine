package com.b3dgs.lionengine.example.d_rts.c_controlpanel;

import java.util.Set;

import com.b3dgs.lionengine.game.rts.ControlPanelModel;

/**
 * Control panel implementation.
 */
final class ControlPanel
        extends ControlPanelModel<Entity>
{
    /**
     * Constructor.
     */
    ControlPanel()
    {
        super();
    }

    /*
     * ControlPanelModel
     */

    @Override
    public void notifyUpdatedSelection(Set<Entity> selection)
    {
        // Nothing to do
    }

    @Override
    protected void onStartOrder()
    {
        // Nothing to do
    }

    @Override
    protected void onTerminateOrder()
    {
        // Nothing to do
    }
}
