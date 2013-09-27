package com.b3dgs.lionengine.example.d_rts.f_warcraft.effect;

import java.util.Locale;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.AppWarcraft;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.effect.FactoryEffectGame;

/**
 * Factory effect implementation.
 */
public class FactoryEffect
        extends FactoryEffectGame<TypeEffect, SetupSurfaceGame, Effect>
{
    /**
     * Constructor.
     */
    public FactoryEffect()
    {
        super(TypeEffect.class);
        loadAll(TypeEffect.values());
    }

    /*
     * FactoryEffectGame
     */

    @Override
    public Effect createEffect(TypeEffect id)
    {
        switch (id)
        {
            case CONSTRUCTION:
                return new Construction(getSetup(TypeEffect.CONSTRUCTION));
            case BURNING:
                return new Burning(getSetup(TypeEffect.BURNING));
            case EXPLODE:
                return new Explode(getSetup(TypeEffect.EXPLODE));
            default:
                throw new LionEngineException("Unknown id: " + id);
        }
    }

    @Override
    protected SetupSurfaceGame createSetup(TypeEffect id)
    {
        return new SetupSurfaceGame(Media.get(AppWarcraft.EFFECTS_DIR, id.name().toLowerCase(Locale.ENGLISH)
                + AppWarcraft.CONFIG_FILE_EXTENSION));
    }
}
