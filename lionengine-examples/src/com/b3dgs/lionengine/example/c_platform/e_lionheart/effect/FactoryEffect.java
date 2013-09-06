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
    /** Handler effect reference. */
    private final HandlerEffect handlerEffect;

    /**
     * Constructor.
     * 
     * @param handlerEffect The handler effect reference.
     */
    public FactoryEffect(HandlerEffect handlerEffect)
    {
        super(TypeEffect.class);
        this.handlerEffect = handlerEffect;
        loadAll(TypeEffect.values());
    }

    /**
     * Start an effect an the specified location.
     * 
     * @param id The effect id.
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void startEffect(TypeEffect id, int x, int y)
    {
        final Effect effect = createEffect(id);
        effect.start(x, y);
        handlerEffect.add(effect);
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
            case EXPLODE:
                return new ExplodeBig(getSetup(TypeEffect.EXPLODE));
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
