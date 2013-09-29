package com.b3dgs.lionengine.example.e_shmup.c_tyrian.effect;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.effect.FactoryEffectGame;

/**
 * Factory effect implementation.
 */
public final class FactoryEffect
        extends FactoryEffectGame<EffectType, SetupSurfaceGame, Effect>
{
    /**
     * Constructor.
     */
    public FactoryEffect()
    {
        super(EffectType.class);
        loadAll(EffectType.values());
    }

    /*
     * FactoryEffectGame
     */
    
    @Override
    public Effect createEffect(EffectType id)
    {
        switch (id)
        {
            case SMOKE:
                return new Smoke(getSetup(id));
            default:
                throw new LionEngineException("Unknown effect: " + id);
        }
    }

    @Override
    protected SetupSurfaceGame createSetup(EffectType id)
    {
        return new SetupSurfaceGame(Media.get("effects", id.toString() + ".xml"));
    }
}
