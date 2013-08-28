package com.b3dgs.lionengine.example.d_rts.f_warcraft.effect;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.effect.EffectGame;
import com.b3dgs.lionengine.game.effect.SetupEffectGame;

/**
 * Effect implementation.
 */
public class Effect
        extends EffectGame
{
    /** Sprite. */
    private final SpriteAnimated sprite;
    /** Horizontal location. */
    private int x;
    /** Vertical location. */
    private int y;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Effect(SetupEffectGame setup)
    {
        super(setup.configurable);
        final int horizontalFrames = getDataInteger("horizontal", "frames");
        final int verticalFrames = getDataInteger("vertical", "frames");
        sprite = Drawable.loadSpriteAnimated(setup.surface, horizontalFrames, verticalFrames);
    }

    /**
     * Start the effect.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void start(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Play the animation.
     * 
     * @param animation The animation to play.
     */
    public void play(Animation animation)
    {
        sprite.play(animation);
    }

    /**
     * Set the effect frame.
     * 
     * @param frame The frame.
     */
    public void setFrame(int frame)
    {
        sprite.setFrame(frame);
    }

    /**
     * Get the current frame.
     * 
     * @return The current frame.
     */
    public int getFrame()
    {
        return sprite.getFrame();
    }

    /**
     * Get the effect width.
     * 
     * @return The effect width.
     */
    public int getWidth()
    {
        return sprite.getFrameWidth();
    }

    /**
     * Get the effect height.
     * 
     * @return The effect height.
     */
    public int getHeight()
    {
        return sprite.getFrameHeight();
    }

    /*
     * EffectGame
     */

    @Override
    public void update(double extrp)
    {
        sprite.updateAnimation(extrp);
        if (sprite.getAnimState() == AnimState.FINISHED)
        {
            destroy();
        }
    }

    @Override
    public void render(Graphic g, CameraGame camera)
    {
        sprite.render(g, camera.getViewpointX(x), camera.getViewpointY(y + sprite.getFrameHeight()));
    }
}
