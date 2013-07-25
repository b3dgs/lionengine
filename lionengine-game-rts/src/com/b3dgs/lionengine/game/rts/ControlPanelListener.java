package com.b3dgs.lionengine.game.rts;

import java.awt.geom.Rectangle2D;

/**
 * List of events linked to the control panel.
 */
public interface ControlPanelListener
{
    /**
     * Notify when selection started.
     * 
     * @param selection The selection.
     */
    void notifySelectionStarted(Rectangle2D selection);

    /**
     * Notify when selection is done.
     * 
     * @param selection The selection.
     */
    void notifySelectionDone(Rectangle2D selection);
}
