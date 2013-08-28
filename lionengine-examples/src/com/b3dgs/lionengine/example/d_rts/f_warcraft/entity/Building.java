package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity;

import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.HandlerEffect;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.effect.Effect;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.effect.TypeEffect;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;

/**
 * Abstract building entity implementation.
 */
public abstract class Building
        extends Entity
{
    /** Die states. */
    private static enum Destroy
    {
        /** No death. */
        NONE,
        /** Burning low. */
        BURNING_LOW,
        /** Burning high. */
        BURNING_HIGH,
        /** Exploding. */
        EXPLODING;
    }

    /** Handler effect. */
    private final HandlerEffect handlerEffect;
    /** Burning animation. */
    private final Effect burning;
    /** Explode animation surface. */
    private final Effect explode;
    /** Burning low animation. */
    private final Animation animBurningLow;
    /** Burning high animation. */
    private final Animation animBurningHigh;
    /** Explode animation. */
    private final Animation animExplode;
    /** Destroy flag. */
    private Destroy destroy;

    /**
     * Constructor.
     * 
     * @param id The entity type enum.
     * @param context The context reference.
     */
    protected Building(TypeEntity id, Context context)
    {
        super(id, context);
        setLayer(0);
        setFrame(2);
        handlerEffect = context.handlerEffect;
        burning = context.factoryEffect.createEffect(TypeEffect.BURNING);
        explode = context.factoryEffect.createEffect(TypeEffect.EXPLODE);
        animBurningLow = Anim.createAnimation(1, 4, 0.125, false, true);
        animBurningHigh = Anim.createAnimation(5, 8, 0.125, false, true);
        animExplode = Anim.createAnimation(1, 18, 0.125, false, false);
        destroy = Destroy.NONE;
    }

    /**
     * Start burning low effect.
     * 
     * @param current The current destroy status.
     * @param anim The animation to play.
     */
    private void burning(Destroy current, Animation anim)
    {
        if (Destroy.NONE == destroy)
        {
            final int x = getLocationIntX() + getWidth() / 2 - 6;
            final int y = getLocationIntY() + getHeight() / 2 - 4;
            burning.start(x, y);
            handlerEffect.add(burning);
        }
        burning.play(anim);
        destroy = current;
    }

    /**
     * Start explode effect.
     */
    private void explode()
    {
        final int x = getLocationIntX() + getWidth() / 2 - explode.getWidth() / 2;
        final int y = getLocationIntY();

        handlerEffect.remove(burning);
        explode.start(x, y);
        explode.play(animExplode);
        handlerEffect.add(explode);
        destroy = Destroy.EXPLODING;
    }

    /*
     * Entity
     */

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        if (isDead())
        {
            if (explode.getFrame() > 7)
            {
                setVisible(false);
            }
        }
    }

    @Override
    public void stop()
    {
        // Nothing to do
    }

    @Override
    public void decreaseLife(int damages)
    {
        super.decreaseLife(damages);

        if (isDead())
        {
            explode();
        }
        else if (getLifePercent() > 20 && getLifePercent() < 50 && Destroy.NONE == destroy)
        {
            burning(Destroy.BURNING_LOW, animBurningLow);
        }
        else if (getLifePercent() <= 20 && (Destroy.NONE == destroy || Destroy.BURNING_LOW == destroy))
        {
            burning(Destroy.BURNING_HIGH, animBurningHigh);
        }
    }
}
