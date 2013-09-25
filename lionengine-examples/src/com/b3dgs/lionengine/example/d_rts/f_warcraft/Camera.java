package com.b3dgs.lionengine.example.d_rts.f_warcraft;

import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.input.Keyboard;

/**
 * Camera implementation.
 */
public class Camera
        extends CameraRts
{
    /** The control panel reference. */
    private final ControlPanel panel;

    /**
     * Constructor.
     * 
     * @param map The map reference.
     * @param panel The control panel reference.
     */
    public Camera(MapTile<?, ?> map, ControlPanel panel)
    {
        super(map);
        this.panel = panel;
    }

    /*
     * CameraRts
     */

    @Override
    public void update(Keyboard keyboard)
    {
        if (!panel.isSelecting())
        {
            super.update(keyboard);
        }
    }
}
