package com.b3dgs.lionengine.example.d_rts.f_warcraft;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.effect.Effect;
import com.b3dgs.lionengine.game.effect.HandlerEffectGame;
import com.b3dgs.lionengine.game.rts.CameraRts;

/**
 * Handle the effect.
 */
public class HandlerEffect
        extends HandlerEffectGame<Effect>
{
    /**
     * Constructor.
     * 
     * @param camera The camera reference.
     */
    public HandlerEffect(CameraRts camera)
    {
        super(camera);
    }
}
