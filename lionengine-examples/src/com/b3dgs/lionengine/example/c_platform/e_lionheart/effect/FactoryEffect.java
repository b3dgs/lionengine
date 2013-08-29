package com.b3dgs.lionengine.example.c_platform.e_lionheart.effect;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.AppLionheart;
import com.b3dgs.lionengine.game.effect.FactoryEffectGame;
import com.b3dgs.lionengine.game.effect.SetupEffectGame;

/**
 * Factory effect implementation.
 */
public class FactoryEffect
        extends FactoryEffectGame<TypeEffect, SetupEffectGame, Effect>
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
            case TAKEN:
                return new Taken(getSetup(TypeEffect.TAKEN));
            default:
                throw new LionEngineException("Unknown id: " + id);
        }
    }

    @Override
    protected SetupEffectGame createSetup(TypeEffect id)
    {
        return new SetupEffectGame(Media.get(AppLionheart.EFFECTS_DIR, id.toString()
                + AppLionheart.CONFIG_FILE_EXTENSION));
    }
}
