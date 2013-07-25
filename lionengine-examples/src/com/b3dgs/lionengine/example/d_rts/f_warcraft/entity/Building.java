package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity;

import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.ResourcesLoader;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.game.HandlerEffect;

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
    private final SpriteAnimated burning;
    /** Explode animation surface. */
    private final SpriteAnimated explode;
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
        burning = ResourcesLoader.BURNING.instanciate();
        explode = ResourcesLoader.EXPLODE.instanciate();
        animBurningLow = Anim.createAnimation(1, 4, 0.125, false, true);
        animBurningHigh = Anim.createAnimation(5, 8, 0.125, false, true);
        animExplode = Anim.createAnimation(1, 18, 0.125, false, false);
        destroy = Destroy.NONE;
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        if (isDead())
        {
            if (AnimState.FINISHED == explode.getAnimState())
            {
                handlerEffect.remove(explode);
                destroy();
            }
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
            handlerEffect.add(x, y, burning);
        }
        burning.play(anim);
        destroy = current;
    }

    /**
     * Start explode effect.
     */
    private void explode()
    {
        final int x = getLocationIntX() + getWidth() / 2 - explode.getFrameWidth() / 2;
        final int y = getLocationIntY();

        handlerEffect.remove(burning);
        handlerEffect.add(x, y, explode);
        explode.play(animExplode);
        destroy = Destroy.EXPLODING;
    }
}
