package com.b3dgs.lionengine.example.d_rts.d_ability;

import java.util.Set;

import com.b3dgs.lionengine.example.d_rts.d_ability.entity.Entity;
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
