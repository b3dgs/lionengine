package com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.effect.Effect;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.effect.EffectType;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.effect.FactoryEffect;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.effect.HandlerEffect;
import com.b3dgs.lionengine.game.SetupSurfaceGame;

/**
 * Missile projectile.
 */
final class Missile
        extends Projectile
{
    /** Factory effect. */
    private final FactoryEffect factoryEffect;
    /** Handler effect. */
    private final HandlerEffect handlerEffect;
    /** Effect timer. */
    private final Timing timerEffect;

    /**
     * Constructor.
     * 
     * @param factoryEffect The factory effect.
     * @param handlerEffect The handler effect.
     * @param setup The setup reference.
     * @param id The id.
     * @param frame The frame.
     */
    public Missile(FactoryEffect factoryEffect, HandlerEffect handlerEffect, SetupSurfaceGame setup, int id, int frame)
    {
        super(setup, id, frame);
        this.factoryEffect = factoryEffect;
        this.handlerEffect = handlerEffect;
        timerEffect = new Timing();
    }

    /**
     * Add smoke effect.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void addEffect(int x, int y)
    {
        final Effect effect = factoryEffect.createEffect(EffectType.SMOKE);
        effect.start(x, y);
        handlerEffect.add(effect);
    }

    /*
     * Projectile
     */

    @Override
    public void start(int x, int y, double vecX, double vecY)
    {
        super.start(x, y, vecX, vecY);
        addEffect(getLocationIntX(), getLocationIntY());
        timerEffect.start();
    }

    @Override
    protected void updateMovement(double extrp, double vecX, double vecY)
    {
        super.updateMovement(extrp, vecX, vecY);
        if (timerEffect.elapsed(40))
        {
            addEffect(getLocationIntX(), getLocationIntY());
            timerEffect.stop();
            timerEffect.start();
        }
    }
}
